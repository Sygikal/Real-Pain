package dev.sygii.realpain.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.realpain.PainAttachments;
import dev.sygii.realpain.body.entity.PlayerBody;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export =true)
@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {

	PersistentProjectileEntity projectile = (PersistentProjectileEntity)(Object)this;

	@Inject(method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;"))
	private void handleDamage(CallbackInfo info, @Local HitResult result) {
		Entity entity = ((EntityHitResult) result).getEntity();
		if (!entity.getWorld().isClient && entity instanceof PlayerEntity) {
			PainAttachments.body.get(entity).hitList.put((PlayerEntity) entity, Pair.of(projectile, result));
			//System.out.println(PainAttachments.body.get(entity).hitList);
		}
	}
}
