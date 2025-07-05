// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}
//android.buildFeatures.buildConfig = true

//tasks.register("secKey"){
//    "7bab18003d78bb0f929ac1902b0c89783a687700"
//}