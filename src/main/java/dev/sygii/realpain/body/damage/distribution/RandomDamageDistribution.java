package dev.sygii.realpain.body.damage.distribution;

import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.Body;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.damage.DamageDistribution;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class RandomDamageDistribution extends DamageDistribution {
    public static Identifier ID = PainMain.id("random");

    public RandomDamageDistribution() {
        super(ID);
    }

    @Override
    public void handleDamage(float amount, List<BodyPart> bodyParts, LivingEntity entity, Body body) {
        List<BodyPart> keysAsArray = new ArrayList<BodyPart>(bodyParts);
        keysAsArray.get(entity.getRandom().nextInt(bodyParts.size())).takeDamage(amount);;
    }

}
