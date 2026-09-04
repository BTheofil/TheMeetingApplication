plugins {
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.search.presentation"
}

dependencies {
    implementation(projects.network)
    implementation(projects.designSystem)
    implementation(projects.search.domain)

    implementation(libs.koin.compose)
}