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
            implementation(libs.kotlinx.datetime)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.websockets)
        }
    }
}