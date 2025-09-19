rootProject.name = "Ascendium"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}

val SupportedVersions = arrayOf(
    "1.19:1.19.2",
    "1.19:1.19.4",

    "1.20:1.20.1",
    "1.20:1.20.4",
    "1.20:1.20.6",

    "1.21:1.21.1",
    "1.21:1.21.4",
    "1.21:1.21.8"
)


val Adapters = arrayOf(
    "1.19:1.19.2",
    "1.19:1.19.4",

    "1.20:1.20.1",
    "1.20:1.20.4",
    "1.20:1.20.6",

    "1.21:1.21.1",
    "1.21:1.21.4",
    "1.21:1.21.8"
)

SupportedVersions.forEach { include(":SupportedVersions:$it") }
Adapters.forEach { include(":Adapters:$it") }