import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.2.0"
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
    id("org.jetbrains.compose") version "1.9.0-beta01"
    kotlin("plugin.serialization") version "2.2.0"
}
val compile = false //important, set to true when running "build"

configurations.all {
    resolutionStrategy {
        force(
            "org.jetbrains.kotlinx:kotlinx-serialization:2.2.0",
            "org.jetbrains.kotlinx:kotlinx-serialization-jvm:2.2.0",
            "org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0"
        )
    }
}

subprojects {
    if (this.childProjects.isNotEmpty()) {
        return@subprojects
    }

    if (this.path.contains("SupportedVersions")) {
        this.setupSupportedVersion()
    }

    if (this.path.contains("Adapters")) {
        this.setupAdapter()
    }
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
}

/*tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${buildDir.absolutePath}/compose_metrics"
        )
        freeCompilerArgs.addAll(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${buildDir.absolutePath}/compose_metrics"
        )
    }
}*/

repositories {
    mavenCentral()
    jetbrainsCompose
    google()
    maven("https://maven.google.com/")
    maven("https://api.modrinth.com/maven")
    maven("https://jogamp.org/deployment/maven")
}

loom {
    runs {
        forEach {
            it.ideConfigGenerated(false)
        }
    }

    mixin {
        useLegacyMixinAp.set(false)
    }
}

val transitiveInclude: Configuration by configurations.creating {
    isTransitive = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
    }

    exclude(group = "com.mojang")
    exclude(group = "org.jetbrains.kotlin")
    exclude(group = "net.fabricmc")
}

dependencies {
    //unused, just for include() and stuff
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    fun use(name: String) = implementation(name)?.let { transitiveInclude(it) }

    //material3 + compose
    use(compose.desktop.linux_arm64)
    use(compose.desktop.linux_x64)
    use(compose.desktop.macos_arm64)
    use(compose.desktop.macos_x64)
    use(compose.desktop.windows_arm64)
    use(compose.desktop.windows_x64)

    use(compose.material3.replace("1.8.2", "1.9.0-beta03"))
    use(compose.uiTooling)
    use(compose.animation)
    use(compose.materialIconsExtended)
    use("com.github.skydoves:colorpicker-compose:1.1.2")

    use("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    use("dev.datlag:kcef:2025.03.23")

    use("org.jetbrains.kotlinx:kotlinx-html:0.12.0")

    for (item in subprojects) {
        if (item.childProjects.isNotEmpty()) {
            continue
        }

        if (item.path.contains("Adapters")) {
            include(if(compile) {
                item
            } else {
                project(item.path, "namedElements")
            })
        }
    }

    var i = 0
    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        i++
        include(it.moduleVersion.id.toString())
    }
    println("Bundled $i transitive dependencies.")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

compose.desktop {
    application {
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AscendiumJvmComposeApp"
            packageVersion = "1.0.0"
        }
    }
}

fun Project.setupSupportedVersion() {
    buildscript {
        repositories {
            gradlePluginPortal()
            mavenCentral()
            maven("https://maven.fabricmc.net/")
        }
        dependencies {
            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.0")
            classpath("org.jetbrains.kotlin:kotlin-serialization:2.2.0")
            classpath("net.fabricmc:fabric-loom:1.11-SNAPSHOT")
        }
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "fabric-loom")

    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.google.com/")
        maven("https://api.modrinth.com/maven")
        maven("https://jogamp.org/deployment/maven")
    }

    dependencies {
        minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
        mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")

        implementation(project(":"))

        modImplementation("net.fabricmc:fabric-loader:${rootProject.property("loader_version")}")
        modImplementation("net.fabricmc:fabric-language-kotlin:${rootProject.property("kotlin_loader_version")}")

        modRuntimeOnly("maven.modrinth:sodium:${project.property("sodium_ver")}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_ver")}")
        modRuntimeOnly("maven.modrinth:in-game-account-switcher:${project.property("igas_ver")}")
    }

    loom {
        runs {
            forEach {
                if (it.name == "server") {
                    it.ideConfigGenerated(false)
                } else {
                    it.client()
                    it.ideConfigGenerated(true)
                    it.runDir("../../../run") //united run directory
                }
            }
        }
    }
}

fun Project.setupAdapter() {
    buildscript {
        repositories {
            gradlePluginPortal()
            mavenCentral()
            maven("https://maven.fabricmc.net/")
        }
        dependencies {
            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.0")
            classpath("org.jetbrains.kotlin:kotlin-serialization:2.2.0")
            classpath("net.fabricmc:fabric-loom:1.11-SNAPSHOT")
        }
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "fabric-loom")

    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.google.com/")
        maven("https://api.modrinth.com/maven")
        maven("https://jogamp.org/deployment/maven")
    }

    dependencies {
        minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
        mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")

        implementation(project(":"))

        modImplementation("net.fabricmc:fabric-loader:${rootProject.property("loader_version")}")
        modImplementation("net.fabricmc:fabric-language-kotlin:${rootProject.property("kotlin_loader_version")}")

        implementation("org.jetbrains.skiko:skiko-awt:0.9.22")
    }

    loom {
        runs {
            forEach {
                it.ideConfigGenerated(false)
            }
        }

        mixin {
            useLegacyMixinAp.set(false)
        }
    }
}