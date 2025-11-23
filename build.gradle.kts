// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

ktlint {
    android = true
    outputColorName = "RED"
    verbose = true
    ignoreFailures = true
    enableExperimentalRules = true

    reporters {
        reporter(reporterType = ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.SARIF)
    }

    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        android = true
    }
}