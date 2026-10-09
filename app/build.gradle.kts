plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.moneytrack"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.moneytrack"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Обфускация и оптимизация R8 (новый DSL AGP 9.3+).
            // Одна настройка enable = true заменяет старую пару флагов:
            //   minifyEnabled true    (isMinifyEnabled = true)   — сжатие, оптимизация и обфускация кода;
            //   shrinkResources true  (isShrinkResources = true) — удаление неиспользуемых ресурсов.
            // Стандартные правила proguard-android-optimize.txt подключаются автоматически,
            // свои правила лежат в src/main/keepRules/rules.keep.
            // Таблица соответствия имён после сборки: app/build/outputs/mapping/release/mapping.txt
            optimization {
                enable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation("androidx.navigation:navigation-compose:2.10.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Сеть: курсы валют ЦБ РФ
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Фоновые задачи: напоминания о подписках
    implementation(libs.androidx.work.runtime.ktx)
}