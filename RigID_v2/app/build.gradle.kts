
import com.android.build.api.dsl.AndroidResources

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.rigid"
    compileSdk = 34

    // Exclude the META-INF/INDEX.LIST file from the APK or AAB file
    packaging {
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/DEPENDENCIES")
    }

    defaultConfig {
        applicationId = "com.example.rigid"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    fun AndroidResources.() {
        noCompress("tflite")
        // or noCompress "lite"
    }
    // or noCompress "lite"
    androidResources
}



dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation ("com.google.mlkit:image-labeling-custom:17.0.2")
    implementation ("com.google.mlkit:image-labeling:17.0.2")
    implementation("com.google.mlkit:object-detection:17.0.1")
    //  implementation 'com.google.mlkit:object-detection:16.2.4'
    implementation ("androidx.camera:camera-camera2:1.3.0-alpha01")
    implementation ("androidx.camera:camera-core:1.3.0-alpha01")
    implementation ("androidx.camera:camera-lifecycle:1.3.0-alpha01")
    implementation ("androidx.camera:camera-view:1.3.0-alpha01")
    implementation("androidx.databinding:databinding-runtime:4.1.0")
    implementation("com.google.mlkit:object-detection-custom:17.0.1")
    implementation ("pub.devrel:easypermissions:3.0.0")

    // Vision API
    //implementation ("com.google.cloud:google-cloud-vision:2.0.2")
    implementation ("com.google.cloud:google-cloud-storage:1.113.14")
    implementation ("com.google.auth:google-auth-library-oauth2-http:0.22.2")
    implementation ("com.google.guava:guava:30.1-jre")
    implementation ("com.google.cloud:google-cloud-vision:3.1.1")
    implementation ("androidx.annotation:annotation:1.5.0")


    implementation(libs.androidx.camera.view)

    implementation(libs.androidx.camera.core)
    implementation(libs.vision.common)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.databinding.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}