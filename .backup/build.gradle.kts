import groovy.lang.Closure

plugins {
    idea
    java
    `maven-publish`
    alias(catalog.plugins.idea.ext)

    alias(catalog.plugins.kotlin.jvm)
    alias(catalog.plugins.kotlin.plugin.serialization)

    alias(catalog.plugins.git.version)

    alias(catalog.plugins.fabric.loom)
}

apply(
    "https://github.com/SettingDust/MinecraftGradleScripts/raw/main/gradle_issue_15754.gradle.kts"
)

group = "settingdust.datadumper"

val gitVersion: Closure<String> by extra

version = gitVersion()

val id: String by rootProject.properties
val name: String by rootProject.properties
val author: String by rootProject.properties
val description: String by rootProject.properties

subprojects {
    apply(plugin = "idea")
    apply(plugin = "java")

    base { archivesName.set("${rootProject.base.archivesName.get()}${project.path.replace(":", "-")}") }

    group = rootProject.group
    version = rootProject.version
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(17) }

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
}

loom {
    mixin {
        defaultRefmapName = "$id.refmap.json"

        add("main", "$id.refmap.json")
    }

    accessWidenerPath = file("src/main/resources/$id.accesswidener")

    mods { register(id) { sourceSet(sourceSets["main"]) } }
}

dependencies {
    minecraft(catalog.minecraft.fabric)
    mappings(variantOf(catalog.mapping.yarn) { classifier("v2") })

    modImplementation(catalog.fabric.loader)
    modImplementation(catalog.fabric.api)
    modImplementation(catalog.fabric.kotlin)

    modImplementation(catalog.modmenu)

    modImplementation(catalog.fabric.permissions)

    include(project(":versions:1.20.1"))
    include(project(":versions:1.21"))
}

val metadata =
    mapOf(
        "group" to group,
        "author" to author,
        "id" to id,
        "name" to name,
        "version" to version,
        "description" to description,
        "source" to "https://github.com/SettingDust/DataDumper",
        "minecraft" to ">=1.20.1 <1.22",
        "fabric_loader" to ">=0.15",
        "fabric_kotlin" to ">=1.11",
        "modmenu" to "*",
    )

tasks {
    withType<ProcessResources> {
        inputs.properties(metadata)
        filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(metadata) }
    }
}
