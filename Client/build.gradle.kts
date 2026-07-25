import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.fabric.loom) apply false

    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
}
val compile: Boolean by lazy {
    gradle.startParameter.taskNames.any { it.contains("build", ignoreCase = true) }
}

configurations.all {
    resolutionStrategy {
        force(
            "org.jetbrains.kotlinx:kotlinx-serialization:2.4.10",
            "org.jetbrains.kotlinx:kotlinx-serialization-jvm:2.4.10",
            "org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0"
        )
    }
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 25
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
}

repositories {
    mavenLocal()
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
            it.generateRunConfig.set(false)
        }
    }

    mixin {
        useLegacyMixinAp.set(false)
    }
}

val transitiveInclude: Configuration = configurations.create("transitiveInclude") {
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

    fun use(dep: Any) = transitiveInclude(dep)

    //material3 + compose
    use(libs.compose.material3)
    use(libs.compose.ui.tooling)
    use(libs.compose.animation)
    use(libs.skydoves.colorpicker.compose)

    use(libs.kotlinx.coroutines.test)
    use(libs.kcef)

    for (item in rootProject.subprojects) {
        if (item.childProjects.isNotEmpty()) {
            continue
        }

        if (item.path.contains("Adapters")) {
            if(item.path.startsWith(":Adapters:1.")) {
                include(if(compile) {
                    item
                } else {
                    project(item.path, "namedElements")
                })
            } else {
                include(item)
            }
        }
    }

    var i = 0
    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        if (it.moduleVersion.id.toString().contains("org.apache.commons")) {
            return@forEach
        }

        i++
        implementation(it.moduleVersion.id.toString())
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
            "minecraft_version" to project.property("minecraft_version")!!,
            "loader_version" to project.property("loader_version")!!,
            "kotlin_loader_version" to project.property("kotlin_loader_version")!!
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
