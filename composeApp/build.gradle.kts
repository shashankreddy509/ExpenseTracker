import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleService)
    alias(libs.plugins.kotlinxSerialization)
    id("app.cash.sqldelight") version "2.0.1"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)

                //Navigation, Room and koin DI
                implementation(libs.compose.navigation)
                implementation(libs.landscapist.coil3)
                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                //Voyager
                implementation(libs.navigator)
                implementation(libs.navigator.screen.model)
                implementation(libs.navigator.transitions)
                implementation(libs.navigator.koin)

                //Firebase
                implementation(libs.firebase.auth)
                implementation(libs.firebase.firestore)
                implementation(libs.stately.common)

                //DateTime
                implementation(libs.kotlinx.datetime)

                //Image
                implementation(libs.landscapist.coil3)

                //DatePicker
                implementation(libs.kmp.date.time.picker)

                // SQLDelight
                implementation(libs.runtime)
                implementation(libs.coroutines.extensions)
                implementation(libs.primitive.adapters)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.koin.android)

                // SQLDelight Android driver
                implementation(libs.android.driver)
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
            }
        }

        val iosMain by creating {
            dependencies {
                // SQLDelight iOS driver
                implementation(libs.native.driver)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                // SQLDelight JVM driver
                implementation(libs.sqlite.driver)
            }
        }
    }
}

android {
    namespace = "com.shashank.expense.tracker"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        applicationId = "com.shashank.expense.tracker"
        versionCode = 1
        versionName = "1.0.0"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
        resources.srcDirs("src/commonMain/resources")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

sqldelight {
    databases {
        create("ExpenseDatabase") {
            packageName.set("com.shashank.expense.tracker.db")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight"))
            verifyMigrations.set(true)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.shashank.expense.tracker.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.shashank.expense.tracker"
            packageVersion = "1.0.0"
        }
    }
}
