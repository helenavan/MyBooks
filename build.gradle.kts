plugins {
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.dagger) apply false
    kotlin("plugin.serialization") version "1.9.0" apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}