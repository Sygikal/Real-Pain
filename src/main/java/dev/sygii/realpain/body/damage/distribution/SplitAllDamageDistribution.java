package dev.sygii.realpain.body.damage.distribution;

import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.Body;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.damage.DamageDistribution;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;

import java.util.List;

public class SplitAllDamageDistribution extends DamageDistribution {
    public static Identifier ID = PainMain.id("split_all");

    public SplitAllDamageDistribution() {
        super(ID);
    }

    @Override
    public void handleDamage(float amount, List<BodyPart> bodyParts, LivingEntity entity, Body body) {
        body.getBodyParts().values().forEach(part -> {
            part.takeDamage(amount / bodyParts.size());
        });
    }
}
