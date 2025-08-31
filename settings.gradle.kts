pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        // 添加 JetBrains Plugin Repository
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
}

rootProject.name = "AutoShowDoc"