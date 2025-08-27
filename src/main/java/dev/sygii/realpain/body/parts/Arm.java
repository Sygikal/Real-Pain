package dev.sygii.realpain.body.parts;

import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.BodyPart;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

import java.awt.*;

import static dev.sygii.realpain.PainClient.drawHealthRectangle;

public class Arm extends BodyPart {

    public Arm(LivingEntity entity, BodySide side) {
        super(entity, PainMain.MAIN.PARTS.armWeight, side);
    }

    @Override
    public void renderModel(PlayerEntityModel<AbstractClientPlayerEntity> model, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {
        float[] colors = getColors();
        if (side.equals(BodySide.LEFT)) {
            model.leftArm.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
            //model.leftSleeve.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
        }else {
            model.rightArm.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
            //model.rightSleeve.render(matrices, vc, light, overlay, colors[0], colors[1], colors[2], 1f);
        }
    }

    @Override
    public void renderFlat(DrawContext context, MatrixStack matrices, VertexConsumer vc, int light, int overlay) {
        float[] colors = getColors();
        if (side.equals(BodySide.LEFT)) {
            int width = 4;
            int height = 11;
            int x = 0;
            int y = 0 + 6;
            drawHealthRectangle(context, x, y, width, height, getColor());
        }else {
            int width = 4;
            int height = 11;
            int x = 0 + 9;
            int y = 0 + 6;
            drawHealthRectangle(context, x, y, width, height, getColor());
        }
    }

}
