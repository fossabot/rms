package com.mamezou.rms.client.console;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.mamezou.rms", importOptions = ImportOption.DoNotIncludeTests.class)
public class LayerDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // アプリケーションアーキテクチャ全体レベルの依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * 論理モジュールの定義
     * <pre>
     * ・clientモジュールはアプリケーション(core)を操作/実行するためのモジュールの集合
     * ・coreモジュールはアプリケーションそのものを司るモジュールの集合でクライアントの方式に依らないもの
     * ・externalモジュールはアプリケーションを外部に公開/連携するためのモジュールの集合
     * ・platformモジュールは業務に依らないキーメカニズムの集合
     * </pre>
     * 依存関係の定義
     * <pre>
     * ・clientモジュールはどのモジュールからも依存されていないこと
     * ・externalモジュールはどのモジュールからも依存されていないこと
     * ・coreモジュールはclientモジュールとexternalモジュールから依存されていること
     * ・platformモジュールはclient、external、coreの3つのモジュールから依存されていること
     * </pre>
     */
    @ArchTest
    static final ArchRule test_論理モジュール間の依存関係の定義 = layeredArchitecture()
            .layer("client").definedBy("com.mamezou.rms.client..")
            .layer("external").definedBy("com.mamezou.rms.external..")
            .layer("core").definedBy("com.mamezou.rms.core..")
            .layer("platform").definedBy("com.mamezou.rms.platform..")

            .whereLayer("client").mayNotBeAccessedByAnyLayer()
            .whereLayer("external").mayNotBeAccessedByAnyLayer()
            .whereLayer("core").mayOnlyBeAccessedByLayers("client", "external")
            .whereLayer("platform").mayOnlyBeAccessedByLayers("client", "external", "core");

    /**
     * アプリケーションアーキテクチャのレイヤと依存関係の定義
     * <pre>
     * ・uiレイヤはどのレイヤからも依存されていないこと（uiレイヤは誰も使ってはダメ）
     * ・apiレイヤはuiレイヤからのみ依存から依存を許可（uiレイヤ以外は誰も使ってはダメ）
     * ・webapiレイヤはどのレイヤからも依存されていないこと（webapiレイヤは誰も使ってはダメ））
     * ・applicationレイヤはapiとwebapiレイヤからのみ依存を許可（applicationレイヤを使って良いのはapiとwebapiレイヤのみ）
     * ・serviceレイヤapplicaitonレイヤからのみ依存を許可（serviceレイヤを使って良いのはapplicationレイヤのみ）
     * ・persistenceレイヤはserviceレイヤからのみ依存を許可（persistenceレイヤを使って良いのはserviceレイヤのみ）
     * ・domianレイヤはuiとplatform以外のレイヤからのみ依存されていること（uiレイヤはapiレイヤを経由してapplicationを使うので直接使ってはダメ）
     * ・platformレイヤはすべてのレイヤから依存されていること
     * </pre>
     */
    @ArchTest
    static final ArchRule test_レイヤー間の依存関係の定義 = layeredArchitecture()
            .layer("ui").definedBy("com.mamezou.rms.client.console..")
            .layer("api").definedBy("com.mamezou.rms.client.api..")
            .layer("webapi").definedBy("com.mamezou.rms.external.webapi..")
            .layer("application").definedBy("com.mamezou.rms.core")
            .layer("service").definedBy("com.mamezou.rms.core.service..")
            .layer("persistence").definedBy("com.mamezou.rms.core.persistence..")
            .layer("domain").definedBy("com.mamezou.rms.core.domain..", "com.mamezou.rms.core.exception..")
            .layer("platform").definedBy("com.mamezou.rms.platform..")

            .whereLayer("ui").mayNotBeAccessedByAnyLayer()
            .whereLayer("api").mayOnlyBeAccessedByLayers("ui")
            .whereLayer("webapi").mayNotBeAccessedByAnyLayer()
            .whereLayer("application").mayOnlyBeAccessedByLayers("api", "webapi")
            .whereLayer("service").mayOnlyBeAccessedByLayers("application")
            .whereLayer("persistence").mayOnlyBeAccessedByLayers("service")
            .whereLayer("domain").mayOnlyBeAccessedByLayers("api", "webapi", "application", "service", "persistence")
            .whereLayer("platform").mayOnlyBeAccessedByLayers("ui", "api", "webapi", "application", "service", "persistence", "domain");

    /**
     * 物理モジュール(jar)間の依存関係の定義
     * <pre>
     * ・-ui-console.jarはどのjarも依存しないこと
     * ・-api.jarに依存してよいのは直接利用する-ui-console.jarと-api.jarを実現する-api-remote.jarと-api-local.jarの3つのみ
     * ・-api-local.jarと-api-remote.jarはどのjarも依存しないこと
     * ・-serevice-server.jarはどのjarも依存しないこと
     * ・-serevice.jarに依存してよいのは直接利用する-serevice-server.jarと-api-local.jarの2つのみ
     * ・-platform.jarはすべてのjarから依存されて良い
     * </pre>
     */
    @ArchTest
    static final ArchRule test_物理モジュール間の定義 = layeredArchitecture()
            .layer("-client-ui-console.jar").definedBy(
                    "com.mamezou.rms.client.console..")
            .layer("-client-api.jar").definedBy(
                    "com.mamezou.rms.client.api",
                    "com.mamezou.rms.client.api.dto..",
                    "com.mamezou.rms.client.api.exception..",
                    "com.mamezou.rms.client.api.login..")
            .layer("-client-api-local.jar").definedBy("com.mamezou.rms.client.api.adaptor.local..")
            .layer("-client-api-remote.jar").definedBy("com.mamezou.rms.client.api.adaptor.remote..")
            .layer("-service-server.jar").definedBy("com.mamezou.rms.external.webapi..")
            .layer("-service.jar").definedBy("com.mamezou.rms.core..")
            .layer("-platform.jar").definedBy("com.mamezou.rms.platform..")

            .whereLayer("-client-ui-console.jar").mayNotBeAccessedByAnyLayer()
            .whereLayer("-client-api.jar").mayOnlyBeAccessedByLayers("-client-ui-console.jar", "-client-api-local.jar", "-client-api-remote.jar")
            .whereLayer("-client-api-local.jar").mayNotBeAccessedByAnyLayer()
            .whereLayer("-client-api-remote.jar").mayNotBeAccessedByAnyLayer()
            .whereLayer("-service-server.jar").mayNotBeAccessedByAnyLayer()
            .whereLayer("-service.jar").mayOnlyBeAccessedByLayers("-client-api-local.jar", "-service-server.jar")
            .whereLayer("-platform.jar").mayOnlyBeAccessedByLayers("-client-ui-console.jar", "-client-api.jar", "-client-api-local.jar",
                    "-client-api-remote.jar", "-service-server.jar", "-service.jar");

    /**
     * アプリのコードで依存OKなライブラリの定義。spiパッケージを除き依存してよいのは以下のモノのみ
     * <pre>
     * ・アプリ自身のクラス(com.mamezou.rms..)
     * ・Apache Commons Lang(org.apache.commons.lang3..)
     * ・SLF4J(org.slf4j..)
     * ・MicroProfile API(org.eclipse.microprofile..)
     * ・JavaEE API(javax..)
     * ・JavaSE API(java..)
     * </pre>
     * エントリポイントとなるMainクラス以外はHelidon(io.helidon..)に直接依存しないこと
     */
    @ArchTest
    static final ArchRule test_アプリが依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("com.mamezou.rms..")
                    .and().haveSimpleNameNotEndingWith("Main")
                    .and().resideOutsideOfPackage("com.mamezou.rms.test..")
                    .and().resideOutsideOfPackage("com.mamezou.rms.client.console.ui..")
                    .and().resideOutsideOfPackage("..jose4j..")
                    .and().resideOutsideOfPackage("..log.ext..")
                    .and().resideOutsideOfPackage("..config.helidon..")
                    .and().resideOutsideOfPackage("..core.persistence.file.io..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage( // helidonへの直接依存はなし
                                "com.mamezou.rms..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile..",
                                "javax..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );

}
