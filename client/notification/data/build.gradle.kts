plugins {
    alias(libs.plugins.meet.android.library)
}

android {
    namespace = "hu.tb.notification.data"
}

dependencies {
    implementation(projects.network)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.message)
    implementation(libs.firebase.installations)
    implementation(libs.androidx.core)
    implementation(libs.koin)
}
