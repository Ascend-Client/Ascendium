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
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")

    implementation(project(":Client"))

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin.loader)

    modRuntimeOnly("maven.modrinth:sodium:${project.property("sodium_ver")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_ver")}")
    modRuntimeOnly("maven.modrinth:in-game-account-switcher:${project.property("igas_ver")}")

    runtimeOnly("org.joml:joml:1.10.5")
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
