buildscript {
    configurations {
        classpath {
            resolutionStrategy {
                force("org.ow2.asm:asm:9.6")
                force("org.ow2.asm:asm-commons:9.6")
            }
        }
    }
}

plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow")
}

group = "me.elsiff"
version = "4.2.1"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://jitpack.io")
    mavenCentral()
}

dependencies {
    compileOnlyApi("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnlyApi("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnlyApi("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnlyApi(files("lib/mcMMO.jar"))
    api("com.github.Musician101.MusiGui:paper:1.2.2")
    api("io.github.Musician101:Bukkitier:2.0.0")
    //TODO temp to fix package names
    //api("com.github.Musician101:MusiBoard:1.0.1")
    api("com.github.musician101:musiboard:-SNAPSHOT")
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        dependencies {
            include(dependency("com.github.Musician101:Bukkitier:"))
            include(dependency("com.github.Musician101.MusiGui:"))
        }

        archiveClassifier.set("")
        relocate("io.musician101.bukkitier", "me.elsiff.morefish.lib.io.musician101.bukkitier")
        relocate("io.musician101.musigui", "me.elsiff.morefish.lib.io.musician101.musigui")
        dependsOn("build")
    }

    register<Copy>("prepTestJar") {
        dependsOn("shadowJar")
        from("build/libs/${project.name}-${project.version}.jar")
        into("server/plugins")
    }
}
