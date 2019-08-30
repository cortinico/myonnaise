plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.github.ben-manes.versions")
}

apply(from = "../kotlin-static-analysis.gradle")

android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(Libs.kotlinJdk)
    implementation(Libs.androidXAppCompat)

    testImplementation(Libs.junit)
}
