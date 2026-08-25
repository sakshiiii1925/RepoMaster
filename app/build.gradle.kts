plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.repomaster"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.repomaster"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
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

    // Android
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)

    implementation(libs.androidx.core.ktx)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"
    )

    // Lifecycle
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4"
    )
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.8.4"
    )

    // Swipe Refresh
    implementation(
        "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    )
    implementation("androidx.work:work-runtime-ktx:2.10.1")
    // PDF
    implementation(
        "com.itextpdf:itext7-core:7.2.5"
    )

    // =====================================================
    // ROOM
    // =====================================================

    val roomVersion = "2.7.2"

    implementation(
        "androidx.room:room-runtime:$roomVersion"
    )

    implementation(
        "androidx.room:room-ktx:$roomVersion"
    )

    ksp(
        "androidx.room:room-compiler:$roomVersion"
    )

    // =====================================================
    // TESTS
    // =====================================================

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)

    androidTestImplementation(
        libs.androidx.espresso.core
    )
}