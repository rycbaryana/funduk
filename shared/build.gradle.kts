plugins {
    alias(libs.plugins.kotlinMultiplatform)

}

kotlin {
    js(IR) {
        browser{}
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
    }
}