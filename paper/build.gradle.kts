import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
}

repositories {
    maven("https://libraries.minecraft.net")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
    // mcMMO depends on WorldGuard, but Gradle failed to find it
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
}

dependencies {
    api(project(":common"))
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    compileOnlyApi("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnlyApi("com.gmail.nossr50.mcMMO:mcMMO:2.2.048")
    api("com.github.Musician101.MusiGUI:paper:c4f5089b33")
    api("com.github.Musician101.MusiCommand:paper:be49f96ace")
    api("com.github.Musician101:MusiBoard:e5951243ac")
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
