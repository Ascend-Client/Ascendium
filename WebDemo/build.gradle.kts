plugins {
    kotlin("js")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
    id("org.jetbrains.compose") version "1.8.1"
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "ascendium-webdemo.js"
            }

            testTask {
                enabled = false
            }
        }
        binaries.executable()
    }
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3.replace("1.8.2", "1.9.0-beta03"))
    implementation(compose.ui)

    implementation("com.github.skydoves:colorpicker-compose:1.1.2")
    implementation(compose.materialIconsExtended)

    implementation(npm("esbuild", "0.20.0"))
    implementation(npm("esbuild-loader", "4.0.0"))
}