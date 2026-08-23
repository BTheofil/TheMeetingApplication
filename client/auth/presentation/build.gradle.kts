plugins {
    alias(libs.plugins.meet.android.library)
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.presentation"
}

dependencies {
    implementation(projects.auth.domain)
    implementation(projects.designSystem)
    implementation(projects.network)
    
    implementation(libs.bundles.koin)
}
