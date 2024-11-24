plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    js(IR) {
        browser{}
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.contentNegotiation)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}