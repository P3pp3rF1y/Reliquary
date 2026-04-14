package reliquary.mixin;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reliquary.entity.ReliquaryFakePlayer;

@Mixin(Entity.class)
public class MixinEntity {
	@Inject(method = "syncData", at = @At("HEAD"), cancellable = true)
	private void skipSyncForReliquaryFakePlayer(AttachmentType<?> type, CallbackInfo ci) {
		if ((Object) this instanceof ReliquaryFakePlayer) {
			ci.cancel();
		}
	}
}
