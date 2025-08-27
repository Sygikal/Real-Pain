package dev.sygii.realpain.body.damage;

import dev.sygii.realpain.body.Body;
import dev.sygii.realpain.body.BodyPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class DamageDistribution {
    private final Identifier id;

    private Identifier damageSource;
    private TagKey<DamageType> damageSourceTag;
    private List<Identifier> bodyPartsList = new ArrayList<>();
    private List<BodyPart> realParts = new ArrayList<>();

    public DamageDistribution(Identifier id) {
        this.id = id;
    }

    public void setDamageSource(String damageSource) {
        if (damageSource.startsWith("#")) {
            damageSourceTag = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.tryParse(damageSource.replace("#", "")));
        }
        this.damageSource = Identifier.tryParse(damageSource.replace("#", ""));
    }

    public List<Identifier> getBodyPartsList() {
        return bodyPartsList;
    }

    public List<BodyPart> getRealParts() {
        return this.realParts;
    }

    public void addBodyPart(Identifier id) {
        bodyPartsList.add(id);
    }

    public void addRealPart(BodyPart part) {
        realParts.add(part);
    }

    public Identifier getDamageSource() {
        return damageSource;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public boolean shouldApply(DamageSource source, LivingEntity entity) {
        if (damageSourceTag != null) {
            return source.isIn(damageSourceTag);
        }
        final DynamicRegistryManager registryManager = entity.getWorld().getRegistryManager();
        Identifier id = registryManager.get(RegistryKeys.DAMAGE_TYPE).getId(source.getType());
        return this.damageSource.equals(id);
    }

    public void handleDamage(float amount, List<BodyPart> bodyParts, LivingEntity entity, Body body) {

    }
}
