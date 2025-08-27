package dev.sygii.realpain.util;

import dev.sygii.realpain.PainAttachments;
import dev.sygii.realpain.body.BodyPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class PlayerSizeHelper {
    private static final Map<EquipmentSlot, AABBAlignedBoundingBox> NORMAL_BOXES;
    private static final Map<EquipmentSlot, AABBAlignedBoundingBox> SNEAKING_BOXES;


    static {
        Map<EquipmentSlot, AABBAlignedBoundingBox> builder = new LinkedHashMap<>();
        builder.put(EquipmentSlot.FEET, new AABBAlignedBoundingBox(0D, 0D, 0D, 1D, 0.15D, 1D));
        builder.put(EquipmentSlot.LEGS, new AABBAlignedBoundingBox(0D, 0.15D, 0D, 1D, 0.45D, 1D));
        builder.put(EquipmentSlot.CHEST, new AABBAlignedBoundingBox(0D, 0.45D, 0D, 1D, 0.8D, 1D));
        builder.put(EquipmentSlot.HEAD, new AABBAlignedBoundingBox(0D, 0.8D, 0D, 1D, 1D, 1D));
        NORMAL_BOXES = Collections.unmodifiableMap(builder);

        builder = new LinkedHashMap<>();
        builder.put(EquipmentSlot.FEET,  new AABBAlignedBoundingBox(0D, 0D, 0D, 1D, 0.15D, 1D));
        builder.put(EquipmentSlot.LEGS, new AABBAlignedBoundingBox(0D, 0.15D, 0D, 1D, 0.4D, 1D));
        builder.put(EquipmentSlot.CHEST, new AABBAlignedBoundingBox(0D, 0.4D, 0D, 1D, 0.75D, 1D));
        builder.put(EquipmentSlot.HEAD,  new AABBAlignedBoundingBox(0D, 0.75D, 0D, 1D, 1D, 1D));
        SNEAKING_BOXES = Collections.unmodifiableMap(builder);
    }

    public static Map<EquipmentSlot, AABBAlignedBoundingBox> getBoxes(Entity entity) {
        switch (entity.getPose()) {
            case STANDING:
                return NORMAL_BOXES;
            case CROUCHING:
                return SNEAKING_BOXES;
            case SPIN_ATTACK: //tridant
            case FALL_FLYING: //elytra
                return Collections.emptyMap(); // To be evaluated
            case DYING:
            case SLEEPING:
            case SWIMMING:
            default:
                return Collections.emptyMap();
        }
    }

    public static EquipmentSlot getSlotTypeForProjectileHit(Entity hittingObject, PlayerEntity toTest) {
        Map<EquipmentSlot, AABBAlignedBoundingBox> toUse = getBoxes(toTest);
        Vec3d oldPosition = hittingObject.getPos();
        Vec3d newPosition = oldPosition.add(hittingObject.getVelocity());

        // See ProjectileHelper.getEntityHitResult
        float[] inflationSteps = new float[] {0.01F, 0.1F, 0.2F, 0.3F};
        for (float inflation : inflationSteps) {
            EquipmentSlot bestSlot = null;
            double bestValue = Double.MAX_VALUE;
            for (Map.Entry<EquipmentSlot, AABBAlignedBoundingBox> entry : toUse.entrySet()) {
                Box axisalignedbb = entry.getValue().createAABB(toTest.getBoundingBox()).expand(inflation);
                Optional<Vec3d> optional = axisalignedbb.raycast(oldPosition, newPosition);
                if (optional.isPresent()) {
                    double d1 = oldPosition.squaredDistanceTo(optional.get());
                    double d2 = 0D;//newPosition.distanceToSqr(optional.get());
                    if ((d1 + d2) < bestValue) {
                        bestSlot = entry.getKey();
                        bestValue = d1 + d2;
                    }
                }
            }
            if (bestSlot != null) {
                /*if (FirstAidConfig.GENERAL.debug.get()) {
                    FirstAid.LOGGER.info("getSlotTypeForProjectileHit: Inflation: " + inflation + " best slot: " + bestSlot);
                }*/
                return bestSlot;
            }
        }
        /*if (FirstAidConfig.GENERAL.debug.get()) {
            FirstAid.LOGGER.info("getSlotTypeForProjectileHit: Not found!");
        }*/
        return null;
    }


   public static List<EquipmentSlot> getMeleeDistribution(PlayerEntity player, DamageSource source) {
        Entity causingEntity = source.getAttacker();
        if (causingEntity != null && causingEntity == source.getSource() && causingEntity instanceof MobEntity mobEntity) {
            if (mobEntity.getTarget() == player && mobEntity.goalSelector.getRunningGoals().anyMatch(prioritizedGoal -> prioritizedGoal.getGoal() instanceof MeleeAttackGoal)) {
                Map<EquipmentSlot, AABBAlignedBoundingBox> boxes = PlayerSizeHelper.getBoxes(player);
                if (!boxes.isEmpty()) {
                    List<EquipmentSlot> allowedParts = new ArrayList<>();
                    Box modAABB = mobEntity.getBoundingBox().expand(mobEntity.getWidth() * 2F + player.getWidth(), 0, mobEntity.getWidth() * 2F + player.getWidth());
                    for (Map.Entry<EquipmentSlot, AABBAlignedBoundingBox> entry : boxes.entrySet()) {
                        Box partAABB = entry.getValue().createAABB(player.getBoundingBox());
                        if (modAABB.intersects(partAABB)) {
                            allowedParts.add(entry.getKey());
                        }
                    }
                    /*if (FirstAidConfig.GENERAL.debug.get()) {
                        FirstAid.LOGGER.info("getMeleeDistribution: Has distribution with {}", allowedParts);
                    }*/
                    if (allowedParts.isEmpty() && player.getY() > mobEntity.getY() && (player.getY() - mobEntity.getY()) < mobEntity.getHeight() * 2F) {
                        // HACK: y is at the bottom of the aabb of mobs, so the range of mobs to your feet is larger than the range of them to your head
                        // If no matching region can be found, but the y difference is within 2 times the bb height of the attacking mob
                        // This should be accurate enough (in theory)
                        /*if (FirstAidConfig.GENERAL.debug.get()) {
                            FirstAid.LOGGER.info("Hack adding feet");
                        }*/
                        allowedParts.add(EquipmentSlot.FEET);
                    }
                    if (!allowedParts.isEmpty()/* && !allowedParts.containsAll(Arrays.asList(CommonUtils.ARMOR_SLOTS))*/) {
                        return allowedParts;
                    }
                }
            }
        }
        return null;
    }
}
