plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.3.0"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

subprojects {
    apply {
        plugin("java-library")
        plugin("com.gradleup.shadow")
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
        build {
            dependsOn(shadowJar)
        }

        shadowJar {
            dependencies {
                include(dependency("com.github.Musician101.MusiGUI:.*"))
                include(dependency("com.github.Musician101.MusiCommand:.*"))
            }

            archiveClassifier = ""
            relocate("io.musician101.musigui", "me.elsiff.morefish.lib.io.musician101.musigui")
            relocate("io.musician101.musicommand", "me.elsiff.morefish.lib.io.musician101.musicommand")
        }
    }
}
