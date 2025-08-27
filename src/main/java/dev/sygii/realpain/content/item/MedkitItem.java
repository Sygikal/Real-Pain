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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class MedkitItem extends Item {
    private final int maxUses = PainMain.MAIN.ITEMS.medkitMaxUses;

    public MedkitItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.getHealth() < user.getMaxHealth()) {
            return ItemUsage.consumeHeldItem(world, user, hand);
        }

        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks >= 0 && user instanceof PlayerEntity playerEntity) {
            Vec3d vec3d = playerEntity.getVelocity();
            playerEntity.setVelocity(0, vec3d.y, 0);
            int i = this.getMaxUseTime(stack) - remainingUseTicks + 1;
            if (i % 10 == 5) {
                world.playSound(playerEntity, playerEntity.getBlockPos(), SoundEvents.BLOCK_WEEPING_VINES_PLACE, SoundCategory.BLOCKS, 1.0f, 0.4f);
            }
            if (i % 30 == 5) {
                world.playSound(playerEntity, playerEntity.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.1f, (float) (i + 100) / this.getMaxUseTime(stack));
            }
        } else {
            user.stopUsingItem();
        }
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;

        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains("Uses")) {
            nbt.put("Uses", NbtInt.of(maxUses));
        }

        int uses = nbt.getInt("Uses");

        if (!world.isClient) {
            user.heal(20);
        }

        nbt.put("Uses", NbtInt.of(--uses));

        if (playerEntity != null) {
            world.playSound(playerEntity, playerEntity.getBlockPos(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 0.2f, 1.8f);
            if (uses <= 0) {
                playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                if (!playerEntity.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
            }
        }

        return stack;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        int uses = maxUses;
        if (nbt != null && nbt.contains("Uses")) {
            uses = nbt.getInt("Uses");
        }
        return Math.round(uses * 13.0F / this.maxUses);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.packRgb(1.0F, 0.4F, 0.4F);
    }

    public int getMaxUseTime(ItemStack stack) {
        return 240;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

}
