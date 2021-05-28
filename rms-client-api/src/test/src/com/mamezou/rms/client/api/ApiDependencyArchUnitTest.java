package com.mamezou.rms.client.api;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.mamezou.rms", importOptions = ImportOption.DoNotIncludeTests.class)
public class ApiDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // apiパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * apiパッケージ内のアプリのコードで依存OKなライブラリの定義。依存してよいのは以下のモノのみ
     * <pre>
     * ・アプリ自身のクラス(com.mamezou.rms..)
     * ・Apache Commons Lang(org.apache.commons.lang3..)
     * ・SLF4J(org.slf4j..)
     * ・JavaSE API(java..)
     * </pre>
     */
    @ArchTest
    static final ArchRule test_apiパッケージが依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("com.mamezou.rms.client.api")
                    .or().resideInAPackage("com.mamezou.rms.client.api.dto..")
                    .or().resideInAPackage("com.mamezou.rms.client.api.exception..")
                    .or().resideInAPackage("com.mamezou.rms.client.api.login..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "com.mamezou.rms..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );


    /**
     * 実装パッケージへの依存がないことの定義
     * <pre>
     * ・apiパッケージはlocal or remote実装に依存してないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_実装パッケージへの依存がないことの定義 =
            noClasses()
                .that()
                    .resideInAPackage("com.mamezou.rms.client.api")
                    .or().resideInAPackage("com.mamezou.rms.client.api.dto..")
                    .or().resideInAPackage("com.mamezou.rms.client.api.exception..")
                    .or().resideInAPackage("com.mamezou.rms.client.api.login..")
                .should()
                    .dependOnClassesThat()
                        .resideInAnyPackage(
                                "com.mamezou.rms.client.api.local..",
                                "com.mamezou.rms.client.api.remote.."
                                );
}
