plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.fabric.loom) apply false
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.google.com/")
    maven("https://api.modrinth.com/maven")
    maven("https://jogamp.org/deployment/maven")
    maven("https://maven.rizecookey.net")
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")

    implementation(project(":Client"))

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin.loader)

    runtimeOnly("org.joml:joml:1.10.5")
}

loom {
    customMinecraftManifest = "https://gist.githubusercontent.com/rizecookey/4c6142baaccc3875f9b227fe22f2ace5/raw/c8ed74b19f7a5315813c9d4b199798b692a8f359/1.16_combat-6.json"
    intermediaryUrl = "https://maven.rizecookey.net/net/fabricmc/intermediary/1.16_combat-6/intermediary-1.16_combat-6-v2.jar"
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
