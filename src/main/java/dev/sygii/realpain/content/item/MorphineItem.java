package dev.sygii.realpain.content.item;

import dev.sygii.realpain.PainMain;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class MorphineItem extends Item {

    public MorphineItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    /*public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        playerEntity.addStatusEffect(new StatusEffectInstance(PainMain.MORPHINE_EFFECT, 3600, 0));
        itemStack.decrement(1);
        return TypedActionResult.consume(itemStack);
    }*/

    /*@Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        entity.addStatusEffect(new StatusEffectInstance(PainMain.MORPHINE_EFFECT, 3600, 0));
        itemStack.decrement(1);
        return ActionResult.CONSUME;
    }*/

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;

        if (!world.isClient) {
            boolean overdosed = false;
            int amplifier = 0;
            int additionalTime = 0;
            if (user.hasStatusEffect(PainMain.MORPHINE_EFFECT)) {
                amplifier = user.getStatusEffect(PainMain.MORPHINE_EFFECT).getAmplifier() + 1;
                if (PainMain.MAIN.ITEMS.additionalMorphineTime) {
                    additionalTime += amplifier * PainMain.MAIN.ITEMS.additionalMorphineTimeModifier;
                }
                if (PainMain.MAIN.ITEMS.allowOverdosing) {
                    if (amplifier > 4) {
                        overdosed = true;
                        user.getWorld()
                                .playSound(
                                        playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F
                                );
                        //world.playSoundFromEntity(playerEntity, user, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.PLAYERS, 1.0f, 0.8f);
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 5000, 4));
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 5000, 4));
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 5000, 4));
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5000, 4));
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 5000, 4));
                    }
                }
            }
            if (overdosed) {
                int odAmount = PainMain.incrementEffect(user, PainMain.OVERDOSE_EFFECT, 9000);
                PainMain.OVERDOSE_CRITERION.trigger((ServerPlayerEntity) user, odAmount + 1);
            }else {
                user.addStatusEffect(new StatusEffectInstance(PainMain.MORPHINE_EFFECT, PainMain.MAIN.ITEMS.morphineDuration + additionalTime, amplifier));
                PainMain.MORPHINE_CRITERION.trigger((ServerPlayerEntity) user, amplifier + 1);

            }
        }else {
            int amplifier = 0;
            if (user.hasStatusEffect(PainMain.MORPHINE_EFFECT)) {
                amplifier = user.getStatusEffect(PainMain.MORPHINE_EFFECT).getAmplifier() + 1;
                if (PainMain.MAIN.ITEMS.allowOverdosing) {
                    if (amplifier > 4) {
                        user.getWorld()
                                .playSound(
                                        playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F
                                );
                    }
                }
            }
        }

        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        user.emitGameEvent(GameEvent.DRINK);

        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (playerEntity != null) {
                playerEntity.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }

    public int getMaxUseTime(ItemStack stack) {
        return PainMain.MAIN.ITEMS.morphineUseTime;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

}
