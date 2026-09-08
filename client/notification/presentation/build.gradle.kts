plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.notification.presentation"
}

dependencies {
    implementation(projects.designSystem)
    implementation(projects.notification.domain)
}