package settingdust.data_dumper.v26_1.util

import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import settingdust.data_dumper.util.ComponentEventAdapter
import settingdust.data_dumper.util.MinecraftVersion

class ComponentEventAdapter : ComponentEventAdapter {
    init {
        MinecraftVersion.V261.requireCurrent()
    }

    override fun showText(text: Component): HoverEvent {
        return HoverEvent.ShowText(text)
    }

    override fun copyToClipboard(text: String): ClickEvent {
        return ClickEvent.CopyToClipboard(text)
    }
}
