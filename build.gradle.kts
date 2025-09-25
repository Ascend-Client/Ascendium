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
}

repositories {}
dependencies {}