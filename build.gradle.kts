import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

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
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.2.4"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.1.1"
}

group = "me.elsiff"
version = "4.3.2"

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://jitpack.io")
    mavenCentral()
    //TODO testing
    mavenLocal()
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    compileOnlyApi("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnlyApi("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnlyApi(files("lib/mcMMO.jar"))
    api("com.github.Musician101.MusiGui:paper:1.2.2")
    //TODO testing
    //api("io.musician101.musicommand:paper:1.0.0-SNAPSHOT")
    api("io.musician101:bukkitier:2.1.0")
    //api("com.github.Musician101:Bukkitier:2.0.0")
    //TODO temp to fix package names
    //api("com.github.Musician101:MusiBoard:1.0.1")
    //api("com.github.musician101:musiboard:master-SNAPSHOT")
    api("io.musician101:musiboard:1.1.0-SNAPSHOT")
}

paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        dependencies {
            include(dependency("io.musician101:bukkitier:"))
            //include(dependency("com.github.Musician101:Bukkitier:"))
            include(dependency("com.github.Musician101.MusiGui:"))
            //include(dependency("io.musician101.musicommand:"))
        }

        archiveClassifier = ""
        relocate("io.musician101.bukkitier", "me.elsiff.morefish.lib.io.musician101.bukkitier")
        relocate("io.musician101.musigui", "me.elsiff.morefish.lib.io.musician101.musigui")
        //relocate("io.musician101.musicommand", "me.elsiff.morefish.lib.io.musician101.musigui")
    }

    runServer {
        minecraftVersion("1.20.4")
    }
}

bukkitPluginYaml {
    main = "me.elsiff.morefish.MoreFish"
    authors.addAll("elsiff", "Musician101")
    apiVersion = "1.20"
    softDepend.addAll("mcMMO", "Vault")
    depend.add("MusiBoard")
    commands.create("morefish") {
        aliases.addAll("mf", "fish")
        description = "Main command for MoreFish."
        usage = "/morefish"
    }
    permissions.create("morefish.admin") {
        default = Permission.Default.OP
        description = "Gives the user the ability to control the fishing contest."
    }

    foliaSupported = true;
}

paperPluginYaml {
    main = "me.elsiff.morefish.MoreFish"
    apiVersion = "1.20"
    authors.addAll("elsiff", "Musician101")
    foliaSupported = true
    dependencies.server {
        create("mcMMO") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
            joinClasspath = true
        }
        create("MusiBoard") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
            joinClasspath = true
        }
        create("Vault") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
            joinClasspath = true
        }
    }
    permissions.create("morefish.admin") {
        default = Permission.Default.OP
        description = "Gives the user the ability to control the fishing contest."
    }
}
