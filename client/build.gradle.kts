plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

val coroutinesVersion = libs.versions.coroutines.get()

kotlin {
    js(IR) {
        browser {
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}


val browserDist by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(browserDist.name, tasks.named("jsBrowserDistribution").map { it.outputs.files.files.single() })
}