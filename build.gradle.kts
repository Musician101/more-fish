plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow")
}

group = "me.elsiff"
version = "4.0.3-SNAPSHOT"

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
    compileOnlyApi("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

    api("com.github.musician101.musigui:spigot:d353c948a4") {
        exclude("com.google.code.findbugs")
        exclude("org.spigotmc")
    }
    api("com.github.musician101:bukkitier:1.2") {
        exclude("org.spigotmc")
    }
}

tasks.withType(ProcessResources::class) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    dependencies {
        include(dependency(":common"))
        include(dependency("io.musician101:"))
        include(dependency("io.musician101.musigui:"))
    }

    archiveBaseName.set("more-fish-${project.version}.jar")
    relocate("io.musician101", "com.campmongoose.serversaturday.lib.io.musician101")
    dependsOn("build")
}
