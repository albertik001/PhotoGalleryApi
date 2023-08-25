@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id(libs.plugins.com.android.application.get().pluginId)
    id(libs.plugins.org.jetbrains.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.navigation.safeArgs.get().pluginId)
}

android {
    namespace = "org.tredo.photogalleryapi"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.tredo.photogalleryapi"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    viewBinding.enable = true
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.glide.glide)
    implementation(libs.ui.viewBindingPropertyDelegate)
    implementation(libs.ui.swipeToRefresh)

    implementation(libs.dagger2)
    kapt(libs.dagger2Compiler)

    implementation(libs.bundles.navigation)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.okHttp)
    implementation(libs.bundles.lifecycle)
    implementation(libs.paging.paging)
    implementation(libs.paging.common)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

}