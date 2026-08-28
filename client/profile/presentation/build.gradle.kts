plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.profile.presentation"
}

dependencies {
    implementation(projects.profile.domain)
    implementation(projects.designSystem)
    implementation(projects.datastore)
    implementation(projects.network)

    implementation(libs.bundles.koin)
}