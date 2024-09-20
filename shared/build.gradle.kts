plugins {
    alias(libs.plugins.kotlinMultiplatform)

}

kotlin {
    js {
        browser{}
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
    }
}