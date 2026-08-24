plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.auth.presentation"
}

dependencies {
    implementation(projects.auth.domain)
    implementation(projects.designSystem)
    implementation(projects.network)
    implementation(projects.datastore)
    
    implementation(libs.bundles.koin)
}
