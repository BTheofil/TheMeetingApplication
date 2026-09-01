plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.search.presentation"
}

dependencies {
    implementation(projects.designSystem)
}