package dev.sygii.realpain.content.item;

import dev.sygii.realpain.PainAttachments;
import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.entity.PlayerBody;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BandageItem extends Item {

    public BandageItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //user.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
        PlayerBody body = PainAttachments.body.get(user);
        for (BodyPart part : body.getBodyParts().values()) {
            if (part.getPartHealth() < (part.getPartMaxHealth() * PainMain.MAIN.ITEMS.bandageThreshold.get())) {
                return ItemUsage.consumeHeldItem(world, user, hand);
            }
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    /*@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if(playerEntity.getHealth() < playerEntity.getMaxHealth()){
            ItemStack itemStack = playerEntity.getStackInHand(hand);
            playerEntity.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
            playerEntity.heal(2);
            itemStack.decrement(1);
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(playerEntity.getStackInHand(hand));
    }*/

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks >= 0 && user instanceof PlayerEntity playerEntity) {
                int i = this.getMaxUseTime(stack) - remainingUseTicks + 1;
                boolean bl = i % 12 == 5;
                if (bl) {
                    //user.heal(1f);
                    //*this.addDustParticles(world, blockHitResult, blockState, user.getRotationVec(0.0F), arm);
                    //user.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
                    world.playSound(playerEntity, playerEntity.getBlockPos(), SoundEvents.BLOCK_WEEPING_VINES_PLACE, SoundCategory.BLOCKS, 1.0f, 0.8f);
                }
        } else {
            user.stopUsingItem();
        }
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;

        if (!world.isClient) {
            /*PlayerBody body = PainAttachments.body.get(user);
            for (BodyPart part : body.getBodyParts().values()) {
                if (part.getPartHealth() < (part.getPartMaxHealth() * PainMain.MAIN.ITEMS.bandageThreshold.get())) {
                    part.heal(PainMain.MAIN.ITEMS.bandageHealAmount);
                    PainAttachments.body.syncFromServer((ServerPlayerEntity) playerEntity, playerEntity, body);
                    break;
                }
            }*/
            user.heal(PainMain.MAIN.ITEMS.bandageHealAmount);
        }

        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return stack;
    }

    public int getMaxUseTime(ItemStack stack) {
        return PainMain.MAIN.ITEMS.bandageUseTime;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

}
