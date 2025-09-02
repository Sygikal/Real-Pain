package dev.sygii.realpain.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.realpain.PainAttachments;
import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.entity.PlayerBody;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export =true)
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	PlayerEntity entity = (PlayerEntity)(Object)this;

	@Inject(method = "<init>",
			at = @At("TAIL"))
	private void init(CallbackInfo info) {
		PainAttachments.body.set(entity, new PlayerBody(entity));
		/*if (entity instanceof ServerPlayerEntity) {
			PainMain.log("server init: " + entity.getMaxHealth());
		}
		if (entity instanceof ClientPlayerEntity) {
			PainMain.log("client init: " + entity.getMaxHealth());
		}*/
	}

	@Inject(method = "applyDamage",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"))
	private void setDistribution(CallbackInfo info, @Local(argsOnly = true) DamageSource source, @Local(argsOnly = true) float amount) {
		PainAttachments.body.get(entity).setDistribution(source);
	}

	/*@Inject(method = "applyDamage",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"))
	private void handleDamage(CallbackInfo info, @Local(argsOnly = true) DamageSource source, @Local(argsOnly = true) float amount) {
		PainAttachments.body.get(entity).applyDamage(source, amount);
		if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
			PlayerBody body = PainAttachments.body.get(serverPlayerEntity);
			PainAttachments.body.syncFromServer(serverPlayerEntity, entity, body);
		}
	}*/

	@Inject(method = "tick",
			at = @At(value = "TAIL"))
	private void tickBody(CallbackInfo info) {
		if (!entity.getWorld().isClient) {
			PainAttachments.body.get(entity).tick();
		}
	}
}
