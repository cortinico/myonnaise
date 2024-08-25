plugins {
    id("com.android.application") apply false version "8.5.1"
    id("com.android.library") apply false version "8.5.1"
    kotlin("android") apply false version "1.9.20"
    alias(libs.plugins.detekt)
}

allprojects {
    group = "com.ncorti"
}

val detektFormatting = libs.detekt.formatting

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        config.from(rootProject.files("config/detekt/detekt.yml"))
    }

    dependencies {
        detektPlugins(detektFormatting)
    }
}
