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

    implementation(project(":Client"))

    implementation(libs.fabric.loader)
    implementation(libs.fabric.kotlin.loader)
}

loom {
    runs {
        forEach {
            if (it.name == "server") {
                it.generateRunConfig.set(false)
            } else {
                it.client()
                it.generateRunConfig.set(true)
                it.runDirectory.set(File("../../../run")) //united run directory
            }
        }
    }
}
