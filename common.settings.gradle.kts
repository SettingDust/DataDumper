import org.gradle.kotlin.dsl.create

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
        maven("https://maven.minecraftforge.net/") { name = "Forge" }
        maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge" }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.spongepowered.mixin") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
        }
    }
}

val kotlin = "1.9.23"
val minecraft = "1.20.1"

settings.extra["kotlin"] = kotlin

settings.extra["minecraft"] = minecraft

dependencyResolutionManagement.versionCatalogs.create("catalog") {
    // https://plugins.gradle.org/plugin/org.jetbrains.gradle.plugin.idea-ext
    plugin("idea-ext", "org.jetbrains.gradle.plugin.idea-ext").version("1.1.8")

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
    version("kotlin", kotlin)
    plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").version(kotlin)
    plugin("kotlin-plugin-serialization", "org.jetbrains.kotlin.plugin.serialization")
        .version(kotlin)

    // https://plugins.gradle.org/plugin/com.diffplug.spotless
    plugin("spotless", "com.diffplug.spotless").version("6.25.0")

    // https://github.com/jmongard/Git.SemVersioning.Gradle
    plugin("semver", "com.github.jmongard.git-semver-plugin").version("0.12.6")

    // https://fabricmc.net/develop/
    plugin("fabric-loom", "fabric-loom").version("1.6.+")

    version("minecraft", minecraft)
    library("minecraft", "com.mojang", "minecraft").version(minecraft)

    library("yarn", "net.fabricmc", "yarn").version("$minecraft+build.10")

    library("fabric-loader", "net.fabricmc", "fabric-loader").version("0.15.7")

    val fabricApi = "0.92.0"
    library("fabric-api", "net.fabricmc.fabric-api", "fabric-api").version("$fabricApi+$minecraft")

    // https://modrinth.com/mod/fabric-language-kotlin/versions
    library(
            "fabric-kotlin",
            "net.fabricmc",
            "fabric-language-kotlin",
        )
        .version("1.10.19+kotlin.$kotlin")

    // https://modrinth.com/mod/modmenu/versions
    library("modmenu", "com.terraformersmc", "modmenu").version("7.2.2")

    /**
     * ***********
     * Quilt
     * ************
     */
    // https://github.com/QuiltMC/quilt-loom
    // Using 1.2 for https://github.com/QuiltMC/quilt-loom/issues/38
    plugin("quilt-loom", "org.quiltmc.loom").version("1.2.+")

    // https://github.com/QuiltMC/quilt-loader/tags
    library(
            "quilt-loader",
            "org.quiltmc",
            "quilt-loader",
        )
        .version("0.24.0")

    // https://modrinth.com/mod/qsl/versions
    library(
            "quilt-fabric-api",
            "org.quiltmc.quilted-fabric-api",
            "quilted-fabric-api",
        )
        .version("7.5.0+0.91.0-$minecraft")

    /**
     * ***********
     * Forge
     * ************
     */
    // https://maven.neoforged.net/#/releases/net/neoforged/gradle/userdev
    plugin("forge-gradle", "net.minecraftforge.gradle").version("6.+")

    // https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html
    library("forge", "net.minecraftforge", "forge").version("$minecraft-47.2.20")

    // https://github.com/SpongePowered/MixinGradle
    plugin("mixin-gradle", "org.spongepowered.mixin").version("0.7-SNAPSHOT")

    // https://modrinth.com/mod/connector/versions
    library("connector", "dev.su5ed.sinytra", "Connector").version("1.0.0-beta.40+$minecraft")
    // https://modrinth.com/mod/forgified-fabric-api/versions
    library("forgified-fabric-api", "dev.su5ed.sinytra.fabric-api", "fabric-api")
        .version("$fabricApi+1.11.5+$minecraft")
    // https://modrinth.com/mod/connector-extras/versions
    library("connector-extras", "maven.modrinth", "connector-extras").version("1.10.0+$minecraft")
}
