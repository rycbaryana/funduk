import io.ktor.plugin.features.*

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
    application
}

val ktorVersion = libs.versions.ktor.get()

group = "by.funduk"
version = "1.0.0"
application {
    mainClass.set("by.funduk.web.MainKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

val browserDist by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_20)
        localImageName.set("funduk-web")

        portMappings.set(
            listOf(
                DockerPortMapping(8081, 8081, DockerPortMappingProtocol.TCP)
            )
        )
        externalRegistry.set(

            DockerImageRegistry.dockerHub(
                appName = provider { "funduk-web" },
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
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.serialization.json)
    browserDist(project(":web:archive", "browserDist"))
    browserDist(project(":web:task", "browserDist"))
}

tasks.withType<Copy>().named("processResources") {
    from(browserDist)
    eachFile {
        val pathElements = file.toPath().toString().split(File.separator)
        val moduleName = pathElements.getOrNull(pathElements.indexOf("web") + 1) ?: throw GradleException("UNKNOWN MODULE")
        relativePath = relativePath.prepend("static/$moduleName")
    }
}