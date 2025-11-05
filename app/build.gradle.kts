plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.group4.gamecontrollershop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.group4.gamecontrollershop"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{

        viewBinding= true

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation ("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-storage:19.2.2")
    implementation ("com.facebook.android:facebook-android-sdk:latest.release")
    implementation("com.paypal.sdk:paypal-android-sdk:2.16.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.facebook.android:facebook-login:latest.release")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}