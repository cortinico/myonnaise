plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    compileSdk = libs.versions.compile.sdk.version.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.version.get().toInt()
        targetSdk = libs.versions.target.sdk.version.get().toInt()
        namespace = "it.ncorti.emgvisualizer"

        applicationId = "it.ncorti.emgvisualizer"

        versionCode = 6
        versionName = "3.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs = listOf("-Xstring-concat=inline")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    lint {
        warningsAsErrors = true
        abortOnError = true
        disable.addAll(listOf("GradleDependency", "IconDensities"))
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(projects.myonnaise)
    implementation(projects.sensorgraphview)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    
    implementation(libs.materialcomponents)

    implementation(libs.rxjava2)
    implementation(libs.rxandroid2)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.dagger.android)
    kapt(libs.dagger.android.processor)

    implementation(libs.appintro)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
}
