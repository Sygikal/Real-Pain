package dev.sygii.realpain.content.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class MorphineStatusEffect extends StatusEffect {
    public MorphineStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xEE7DDD);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }
}
