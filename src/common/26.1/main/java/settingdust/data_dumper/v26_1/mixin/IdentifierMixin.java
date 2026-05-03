package settingdust.data_dumper.v26_1.mixin;

import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import settingdust.data_dumper.util.CommonIdentifier;

@Mixin(Identifier.class)
public abstract class IdentifierMixin implements CommonIdentifier {
}
