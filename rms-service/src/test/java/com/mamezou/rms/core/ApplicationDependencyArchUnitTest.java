package com.mamezou.rms.core;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.mamezou.rms", importOptions = ImportOption.DoNotIncludeTests.class)
public class ApplicationDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // applicationパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * applicationパッケージ内のアプリのコードで依存OKなライブラリの定義。依存してよいのは以下のモノのみ
     * <pre>
     * ・アプリ自身のクラス(com.mamezou.rms..)
     * ・Apache Commons Lang(org.apache.commons.lang3..)
     * ・SLF4J(org.slf4j..)
     * ・MicroProfile Config
     * ・CDI
     * ・JTA
     * ・JPA
     * ・BeanValidation
     * ・JavaSE API(java..)
     * </pre>
     */
    @ArchTest
    static final ArchRule test_アプリが依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("com.mamezou.rms.application..")
                    .and().resideOutsideOfPackage("com.mamezou.rms.application.persistence.file.io..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "com.mamezou.rms..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile.config..",
                                "javax.inject..",
                                "javax.enterprise.inject..",
                                "javax.enterprise.context..",
                                "javax.validation..",
                                "javax.transaction..",
                                "javax.persistence..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );

    /**
     * persistenceの実装パッケージへの依存がないことの定義
     * <pre>
     * ・persistenceパッケージはjpa or fileパッケージに依存してないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_persistenceの実装パッケージへの依存がないことの定義 =
            noClasses()
                .that()
                    .resideInAPackage("com.mamezou.rms.application.persistence") // persistence直下
                .should()
                    .dependOnClassesThat()
                        .resideInAnyPackage(
                                "com.mamezou.rms.application.persistence.jpa..",
                                "com.mamezou.rms.application.persistence.file.."
                                );

    /**
     * java.io.*とjava.nio.*への依存はfileパッケージのみの定義
     * <pre>
     * ・java.io.*とjava.nio.*に依存するのはpersistence.fileパッケージのみであること
     * </pre>
     */
    @ArchTest
    static final ArchRule test_ioパッケージの依存はfileパッケージのみの定義 =
            noClasses()
                .that()
                    .resideInAPackage("com.mamezou.rms.application..")
                    .and().resideOutsideOfPackage("com.mamezou.rms.application.persistence.file..")
            .should()
                .dependOnClassesThat()
                    .resideInAnyPackage(
                        "java.io..",
                        "java.nio.."
                        );

    /**
     * commons-csvへの依存はfile.ioパッケージのみの定義
     * <pre>
     * ・org.apache.commons.csv.*に依存するのはpersistence.file.ioパッケージのみであること
     * </pre>
     */
    @ArchTest
    static final ArchRule test_commons_csvへの依存はioパッケージのみの定義 =
            noClasses()
                .that()
                    .resideInAPackage("com.mamezou.rms.application..")
                    .and().resideOutsideOfPackage("com.mamezou.rms.application.persistence.file.io..")
            .should()
                .dependOnClassesThat()
                    .resideInAnyPackage(
                        "org.apache.commons.csv.."
                        );

    /**
     * javax.persistence.*への依存はjpaパッケージのみの定義
     * <pre>
     * ・javax.persistence.*に依存するのはpersistence.jpaパッケージとenntityパッケージの2つであること
     * </pre>
     */
    @ArchTest
    static final ArchRule test_JPAの依存はjpaパッケージのみの定義 =
            noClasses()
                .that()
                    .resideInAPackage("com.mamezou.rms.application..")
                    .and().resideOutsideOfPackage("com.mamezou.rms.application.persistence.jpa..")
                    .and().resideOutsideOfPackage("com.mamezou.rms.application.entity..")
            .should()
                .dependOnClassesThat()
                    .resideInAnyPackage(
                        "javax.persistence.."
                        );

    /**
     * entityパッケージ内部の依存関係の定義
     * <pre>
     * ・entityパッケージ直下のクラスが使ってよいライブラリは条件で定義されたモノのみ
     * </pre>
     */
    @ArchTest
    static final ArchRule test_entityパッケージのクラスが依存してよいパッケージの定義 =
            classes()
                .that()
                    .resideInAPackage("com.mamezou.rms.application.entity..")
                .should()
                    .onlyDependOnClassesThat()
                        .resideInAnyPackage(
                                "com.mamezou.rms.application.entity..",
                                "org.apache.commons.lang3..",
                                "javax.persistence..",
                                "javax.validation..",
                                "java.."
                                );
}