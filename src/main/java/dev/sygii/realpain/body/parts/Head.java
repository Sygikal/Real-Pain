package dev.sygii.realpain.body.parts;

import dev.sygii.realpain.PainClient;
import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.debuff.HitDebuff;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.awt.*;

import static dev.sygii.realpain.PainClient.drawHealthRectangle;

public class Head extends BodyPart {

    public Head(LivingEntity entity) {
        super(entity, PainMain.MAIN.PARTS.headWeight, PainMain.MAIN.PARTS.headFatal);
        this.hitDebuffs.add(new HitDebuff(StatusEffects.BLINDNESS, 0.5f, 40, 0));
        this.activeSlots.add(EquipmentSlot.HEAD);
    }

    @Override
    public void renderModel(PlayerEntityModel<AbstractClientPlayerEntity> model, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {
        float[] colors = getColors();
        model.head.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
    }

    @Override
    public void renderFlat(DrawContext context, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {
        drawHealthRectangle(context, 0 + 3, 0, 7, 7, getColor());
    }
}
