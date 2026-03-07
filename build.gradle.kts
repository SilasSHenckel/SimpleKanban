// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version "2.57" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("androidx.room") version "2.8.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
}