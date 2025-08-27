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

import static dev.sygii.realpain.PainClient.drawHealthRectangle;

public class Leg extends BodyPart {

    public Leg(LivingEntity entity, BodySide side) {
        super(entity, PainMain.MAIN.PARTS.legWeight, side);
        this.activeSlots.add(EquipmentSlot.LEGS);
        this.activeSlots.add(EquipmentSlot.FEET);
    }

    @Override
    public void renderModel(PlayerEntityModel<AbstractClientPlayerEntity> model, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {
        float[] colors = getColors();
        if (side.equals(BodySide.LEFT)) {
            model.leftLeg.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
            //model.leftPants.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
        } else {
            model.rightLeg.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
            //model.rightPants.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
        }
    }

    @Override
    public void renderFlat(DrawContext context, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {
        float[] colors = getColors();
        if (side.equals(BodySide.LEFT)) {
            int width = 4;
            int height = 11;
            int x = 0 + 3;
            int y = 0 + 16;
            drawHealthRectangle(context, x, y, width, height, getColor());
        } else {
            int width = 4;
            int height = 11;
            int x = 0 + 6;
            int y = 0 + 16;
            drawHealthRectangle(context, x, y, width, height, getColor());
        }
    }
}
