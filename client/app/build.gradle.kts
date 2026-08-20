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

    implementation(libs.koin)
}
