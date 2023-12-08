plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow")
}

group = "me.elsiff"
version = "4.2.0"

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
    compileOnlyApi("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnlyApi("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnlyApi("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnlyApi(files("lib/mcMMO.jar"))

    api("com.github.musician101.musigui:paper:1.2.2") {
        exclude("io.papermc.paper")
    }
    api("com.github.Musician101:Bukkitier:1.3.3") {
        exclude("org.spigotmc")
    }
    //TODO temp to fix package names
    //api("com.github.Musician101:MusiBoard:1.0.1") {
    api("com.github.Musician101:MusiBoard:master-SNAPSHOT") {
        exclude("io.papermc.paper")
        exclude("com.github.Musician101")
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
            include(dependency("com.github.Musician101:Bukkitier:"))
            include(dependency("com.github.musician101.musigui:"))
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
