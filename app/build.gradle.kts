plugins {
    id("com.android.application")
}

android {
    namespace = "com.guit.edu.myapplication"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.guit.edu.myapplication"
        minSdk = 25
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    /*Bmob后端云*/
    implementation ("io.github.bmob:android-sdk:3.9.4")
    implementation ("io.reactivex.rxjava2:rxjava:2.2.8")
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation ("com.squareup.okhttp3:okhttp:4.8.1")
    implementation ("com.squareup.okio:okio:2.2.2")
    implementation ("com.google.code.gson:gson:2.8.5")

    /*圆形头像组件*/
    implementation ("com.makeramen:roundedimageview:2.2.1")
    implementation ("com.github.bumptech.glide:glide:3.7.0")

    /*滑动选择器*/
    implementation ("com.github.javakam:widget.wheelview:1.8.0@aar")
    implementation ("com.github.javakam:widget.pickerview:1.8.0@aar")

    /*绘制图表工具*/
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
}