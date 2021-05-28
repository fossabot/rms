package com.mamezou.rms.client.api.remote;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.mamezou.rms", importOptions = ImportOption.DoNotIncludeTests.class)
public class RemoteApiDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // api.remoteパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * api.remoteパッケージ内のアプリのコードで依存OKなライブラリの定義。依存してよいのは以下のモノのみ
     * <pre>
     * ・アプリ自身のクラス(com.mamezou.rms..)
     * ・Apache Commons Lang(org.apache.commons.lang3..)
     * ・SLF4J(org.slf4j..)
     * ・MicroProfile RestClient
     * ・CDI(javax..)
     * ・JAX-RS(javax..)
     * ・JavaSE API(java..)
     * </pre>
     */
    @ArchTest
    static final ArchRule test_アプリが依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("com.mamezou.rms.client.api.remote..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "com.mamezou.rms..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile.rest.client..",
                                "javax.annotation..",
                                "javax.inject..",
                                "javax.enterprise.inject..",
                                "javax.enterprise.context..",
                                "javax.enterprise.event..",
                                "javax.ws.rs..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );
}
