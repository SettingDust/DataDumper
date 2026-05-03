package settingdust.data_dumper.neoforge.v21_1.util

import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.entity.player.Player
import settingdust.data_dumper.neoforge.util.PermissionAdapter
import settingdust.data_dumper.util.MinecraftVersion

class PermissionAdapter : PermissionAdapter {
    init {
        MinecraftVersion.V1211.requireCurrent()
    }

    override fun CommandSourceStack.checkPermission(level: Int): Boolean =
        hasPermission(level)

    override fun Player.checkPermission(level: Int): Boolean =
        hasPermissions(level)
}
