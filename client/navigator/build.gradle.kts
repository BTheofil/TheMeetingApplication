plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.navigator"
}

dependencies {
    implementation(projects.auth.presentation)
    implementation(projects.auth.domain)
    implementation(projects.dashboard.presentation)
    implementation(projects.profile.presentation)
    implementation(projects.datastore)
    implementation(projects.designSystem)
    implementation(projects.network)
    implementation(projects.search.presentation)

    implementation(libs.koin.compose)
    api(libs.bundles.nav3)
}
