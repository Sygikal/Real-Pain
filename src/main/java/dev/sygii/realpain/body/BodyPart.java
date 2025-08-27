package dev.sygii.realpain.body;

import dev.sygii.realpain.PainClient;
import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.debuff.HitDebuff;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BodyPart {
    public final LivingEntity entity;
    public final float healthWeight;
    public final BodySide side;
    public final boolean fatal;

    public Body mainBody;

    public float health;

    public Identifier id;
    public BodyPart parent;
    public List<BodyPart> children = new ArrayList<>();
    //public float maxHealth;

    public final List<HitDebuff> hitDebuffs = new ArrayList<>();
    public final List<EquipmentSlot> activeSlots = new ArrayList<>();


    public BodyPart(LivingEntity entity, float healthWeight) {
        this(entity, healthWeight, BodySide.CENTER, false);
    }

    public BodyPart(LivingEntity entity, float healthWeight, BodySide side) {
        this(entity, healthWeight, side, false);
    }

    public BodyPart(LivingEntity entity, float healthWeight, boolean fatal) {
        this(entity, healthWeight, BodySide.CENTER, fatal);
    }

    public BodyPart(LivingEntity entity, float healthWeight, BodySide side, boolean fatal) {
        this.entity = entity;
        this.healthWeight = healthWeight;
        this.side = side;
        this.fatal = fatal;
    }

    public void renderModel( PlayerEntityModel<AbstractClientPlayerEntity> model, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {

    }

    public void renderFlat(DrawContext context, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {

    }

    public int getColor() {
        int[] damageColors = {0xffb70000, 0xfff87c00, 0xffffd966, 0xff8fce00, 0xff6db54e};
        float healthPercentage = this.getPartHealth() / this.getPartMaxHealth();
        int size = damageColors.length - 1;
        //int color =  damageColors[Math.round(size * healthPercentage)];
        //System.out.println(healthPercentage);
        Color merged = interpolateColorHue(new Color(0xb70000), new Color(0x60db2c), healthPercentage);
        return merged.getRGB();
    }

    public float[] getColors() {
        int[] damageColors = {0xffb70000, 0xfff87c00, 0xffffd966, 0xff8fce00, 0xff6db54e};
        float healthPercentage = this.getPartHealth() / this.getPartMaxHealth();
        int size = damageColors.length - 1;
        //int color =  damageColors[Math.round(size * healthPercentage)];
        Color merged = interpolateColorHue(new Color(0xb70000), new Color(0x60db2c), healthPercentage);
        int color = merged.getRGB();
        int j = (color & 0xFF0000) >> 16;
        int k = (color & 0xFF00) >> 8;
        int l = (color & 0xFF) >> 0;
        return new float[]{j / 255.0F, k / 255.0F, l / 255.0F};
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));

        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

        Color resultColor = Color.getHSBColor(interpolateFloat(color1HSB[0], color2HSB[0], amount),
                interpolateFloat(color1HSB[1], color2HSB[1], amount), interpolateFloat(color1HSB[2], color2HSB[2], amount));

        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public void heal(float amount) {
        float remainingToHeal = this.getPartMaxHealth() - this.health;
        if (amount > remainingToHeal) {
            float remaining = amount - remainingToHeal;
            int childSize = 0;
            for (BodyPart child : this.children) {
                if (child.getPartHealth() < child.getPartMaxHealth()) {
                    childSize++;
                }
            }
            for (BodyPart child : this.children) {
                child.heal(amount / childSize);
            }
        }
        setHealth(this.health + amount);
    }

    public void takeDamage(float amount) {
        /*float og = amount;
        if (entity instanceof PlayerEntity player) {
            float armor = 0;
            double toughness = 0;
            for (EquipmentSlot slot : this.activeSlots) {
                if (player.getInventory().getArmorStack(slot.getEntitySlotId()).getItem() instanceof ArmorItem armorItem) {
                    armor += armorItem.getProtection();
                    toughness += armorItem.getToughness();
                    //PainMain.log("Adding needed " + armorItem.getProtection() + " | " + armorItem.getToughness());
                }
            }
            amount = DamageUtil.getDamageLeft(amount, armor, (float) toughness);
        }
        System.out.println(og + " | " + amount + " " + id);*/


        if (amount > this.health) {
            float remaining = amount - this.health;
            if (this.parent != null) {
                this.parent.takeDamage(remaining);
            }
        }
        setHealth(this.health - amount);
        if (this.fatal && getPartHealth() <= 0) {
            entity.setHealth(0);
        }

        for (HitDebuff hitDebuff : this.hitDebuffs) {
            if (getPartHealth() <= getPartMaxHealth() * hitDebuff.threshold()) {
                entity.addStatusEffect(new StatusEffectInstance(hitDebuff.effect(), hitDebuff.duration(), hitDebuff.amplifier()));
            }
        }
        //System.out.println(this.id + " | " + this.health);
    }

    public void setHealth(float amount) {
        this.health = Math.min(Math.max(amount, 0), getPartMaxHealth());
    }

    /*public void setMaxHealth(float amount) {
        this.health = Math.min(Math.max(amount, 0), getPartMaxHealth());
    }*/

    public float getPartHealth() {
        //return this.entity.getHealth() * this.healthPercentage;
        return this.health;
    }

    public float getPartMaxHealth() {
        if (this.entity == null || this.entity.isDead()) {
            return 1;
        }
        return this.entity.getMaxHealth() * (this.healthWeight / mainBody.bodyWeight);
    }

    public void addChild(BodyPart child) {
        this.children.add(child);
    }

    public BodyPart setParent(BodyPart parent) {
        parent.addChild(this);
        this.parent = parent;
        return this;
    }

    public enum BodySide {
        CENTER,
        LEFT,
        RIGHT;
    }
}
