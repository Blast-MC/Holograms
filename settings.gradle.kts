pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "Holograms"
include("api")

project(":api").name = "HologramsAPI"
