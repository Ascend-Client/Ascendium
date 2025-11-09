rootProject.name = "Ascendium"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

val SupportedVersions = arrayOf(
    "1.16:cts8c",
    "1.16:1.16.5",

    "1.19:1.19.2",
    "1.19:1.19.4",

    "1.20:1.20.1",
    "1.20:1.20.4",
    "1.20:1.20.6",

    "1.21:1.21.1",
    "1.21:1.21.4",
    "1.21:1.21.5",
    "1.21:1.21.8",
    "1.21:1.21.9",
    "1.21:1.21.10",

    "Snapshot_25w45a"
)

val Adapters = arrayOf(
    "1.16:cts8c",
    "1.16:1.16.5",

    "1.19:1.19.2",
    "1.19:1.19.4",

    "1.20:1.20.1",
    "1.20:1.20.4",
    "1.20:1.20.6",

    "1.21:1.21.1",
    "1.21:1.21.4",
    "1.21:1.21.5",
    "1.21:1.21.8",
    "1.21:1.21.9",
    "1.21:1.21.11"
)

SupportedVersions.forEach { include(":SupportedVersions:$it") }
Adapters.forEach { include(":Adapters:$it") }
include(":Client", ":WebDemo")
