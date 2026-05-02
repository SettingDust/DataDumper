package settingdust.data_dumper.v26_1.util

import net.minecraft.resources.Identifier as MinecraftIdentifier
import settingdust.data_dumper.util.Identifier

fun Identifier.toNative(): MinecraftIdentifier = MinecraftIdentifier.fromNamespaceAndPath(namespace, path)

fun MinecraftIdentifier.toCommon(): Identifier = Identifier(namespace, path)
