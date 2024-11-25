plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.theateam.sparklinehr"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.theateam.sparklinehr"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // for view Binding
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation ("com.airbnb.android:lottie:5.2.0")
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database)

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)

    testImplementation("org.mockito:mockito-core:5.13.0")


    testImplementation ("androidx.test:core:1.4.0")
    testImplementation ("androidx.test.ext:junit:1.1.5")
    testImplementation ("androidx.test:runner:1.4.0")


    testImplementation(libs.junit)
    testImplementation(libs.testng)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.espresso.core)



    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:4.8.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.1.0")

    testImplementation ("com.google.firebase:firebase-database:20.2.2")
    androidTestImplementation(libs.junit.junit)

//    testImplementation 'org.robolectric:robolectric:4.10'
}