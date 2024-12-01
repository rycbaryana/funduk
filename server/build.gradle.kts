import io.ktor.plugin.features.*

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
    application
}

val ktorVersion = libs.versions.ktor.get()

group = "by.funduk.internal"
version = "1.0.0"
application {
    mainClass.set("by.funduk.internal.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_20)
        localImageName.set("funduk-server")

        portMappings.set(
            listOf(
                DockerPortMapping(8080, 8080, DockerPortMappingProtocol.TCP)
            )
        )
        externalRegistry.set(
            DockerImageRegistry.dockerHub(
                appName = provider { "funduk-server" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.callLogging)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation("org.mindrot:jbcrypt:0.4")
    implementation(libs.ktor.serialization.json)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.h2)
    testImplementation(libs.h2)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}
