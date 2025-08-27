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
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ProjectileDamageDistribution extends DamageDistribution {
    public static Identifier ID = PainMain.id("projectiles");
    EquipmentSlot projectileSlot = null;
    List<BodyPart> projectileParts = new ArrayList<>();


    public ProjectileDamageDistribution() {
        super(ID);
    }

    @Override
    public boolean shouldApply(DamageSource source, LivingEntity entity) {
        if (source.isIn(DamageTypeTags.IS_PROJECTILE) && entity instanceof PlayerEntity) {
            Pair<Entity, HitResult> rayTraceResult = PainAttachments.body.get(entity).hitList.remove(entity);
            if (rayTraceResult != null) {
                Entity entityProjectile = rayTraceResult.getLeft();
                projectileSlot = PlayerSizeHelper.getSlotTypeForProjectileHit(entityProjectile, (PlayerEntity) entity);
                PainMain.log("Projectile to :" + projectileSlot);
                if (PainAttachments.body.get(entity).getPartForSlot(projectileSlot) != null) {
                    PainAttachments.body.get(entity).getPartForSlot(projectileSlot).forEach(part -> {
                        if (!projectileParts.contains(part)) {
                            projectileParts.add(part);
                        }
                    });
                    return true;
                }
            }

        }

        return false;
    }

    @Override
    public List<BodyPart> getRealParts() {
        return projectileParts;
    }

    @Override
    public void handleDamage(float amount, List<BodyPart> bodyParts, LivingEntity entity, Body body) {
        projectileParts.forEach(part -> {
            part.takeDamage(amount / projectileParts.size());
        });
        /*Pair<Entity, HitResult> rayTraceResult = body.hitList.remove(entity);
        if (rayTraceResult != null) {
            Entity entityProjectile = rayTraceResult.getLeft();
            EquipmentSlot slot = PlayerSizeHelper.getSlotTypeForProjectileHit(entityProjectile, (PlayerEntity) entity);
            PainMain.log("Projectile to :" + slot);
            if (body.getPartForSlot(slot) != null) {
                body.getPartForSlot(slot).forEach(part -> {
                    part.takeDamage(amount / body.getPartForSlot(slot).size());
                });
            }
        }*/
    }
}
