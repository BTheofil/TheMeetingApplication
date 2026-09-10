plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.dashboard.presentation"
}

dependencies {
    implementation(projects.designSystem)
    implementation(projects.dashboard.domain)
    implementation(projects.datastore)
    implementation(projects.network)

    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.koin)
}
