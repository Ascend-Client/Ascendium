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
