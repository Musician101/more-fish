import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.3.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
}

group = "me.elsiff"
version = "4.4.0"

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
    // mcMMO depends on WorldGuard, but Gradle failed to find it
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    compileOnlyApi("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnlyApi("com.gmail.nossr50.mcMMO:mcMMO:2.2.048")
    api("com.github.Musician101.MusiGUI:paper:3fb38265d4")
    api("com.github.Musician101.MusiCommand:paper:be49f96ace")
    api("com.github.Musician101:MusiBoard:e5951243ac")
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

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

    runServer {
        minecraftVersion("1.21.11")
    }
}

paperPluginYaml {
    main = "me.elsiff.morefish.MoreFish"
    apiVersion = "1.21.11"
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
