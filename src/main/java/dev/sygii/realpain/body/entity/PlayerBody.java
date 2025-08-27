package dev.sygii.realpain.body.entity;

import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.Body;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.debuff.TickedDebuff;
import dev.sygii.realpain.body.parts.Arm;
import dev.sygii.realpain.body.parts.Head;
import dev.sygii.realpain.body.parts.Leg;
import dev.sygii.realpain.body.parts.Torso;
import dev.sygii.realpain.util.PlayerSizeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.hit.HitResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerBody extends Body {

    public PlayerBody(PlayerEntity entity) {
        super(entity);
        addBodyPart(PainMain.id("torso"), new Torso(entity));
        addBodyPart(PainMain.id("head"), new Head(entity).setParent(this.getPart(PainMain.id("torso"))));
        addBodyPart(PainMain.id("left_arm"), new Arm(entity, BodyPart.BodySide.LEFT).setParent(this.getPart(PainMain.id("torso"))));
        addBodyPart(PainMain.id("right_arm"), new Arm(entity, BodyPart.BodySide.RIGHT).setParent(this.getPart(PainMain.id("torso"))));
        addBodyPart(PainMain.id("left_leg"), new Leg(entity, BodyPart.BodySide.LEFT).setParent(this.getPart(PainMain.id("torso"))));
        addBodyPart(PainMain.id("right_leg"), new Leg(entity, BodyPart.BodySide.RIGHT).setParent(this.getPart(PainMain.id("torso"))));
        addSlotPart(EquipmentSlot.CHEST, PainMain.id("torso"));
        addSlotPart(EquipmentSlot.CHEST, PainMain.id("left_arm"));
        addSlotPart(EquipmentSlot.CHEST, PainMain.id("right_arm"));
        addSlotPart(EquipmentSlot.HEAD, PainMain.id("head"));
        addSlotPart(EquipmentSlot.LEGS, PainMain.id("left_leg"));
        addSlotPart(EquipmentSlot.LEGS, PainMain.id("right_leg"));
        addSlotPart(EquipmentSlot.FEET, PainMain.id("left_leg"));
        addSlotPart(EquipmentSlot.FEET, PainMain.id("right_leg"));

        //addHitDebuff(PainMain.id("head"), StatusEffects.BLINDNESS, 0.3f, 40);
        /*if (this.getPartHealth() <= (this.getPartMaxHealth() * 0.3f)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40, 0));
        }*/

        addTickedDebuff(PainMain.id("torso"), StatusEffects.WEAKNESS, (part) -> {
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.25f) {
                return 1;
            }
            return 0;
        });

        addTickedDebuff(PainMain.id("head"), StatusEffects.WEAKNESS, (part) -> {
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.25f) {
                return 1;
            }
            return 0;
        });

        addTickedDebuff(PainMain.id("head"), StatusEffects.NAUSEA, (part) -> {
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.15f) {
                return 1;
            }
            return 0;
        });

        addTickedDebuff(PainMain.id("right_leg"), StatusEffects.SLOWNESS, (part) -> {
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.25f) {
                return 3;
            }
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.5f) {
                return 2;
            }
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.75f) {
                return 1;
            }
            return 0;
        });

        addTickedDebuff(PainMain.id("left_leg"), StatusEffects.SLOWNESS, (part) -> {
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.25f) {
                return 3;
            }
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.5f) {
                return 2;
            }
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.75f) {
                return 1;
            }
            return 0;
        });

        addTickedDebuff(PainMain.id("right_arm"), StatusEffects.MINING_FATIGUE, (part) -> {
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.25f) {
                return 3;
            }
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.5f) {
                return 2;
            }
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.75f) {
                return 1;
            }
            return 0;
        });

        addTickedDebuff(PainMain.id("left_arm"), StatusEffects.MINING_FATIGUE, (part) -> {
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.25f) {
                return 3;
            }
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.5f) {
                return 2;
            }
            if (part.getPartHealth() <= part.getPartMaxHealth() * 0.75f) {
                return 1;
            }
            return 0;
        });

        initBodyParts();
    }

    /*@Override
    public void applyDamage(DamageSource source, float amount) {
        PainMain.log("ASD" + amount);
        if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            Pair<Entity, HitResult> rayTraceResult = hitList.remove(entity);
            if (rayTraceResult != null) {
                Entity entityProjectile = rayTraceResult.getLeft();
                EquipmentSlot slot = PlayerSizeHelper.getSlotTypeForProjectileHit(entityProjectile, (PlayerEntity) entity);
                PainMain.log("Projectile to :" + slot);
                if (getPartForSlot(slot) != null) {
                    getPartForSlot(slot).forEach(part -> {
                        part.takeDamage(amount / getPartForSlot(slot).size());
                    });
                }
                return;
            }
        }
        super.applyDamage(source, amount);
    }*/

    @Override
    public void applyHealing(float amount) {
        if (amount > 0) {
            getPart(PainMain.id("torso")).heal(amount);
        }
    }

    @Override
    public void tick() {
        int morphineAmount = entity.hasStatusEffect(PainMain.MORPHINE_EFFECT) ? entity.getStatusEffect(PainMain.MORPHINE_EFFECT).getAmplifier() + 2 : 1;
        //System.out.println(getAmplifier(getPart(PainMain.id("right_leg"))));
        //int amplifier;
        /*//legs and foot
        amplifier = -morphineAmount;
        amplifier += getAmplifier(getPart(PainMain.id("right_leg")));
        amplifier += getAmplifier(getPart(PainMain.id("left_leg")));
        applyStatusEffectWithAmplifier(StatusEffects.SLOWNESS, amplifier);

        //arms
        amplifier = -morphineAmount;
        amplifier += getAmplifier(getPart(PainMain.id("right_arm")));
        amplifier += getAmplifier(getPart(PainMain.id("left_arm")));
        applyStatusEffectWithAmplifier(StatusEffects.MINING_FATIGUE, amplifier);

        //torso
        amplifier = -morphineAmount;
        amplifier += getAmplifier(getPart(PainMain.id("torso")));
        amplifier += getAmplifier(getPart(PainMain.id("head")));
        applyStatusEffectWithAmplifier(StatusEffects.WEAKNESS, amplifier);*/
        this.debuffs.forEach((effect, debuffs) -> {
            AtomicInteger amplifier = new AtomicInteger(-morphineAmount);
            debuffs.forEach((debuff -> {
                amplifier.getAndAdd(debuff.runner().run(debuff.part()));
            }));
            //System.out.println(amplifier.get());
            applyStatusEffectWithAmplifier(effect, amplifier.get());
        });
    }

    public void applyStatusEffectWithAmplifier(StatusEffect effect, int amplifier){
        if(amplifier >= 0){
            StatusEffectInstance s = entity.getStatusEffect(effect);
            if(s == null){
                entity.addStatusEffect(new StatusEffectInstance(effect, 40, amplifier));
            }else if(s.getDuration() <= 5 || s.getAmplifier() != amplifier){
                entity.addStatusEffect(new StatusEffectInstance(effect, 40, amplifier));
            }
        }
    }

    /*public void applyStatusEffectWithAmplifier(StatusEffect effect, int amplifier){
        if(amplifier >= 0){
            StatusEffectInstance s = entity.getStatusEffect(effect);
            if(s == null){
                entity.addStatusEffect(new StatusEffectInstance(effect, 80, amplifier));
            }else if(s.getDuration() <= 5){
                entity.addStatusEffect(new StatusEffectInstance(effect, 80, amplifier));
            }else if(s.getAmplifier() < amplifier) {
                amplifier += s.getAmplifier();
                entity.addStatusEffect(new StatusEffectInstance(effect, 80, amplifier));
            }
        }
    }*/

    public int getAmplifier(BodyPart part){
        float threshold1 = part.getPartMaxHealth() * 0.75f;
        float threshold2 = part.getPartMaxHealth() * 0.5f;
        float threshold3 = part.getPartMaxHealth() * 0.25f;
        /*if(part.getPartHealth() <= threshold) {
            return Math.round(threshold / (part.getPartHealth() + 0.1f));
        }*/

        if(part.getPartHealth() <= threshold3) {
            return 3;
        }
        if(part.getPartHealth() <= threshold2) {
            return 2;
        }
        if(part.getPartHealth() <= threshold1) {
            return 1;
        }

        return 0;
    }
}
