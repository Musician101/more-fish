plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

subprojects {
    apply {
        plugin("java")
        plugin("java-library")
        plugin("com.github.johnrengelman.shadow")
    }

    group = "me.elsiff"
    version = "4.2.0-SNAPSHOT"

    repositories {
        mavenCentral()
        //TODO temp
        mavenLocal()
        maven("https://jitpack.io")
    }

    tasks {
        shadowJar {
            dependencies {
                include(dependency("io.musician101.musigui:"))
            }

            relocate("io.musician101.musigui", "me.elsiff.morefish.lib.io.musician101.musigui")
            dependsOn("build")
        }
    }
}
