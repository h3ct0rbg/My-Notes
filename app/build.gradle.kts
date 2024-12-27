plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.android.mynotes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.mynotes"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Add new dependencies
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    implementation(libs.recyclerview)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.material.v110)
    implementation(libs.roundedimageview)
}