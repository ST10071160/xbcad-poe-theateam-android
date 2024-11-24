// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

ktlint {
    version.set("0.43.0")
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
}


