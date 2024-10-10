extra["minecraft"] = "1.20.1"

apply("https://github.com/SettingDust/MinecraftGradleScripts/raw/main/common.gradle.kts")

apply("https://github.com/SettingDust/MinecraftGradleScripts/raw/main/kotlin.gradle.kts")

apply("https://github.com/SettingDust/MinecraftGradleScripts/raw/main/fabric.gradle.kts")

apply("https://github.com/SettingDust/MinecraftGradleScripts/raw/main/modmenu.gradle.kts")

dependencyResolutionManagement.versionCatalogs.named("catalog") {
    library("minecraft-fabric-1.21", "com.mojang", "minecraft").version("1.21")

    library("fabric-permissions", "me.lucko", "fabric-permissions-api").version("0.2-SNAPSHOT")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val name: String by settings

rootProject.name = name

include("versions")
include("versions:1.20.1")
include("versions:1.21")
