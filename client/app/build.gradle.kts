plugins {
    alias(libs.plugins.meet.android.application)
    alias(libs.plugins.meet.android.application.compose)
}

android {
    namespace = "hu.tb.meeting"

    defaultConfig {
        applicationId = "hu.tb.meeting"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
}

dependencies {
    implementation(projects.navigator)
    implementation(projects.designSystem)
    implementation(projects.auth.presentation)
    implementation(projects.profile.presentation)
    implementation(projects.network)
    implementation(projects.datastore)
    implementation(projects.dashboard.presentation)

    implementation(libs.koin)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
}
