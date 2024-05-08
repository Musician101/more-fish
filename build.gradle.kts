import xyz.jpenilla.resourcefactory.bukkit.Permission

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
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.7.0"
    id("xyz.jpenilla.run-paper") version "2.2.4"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
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
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
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
    }

    shadowJar {
        dependencies {
            include(dependency("com.github.Musician101:Bukkitier:"))
            include(dependency("com.github.Musician101.MusiGui:"))
        }

        archiveFileName.set("${project.name}-${project.version}.jar")
        relocate("io.musician101.bukkitier", "me.elsiff.morefish.lib.io.musician101.bukkitier")
        relocate("io.musician101.musigui", "me.elsiff.morefish.lib.io.musician101.musigui")
        dependsOn("build")
    }

    runServer {
        minecraftVersion("1.20.4")
    }
}

bukkitPluginYaml {
    main = "me.elsiff.morefish.MoreFish"
    authors.addAll("elsiff", "Musician101")
    apiVersion = "1.20"
    softDepend.addAll("mcMMO", "MusiBoard", "Vault")
    commands.create("morefish") {
        aliases.addAll("mf", "fish")
        description = "Main command for MoreFish."
        usage = "/morefish"
    }
    permissions.create("morefish.admin") {
        default = Permission.Default.OP
        description = "Gives the user the ability to control the fishing contest."
    }
}
