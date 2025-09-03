rootProject.name = "Ascendium"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}

include("SupportedVersions:1.21:1.21.4")
include("SupportedVersions:1.21:1.21.8")
include("Adapters:1.21.4")
include("Adapters:1.21.8")