plugins {
    alias(libs.plugins.meet.android.library)
    alias(libs.plugins.meet.android.library.compose)
}

android {
    namespace = "hu.tb.navigator"
}

dependencies {
    // `api`, not `implementation`: this module's public surface *is* nav3 -- consumers
    // declare their own NavKeys and entry providers against these types.
    api(libs.bundles.nav3)
}
