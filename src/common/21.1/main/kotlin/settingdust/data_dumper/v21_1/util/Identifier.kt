package settingdust.data_dumper.v21_1.util

import net.minecraft.resources.ResourceLocation
import settingdust.data_dumper.util.Identifier

fun Identifier.toNative(): ResourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, path)

fun ResourceLocation.toCommon(): Identifier = Identifier(namespace, path)
