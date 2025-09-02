package dev.sygii.realpain.body;

import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.damage.DamageDistribution;
import dev.sygii.realpain.body.damage.distribution.ProjectileDamageDistribution;
import dev.sygii.realpain.body.damage.distribution.SplitAllDamageDistribution;
import dev.sygii.realpain.body.debuff.DebuffRunner;
import dev.sygii.realpain.body.debuff.HitDebuff;
import dev.sygii.realpain.body.debuff.TickedDebuff;
import dev.sygii.realpain.body.entity.PlayerBody;
import dev.sygii.realpain.util.PlayerSizeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Body {
    public final HashMap<Identifier, BodyPart> bodyParts = new HashMap<>();
    public final HashMap<EquipmentSlot, ArrayList<BodyPart>> slotToPart = new HashMap<>();
    public final LivingEntity entity;

    public float bodyWeight;

    public final Map<PlayerEntity, Pair<Entity, HitResult>> hitList = new WeakHashMap<>();
    //public final List<TickedDebuff> tickedDebuffs = new ArrayList<>();
    public final Map<StatusEffect, List<TickedDebuff>> debuffs = new WeakHashMap<>();

    public Body(LivingEntity entity) {
        this.entity = entity;
    }

    public DamageDistribution currentDistribution = null;

    public boolean fatality = false;
    public boolean newlyLoaded = false;


    public void initBodyParts() {
        for (BodyPart part : this.bodyParts.values()) {
            if (entity instanceof PlayerEntity) {
                //PainMain.log("setting part ; " + part.id );
            }
            part.health = part.getPartMaxHealth();
        }
    }

    public void addBodyPart(Identifier id, BodyPart part) {
        part.id = id;
        part.mainBody = this;
        this.bodyWeight += part.healthWeight;
        bodyParts.put(part.id, part);
    }

    public void addSlotPart(EquipmentSlot slot, Identifier id) {
        if (slotToPart.get(slot) == null) {
            slotToPart.put(slot, new ArrayList<BodyPart>());
        }
        slotToPart.get(slot).add(getPart(id));
    }

    public void addTickedDebuff(Identifier id, StatusEffect effect, int duration, DebuffRunner runner) {
        if (debuffs.get(effect) == null) {
            debuffs.put(effect, new ArrayList<TickedDebuff>());
        }
        debuffs.get(effect).add(new TickedDebuff(getPart(id), duration, runner));
    }

    public void tick() {

    }

    public DamageDistribution getDistribution(DamageSource source) {
        for (DamageDistribution dist : PainMain.damageDistributionMap) {
            dist.getRealParts().clear();
            for (Identifier partId : dist.getBodyPartsList()) {
                dist.addRealPart(getPart(partId));
            }
            if (dist.shouldApply(source, entity)) {
                return dist;
            }
        }
        PainMain.log("Falling back to split damage");
        return new SplitAllDamageDistribution();
    }

    public DamageDistribution get() {
        if (this.currentDistribution != null) {
            return this.currentDistribution;
        }
        PainMain.log("Damage was somehow null");
        return new SplitAllDamageDistribution();
    }

    public void setDistribution(DamageSource source) {
        PainMain.log(source.toString());
        this.currentDistribution = getDistribution(source);
    }

    public float applyDamage(float original, float amount) {
        DamageDistribution dist = get();
        PainMain.log(dist.getIdentifier() + " " + amount);
        dist.handleDamage(amount, dist.getRealParts(), entity, this);

        if (isFatal()) {
            setFatality(false);
            return 0;
        }

        return original;

        /*List<EquipmentSlot> meleeSlots = PlayerSizeHelper.getMeleeDistribution((PlayerEntity) entity, source);
        if (meleeSlots != null) {
            PainMain.log("Falling back to melee damage");
            meleeSlots.forEach(slot -> {
                if (getPartForSlot(slot) != null) {
                    for (BodyPart bodyPart : getPartForSlot(slot)) {
                        bodyPart.takeDamage(amount / (getPartForSlot(slot).size() * meleeSlots.size()));
                    }
                }
            });
            return;
        }
        PainMain.log("Falling back to split damage");
        getBodyParts().values().forEach(part -> {
            part.takeDamage(amount / getBodyParts().size());
        });*/
    }

    public boolean isFatal() {
        return fatality;
    }

    public void setFatality(boolean fatal) {
        this.fatality = fatal;
    }

    public void applyHealing(float amount) {

    }

    public BodyPart getPart(Identifier identifier){
        return getBodyParts().get(identifier);
    }

    public List<BodyPart> getPartForSlot(EquipmentSlot slot){
        return slotToPart.get(slot);
    }

    public HashMap<Identifier, BodyPart> getBodyParts() {
        return bodyParts;
    }
}
