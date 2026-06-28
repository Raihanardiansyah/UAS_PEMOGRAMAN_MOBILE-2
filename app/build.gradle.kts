plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.tiitipsampah"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.tiitipsampah"
        minSdk = 24
        targetSdk = 36
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
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // Salin baris CameraX ini ke gradle kamu, Cuy:
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    implementation("com.google.code.gson:gson:2.10.1")
    // Tambahin library AI Google ML Kit ini, Cuy:
    implementation("com.google.mlkit:object-detection:17.0.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.fragment:fragment:1.6.2")
}