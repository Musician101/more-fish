import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.4.3"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
}

group = "me.elsiff"
version = "4.4.0"

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    paperweight.paperDevBundle("26.2.build.+")
    compileOnlyApi("com.github.MilkBowl:VaultAPI:1.7.1")
    api("com.github.Musician101.MusiGUI:paper:c4f5089b33")
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
        minecraftVersion("26.2")
    }
}

paperPluginYaml {
    main = "me.elsiff.morefish.MoreFish"
    apiVersion = "26.2"
    authors.addAll("elsiff", "Musician101")
    foliaSupported = true
    dependencies.server {
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
