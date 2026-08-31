plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.dashboard.presentation"
}

dependencies {
    implementation(projects.designSystem)
    implementation(projects.auth.domain)
    implementation(libs.kotlinx.datetime)
}
