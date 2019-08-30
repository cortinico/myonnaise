buildscript {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }
    dependencies {
        classpath(Libs.androidGradlePlugin)
        classpath(Libs.kotlinGradlePlugin)
        classpath(Libs.androidMavenGradlePlugin)
        classpath(Libs.bintrayGradlePlugin)
        classpath(Libs.versionsGradlePlugin)
        classpath(Libs.detektGradlePlugin)
        classpath(Libs.ktLintGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}