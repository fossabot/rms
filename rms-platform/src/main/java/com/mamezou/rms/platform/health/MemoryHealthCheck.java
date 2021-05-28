package com.mamezou.rms.platform.health;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.function.BiFunction;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class MemoryHealthCheck {

    private String livenessName;
    private String readinessName;
    private MemoryLivenessEvaluator evaluator;
    private MemoryMXBean mbean;

    @Inject
    public MemoryHealthCheck(
            @ConfigProperty(name="healthCheck.memoryLiveness.name") String livenessName,
            @ConfigProperty(name="healthCheck.memoryReadiness.name") String readinessName,
            @ConfigProperty(name="healthCheck.memoryLiveness.method") String defaultMethod,
            @ConfigProperty(name="healthCheck.memoryLiveness.threshold") long defaultThreshold
        ) {
        this.livenessName = livenessName;
        this.readinessName = readinessName;
        this.evaluator = MemoryLivenessEvaluator.of(defaultMethod, defaultThreshold);
        this.mbean = ManagementFactory.getMemoryMXBean();
    }

    @Produces
    @Liveness
    public HealthCheck checkLivenss() {
        return () -> {
            MemoryUsage memoryUsage = mbean.getHeapMemoryUsage();
            log.info("MemoryUsage:" + memoryUsage);
            return  HealthCheckResponse
                .named(livenessName)
                .withData("init", memoryUsage.getInit() / (1024 * 1024)) // MByte
                .withData("used", memoryUsage.getUsed() / (1024 * 1024)) // MByte
                .withData("max", memoryUsage.getMax() / (1024 * 1024))   // MByte
                .withData("method", evaluator.name())
                .withData("threshold", evaluator.threshold())
                .state(evaluator.liveness(memoryUsage))
                .build();
        };
    }

    @Produces
    @Readiness
    public HealthCheck checkReadiness() {
        // since no memory viewpoint, unconditionally returns up.
        return () -> HealthCheckResponse.named(readinessName).up().build();
    }

    // ----------------------------------------------------- observe method

    void resetEvaluator(@Observes MemoryLivenessEvaluator evaluator) {
        log.info("recieve event. event=" + evaluator);
        this.evaluator = evaluator;
    }


    // ----------------------------------------------------- inner classes

    interface MemoryLivenessEvaluator {

        // メモリ使用量が閾値以下であることの評価
        static final BiFunction<MemoryUsage, Long, Boolean> absoluteEvalute =
                (memoryUsage, threshold) -> memoryUsage.getUsed() < threshold * 1024 * 1024;
        // メモリ使用率が閾値以下であることの評価
        static final BiFunction<MemoryUsage, Long, Boolean> relativeEvalute =
                (memoryUsage, threshold) -> (memoryUsage.getUsed() / (double) memoryUsage.getMax()) * 100 < (double) threshold;

        String name();
        long threshold();
        boolean liveness(MemoryUsage memoryUsage);

        static MemoryLivenessEvaluator of(String method, long threshold) {
            switch (method) {
            case "abs":
                return new EvaluateHolder("abs", absoluteEvalute, threshold);
            case "rel":
            default:
                return new EvaluateHolder("rel", relativeEvalute, threshold);
            }
        }
    }

    static class EvaluateHolder implements MemoryLivenessEvaluator {

        String name;
        BiFunction<MemoryUsage, Long, Boolean> eval;
        long threshold;

        EvaluateHolder(String name, BiFunction<MemoryUsage, Long, Boolean> eval, long threshold) {
            this.name = name;
            this.eval = eval;
            this.threshold = threshold;
        }

        @Override
        public String name() {
            return name;
        }
        @Override
        public long threshold() {
            return threshold;
        }
        @Override
        public boolean liveness(MemoryUsage memoryUsage) {
            return eval.apply(memoryUsage, threshold);
        }
    }
}