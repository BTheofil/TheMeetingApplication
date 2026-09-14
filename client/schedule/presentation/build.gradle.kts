plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.schedule.presentation"
}

dependencies {
    implementation(projects.designSystem)
    implementation(projects.network)
    implementation(projects.schedule.domain)

    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.koin)
}