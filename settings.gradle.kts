pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app", ":myonnaise", ":sensorgraphview")

rootProject.name = "myonnaise-root"