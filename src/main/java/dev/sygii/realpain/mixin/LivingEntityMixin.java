package dev.sygii.realpain.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.realpain.PainAttachments;
import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.damage.DamageDistribution;
import dev.sygii.realpain.body.entity.PlayerBody;
import dev.sygii.realpain.util.PlayerSizeHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Debug(export =true)
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	LivingEntity entity = (LivingEntity)(Object)this;

	@Unique
	boolean shouldKillAfterOverdose = false;

	/*@Inject(method = "heal",
			at = @At(value = "HEAD"))
	private void handleHealing(CallbackInfo info, @Local(argsOnly = true) float amount) {
		if (entity instanceof PlayerEntity player) {
			PainAttachments.body.get(player).applyHealing(amount);
			if (player instanceof ServerPlayerEntity serverPlayerEntity) {
				PlayerBody body = PainAttachments.body.get(serverPlayerEntity);
				PainAttachments.body.syncFromServer(serverPlayerEntity, entity, body);
			}
		}
	}*/

	@Inject(method = "applyArmorToDamage",
			at = @At(value = "HEAD"), cancellable = true)
	private void handleArmor(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
		if (entity instanceof PlayerEntity player && !source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
			PlayerBody body = PainAttachments.body.get(entity);
			DamageDistribution dist = body.currentDistribution;

			int[] slots = {0, 1, 2, 3};
			float armor = entity.getArmor();
			double toughness = entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
			for (int slot : slots) {
				if (player.getInventory().getArmorStack(slot).getItem() instanceof ArmorItem armorItem) {
					armor -= armorItem.getProtection();
					toughness -= armorItem.getToughness();
					//PainMain.log("Subtracting " + armorItem.getProtection() + " | " + armorItem.getToughness());
				}
			}

			List<EquipmentSlot> slotsToScan = new ArrayList<>();
			for (BodyPart part : dist.getRealParts()) {
				for (EquipmentSlot slot : part.activeSlots) {
					if (!slotsToScan.contains(slot)) {
						slotsToScan.add(slot);
					}
				}
			}

			for (EquipmentSlot slot : slotsToScan) {
				if (player.getInventory().getArmorStack(slot.getEntitySlotId()).getItem() instanceof ArmorItem armorItem) {
					player.getInventory().damageArmor(source, amount, new int[]{slot.getEntitySlotId()});
					armor += armorItem.getProtection();
					toughness += armorItem.getToughness();
					PainMain.log("Adding needed " + armorItem.getProtection() + " | " + armorItem.getToughness());
				}
			}

			PainMain.log("Getting leftover damage with: " + armor + "a " + toughness + "t | " + amount + " -> " +  DamageUtil.getDamageLeft(amount, armor, (float) toughness));
			cir.setReturnValue(DamageUtil.getDamageLeft(amount, armor, (float) toughness));
		}
	}

	@Inject(method = "setHealth",
			at = @At(value = "HEAD"))
	private void handleSetHealth(CallbackInfo info, @Local(argsOnly = true) float health) {
		float f = entity.getHealth();
		float amount = health - f;
		if (entity instanceof PlayerEntity player) {
			if (PainAttachments.body.get(player) != null) {
				PainAttachments.body.get(player).applyHealing(amount);
				if (player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
					PlayerBody body = PainAttachments.body.get(serverPlayerEntity);
					PainAttachments.body.syncFromServer(serverPlayerEntity, entity, body);
				}
			}
		}
	}

	@ModifyArg(method = "clearStatusEffects",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onStatusEffectRemoved(Lnet/minecraft/entity/effect/StatusEffectInstance;)V"))
	private StatusEffectInstance handleOverdoseRemoval(StatusEffectInstance effect) {
		if (effect.getEffectType().equals(PainMain.OVERDOSE_EFFECT)) {
			shouldKillAfterOverdose = true;
		}
		return effect;
	}

	@Inject(method = "clearStatusEffects",
			at = @At(value = "TAIL"))
	private void handleOverdoseRemoval2(CallbackInfoReturnable<Boolean> cir) {
		if (shouldKillAfterOverdose) {
			entity.damage(PainMain.of(entity.getWorld(), PainMain.OVERDOSE_DAMAGE_TYPE), Float.MAX_VALUE);
			shouldKillAfterOverdose = false;
		}
	}
}
