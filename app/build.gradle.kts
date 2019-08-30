plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-android-extensions")
    id("com.github.ben-manes.versions")
}
apply(from = "../jacoco.gradle")
apply(from = "../kotlin-static-analysis.gradle")

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "it.ncorti.emgvisualizer"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 3
        versionName = "2.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

repositories {
    google()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(project(":myonnaise"))
    implementation(project(":sensorgraphview"))

    implementation(Libs.kotlinJdk)

    implementation(Libs.androidXAppCompat)
    implementation(Libs.androidXConstraintLayout)
    implementation(Libs.androidXRecyclerView)
    implementation(Libs.materialComponents)

    testImplementation(Libs.junit)
    testImplementation(Libs.mockitoCore)
    testImplementation(Libs.mockitoKotlin)

    implementation(Libs.rxjava2)
    implementation(Libs.rxAndroid2)

    implementation(Libs.dagger)
    kapt(Libs.daggerCompiler)
    implementation(Libs.daggerAndroid)
    kapt(Libs.daggerAndroidProcessor)

    implementation(Libs.appIntro)
}
