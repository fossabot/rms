package com.mamezou.rms.client.console;

import static com.mamezou.rms.client.console.ui.ClientConstants.*;

import java.util.logging.LogManager;

import javax.enterprise.inject.spi.CDI;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.mamezou.rms.client.console.ui.ScreenController;
import com.mamezou.rms.client.console.ui.textio.TextIoUtils;
import com.mamezou.rms.platform.env.Environment;
import com.mamezou.rms.platform.env.MainJarInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleMain {

    public static void main(String[] args) throws Exception {

        // Fiddlerの設定
//        System.setProperty("http.proxyHost", "localhost");
//        System.setProperty("http.proxyPort", "8888");
//        System.setProperty("web-api/mp-rest/url", "http://pmr216n.primo.mamezou.com:7001");
//        System.err.println("★Fiddlerの設定が入ってるのでFiddler立ち上げてね！");
//        System.err.println("★あと宛先アドレスはlocalhostではなくFQCN指定の方に変えてね！");

        // java.util.loggingの出力をSLF4Jへdelegate
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // CDIコンテナの起動
        io.helidon.microprofile.cdi.Main.main(args);

        // Startup Logging
        startupLog();

        ScreenController controller = CDI.current().select(ScreenController.class).get();
        while (true) {
            try {
                controller.start(args); // 処理開始
            } catch (Exception e) {
                log.error("Back to start..", e);
                TextIoUtils.printErrorInformation(UNKNOWN_ERROR_INFORMATION);
            }
        }
    }

    private static void startupLog() {
        MainJarInfo mainJarInfo = Environment.getMainJarInfo();
        log.info(System.lineSeparator() +
                "Startup-Module:" + mainJarInfo.startupModuleInfo() + System.lineSeparator() +
                "Version:" + mainJarInfo.getVersion() + System.lineSeparator() +
                "Build-Time:" + mainJarInfo.getBuildtimeInfo()
                );
    }
}
