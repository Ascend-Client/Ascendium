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

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.google.com/")
    google()
    maven("https://api.modrinth.com/maven")
}

val transitiveInclude: Configuration by configurations.creating {
    isTransitive = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
    }

    exclude(group = "com.mojang")
    exclude(group = "org.jetbrains.kotlin")
    exclude(group = "org.jetbrains.kotlinx")
    exclude(group = "net.fabricmc")
}

dependencies {
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

    use(compose.material3)
    use(compose.uiTooling)
    use(compose.animation)

    var i = 0
    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        i++
        include(it.moduleVersion.id.toString())
    }
    println("Bundled $i transitive dependencies.")

    modRuntimeOnly("maven.modrinth:sodium:mc1.21.4-0.6.13-fabric")
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.119.4+1.21.4")
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