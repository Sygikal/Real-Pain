package dev.sygii.realpain.body.parts;

import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.BodyPart;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

import java.awt.*;

import static dev.sygii.realpain.PainClient.drawHealthRectangle;

public class Torso extends BodyPart {

    public Torso(LivingEntity entity) {
        super(entity, PainMain.MAIN.PARTS.torsoWeight, true);
        this.activeSlots.add(EquipmentSlot.CHEST);
    }

    @Override
    public void renderModel(PlayerEntityModel<AbstractClientPlayerEntity> model, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {
        float[] colors = getColors();
        Color newColor = new Color(0x8038761d);
        model.body.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
    }

    @Override
    public void renderFlat(DrawContext context, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {
        int width = 7;
        int height = 11;
        int x = 0 + 3;
        int y = 0 + 6;
        drawHealthRectangle(context, x, y, width, height, getColor());
    }

}
