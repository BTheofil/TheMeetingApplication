plugins {
    alias(libs.plugins.meet.android.library)
}

android {
    namespace = "hu.tb.network"
}

dependencies {
    implementation(projects.network.data)
    api(projects.auth.domain)
    api(projects.search.domain)
    api(projects.notification.domain)

    api(libs.bundles.ktor)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.koin)
}
