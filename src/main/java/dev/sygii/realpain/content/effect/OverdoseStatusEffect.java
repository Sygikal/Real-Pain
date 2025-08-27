package dev.sygii.realpain.content.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class OverdoseStatusEffect extends StatusEffect {
    public OverdoseStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x5c0800);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }
}
