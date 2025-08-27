package dev.sygii.realpain.body.debuff;

import dev.sygii.realpain.body.BodyPart;
import net.minecraft.entity.effect.StatusEffect;

public record TickedDebuff(BodyPart part, DebuffRunner runner) {

}
