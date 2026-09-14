plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.design_system"
}

dependencies {
    api(libs.kotlinx.datetime)
}