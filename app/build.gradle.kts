plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.construagro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.construagro"
        minSdk = 26
        targetSdk = 35
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
    implementation(platform(libs.firebase.bom)) // BOM primeiro

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.common)
    implementation(libs.firebase.database)

    implementation(libs.appcompat)
    implementation(libs.material) // Já está declarado via libs
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Adicione estas dependências para melhor suporte do Material Design
    implementation("com.google.android.material:material:1.11.0") // Versão mais recente
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") // Para ViewModel

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}