plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.example.shortvideoawareness"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.shortvideoawareness"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"
    }
}
