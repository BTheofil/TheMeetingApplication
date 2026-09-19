plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.profile.presentation"
}

dependencies {
    implementation(projects.designSystem)
    implementation(projects.datastore)
    implementation(projects.network)
    implementation(projects.profile.domain)
    implementation(projects.profile.data)

    implementation(libs.bundles.koin)
    implementation(libs.revenuecat)
}