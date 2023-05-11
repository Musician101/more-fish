plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow")
}

group = "me.elsiff"
version = "4.1.3"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net/")
    mavenCentral()
}

dependencies {
    compileOnlyApi("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
    compileOnlyApi("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnlyApi("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    compileOnlyApi(files("lib/mcMMO.jar"))

    //TODO work around since Jitpack can't build the 1.1.0 release for some reason
    api("com.github.musician101.musigui:spigot:e292d9c8e2") {
        exclude("com.google.code.findbugs")
        exclude("org.spigotmc")
    }
    api("com.github.musician101:bukkitier:1.2.2") {
        exclude("org.spigotmc")
    }
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
            include(dependency(":common"))
            include(dependency("com.github.musician101:"))
            include(dependency("com.github.musician101.musigui:spigot:"))
        }

        archiveClassifier.set("")
        relocate("io.musician101", "me.elsiff.morefish.lib.io.musician101")
        dependsOn("build")
    }

    register<Copy>("prepTestJar") {
        dependsOn("shadowJar")
        from("build/libs/${project.name}-${project.version}.jar")
        into("server/plugins")
    }
}


