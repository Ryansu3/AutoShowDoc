plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.cmcc.littlec.autoshowdoc"
version = "1.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2024.3.5")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("java"))
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")  // HTTP客户端
    implementation("org.freemarker:freemarker:2.3.32")   // 模板引擎
    // 添加Spring框架依赖用于注解解析
    implementation("org.springframework:spring-web:6.2.0")
    implementation("org.springframework:spring-context:6.2.0")
}

tasks {
    initializeIntelliJPlugin{
        selfUpdateCheck.set(false)
    }
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
