package hu.tb

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure Compose-specific options.
 *
 * Every Compose module gets the same baseline here -- BOM, material3 and tooling -- so the
 * application and library paths cannot drift apart. Dependencies stay on `implementation`:
 * consumers get Compose on their own compile classpath from their own Compose convention
 * plugin, so nothing needs to leak transitively.
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    pluginManager.apply("com.github.skydoves.compose.stability.analyzer")

    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            "implementation"(platform(bom))
            "implementation"(libs.findLibrary("androidx-compose-material3").get())
            "implementation"(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
        }
    }
}
