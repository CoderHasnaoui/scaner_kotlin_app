plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.mimi_projet_zentech"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mimi_projet_zentech"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}
dependencies {
    // encrypted shared pre
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    // scanning barcode
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("androidx.camera:camera-mlkit-vision:1.3.0")

    // log interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
// gson converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // ML Google
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")
    // Zixing // libraries
//    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
//    implementation("com.google.zxing:core:3.5.1")

    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    dependencies {
        // This gives you access to almost every Material icon (Outlined, Rounded, etc.)
        implementation("androidx.compose.material:material-icons-extended:1.7.0")
    }
    dependencies {
        // 1. The ViewModel library (The "Brain")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

        // 2. Lifecycle Utilities (Handles app background/foreground logic)
        implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

        // 3. Navigation Compose (You already have this, but it's part of the flow)
        implementation("androidx.navigation:navigation-compose:2.8.5")

    }

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
// Retrofit with Scalar Converter
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    // Compose
    implementation("com.google.android.material:material:1.12.0")
    // lolit dependeency
    dependencies {
        implementation("com.airbnb.android:lottie-compose:6.7.1")
    }
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.camera.camera2.pipe)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.datastore.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}