plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.fabric.loom) apply false
}

subprojects {
    if(this.childProjects.isNotEmpty()) return@subprojects
    if(!(this.path.contains("Adapters") || this.path.contains("SupportedVersions") || this.path.contains("Client")))
        return@subprojects

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "fabric-loom")

    repositories {
        mavenCentral()
    }

    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.skiko:skiko-awt:0.156.1")
            force("org.jetbrains.skiko:skiko:0.156.1")
            force("commons-io:commons-io:2.18.0")
        }
    }
}

repositories {}
dependencies {}