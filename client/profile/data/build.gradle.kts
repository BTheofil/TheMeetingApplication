plugins {
    alias(libs.plugins.meet.android.library)
}
android {
    namespace = "hu.tb.profile.data"
}
dependencies {
    implementation(projects.profile.domain)

    implementation(libs.bundles.koin)
    implementation(libs.revenuecat)
}