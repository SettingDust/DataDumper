package settingdust.data_dumper.v20_1.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import settingdust.data_dumper.util.CommonIdentifier;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin implements CommonIdentifier {
}
