plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")

    id ("kotlin-kapt")
    id ("com.google.dagger.hilt.android")

}

android {
    namespace = "com.example.chatter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chatter"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation("androidx.navigation:navigation-compose:2.9.1")

    implementation ("com.google.dagger:hilt-android:2.57")
    kapt ("com.google.dagger:hilt-compiler:2.57")
    // For instrumentation tests
    androidTestImplementation ("com.google.dagger:hilt-android-testing:2.57")
    kaptAndroidTest ("com.google.dagger:hilt-compiler:2.57")
    // For local unit tests
    testImplementation ("com.google.dagger:hilt-android-testing:2.57")
    kaptTest ("com.google.dagger:hilt-compiler:2.57")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // Import the Firebase BoM
//    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Realtime Database with Kotlin extensions
    implementation("com.google.firebase:firebase-database-ktx")

    // If needed, add others:
    // implementation("com.google.firebase:firebase-auth-ktx")
    // implementation("com.google.firebase:firebase-firestore-ktx")
    kapt("androidx.hilt:hilt-compiler:1.2.0")


    implementation("androidx.compose.material3:material3:1.3.1")

// latest stable M3


}
kapt {
    correctErrorTypes= true
}