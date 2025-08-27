package dev.sygii.realpain.body.damage.distribution;

import dev.sygii.realpain.PainAttachments;
import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.Body;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.damage.DamageDistribution;
import dev.sygii.realpain.util.PlayerSizeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeleeDamageDistribution extends DamageDistribution {
    public static Identifier ID = PainMain.id("melee");
    List<EquipmentSlot> meleeSlots = null;
    List<BodyPart> meleeParts = new ArrayList<>();

    public MeleeDamageDistribution() {
        super(ID);
    }

    @Override
    public boolean shouldApply(DamageSource source, LivingEntity entity) {
        meleeSlots = PlayerSizeHelper.getMeleeDistribution((PlayerEntity) entity, source);
        if (meleeSlots != null){
            meleeSlots.forEach(slot -> {
                PainAttachments.body.get(entity).getPartForSlot(slot).forEach((part) -> {
                    if (!meleeParts.contains(part)) {
                        meleeParts.add(part);
                    }
                });
            });
            return true;
        }
        return false;
    }

    @Override
    public List<BodyPart> getRealParts() {
        return meleeParts;
    }

    @Override
    public void handleDamage(float amount, List<BodyPart> bodyParts, LivingEntity entity, Body body) {
        if (meleeSlots != null) {
            PainMain.log("Falling back to melee damage");
            meleeParts.forEach(part -> {
                part.takeDamage(amount / meleeParts.size());
            });
            /*meleeSlots.forEach(slot -> {
                if (body.getPartForSlot(slot) != null) {
                    for (BodyPart bodyPart : body.getPartForSlot(slot)) {
                        bodyPart.takeDamage(amount / (body.getPartForSlot(slot).size() * meleeSlots.size()));
                    }
                }
            });*/
            return;
        }
    }
}
