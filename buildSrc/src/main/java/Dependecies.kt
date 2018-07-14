object Versions {
    const val androidx = "28.0.0-alpha3"
    const val appintro = "v4.2.3"
    const val dagger = "2.16"
    const val constraintlayout = "1.1.2"
    const val junit = "4.12"
    const val kotlin = "1.2.51"
    const val materialcomponents = "1.0.0-alpha3"
    const val mockito = "2.19.0"
    const val mockitoKotlin = "2.0.0-RC1"
    const val rxjava2 = "2.1.15"
    const val rxandroid2 = "2.0.2"
}

object Libs {
    const val kotlinJdk = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val androidXAppCompat = "com.android.support:appcompat-v7:${Versions.androidx}"
    const val materialComponents = "com.google.android.material:material:${Versions.materialcomponents}"
    const val androidXRecyclerView = "com.android.support:recyclerview-v7:${Versions.androidx}"
    const val androidXConstraintLayout = "com.android.support.constraint:constraint-layout:${Versions.constraintlayout}"

    const val junit = "junit:junit:${Versions.junit}"
    const val mockitoCore = "org.mockito:mockito-core:${Versions.mockito}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"

    const val rxjava2 = "io.reactivex.rxjava2:rxjava:${Versions.rxjava2}"
    const val rxAndroid2 = "io.reactivex.rxjava2:rxandroid:${Versions.rxandroid2}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    const val daggerAndroid = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    const val appIntro = "com.github.apl-devs:appintro:${Versions.appintro}"
}