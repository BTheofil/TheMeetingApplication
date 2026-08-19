plugins {
    alias(libs.plugins.meet.android.library)
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.navigator"
}

dependencies {
    implementation(projects.auth.presentation)
    implementation(projects.auth.domain)

    api(libs.bundles.nav3)
}
