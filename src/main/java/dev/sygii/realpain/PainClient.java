package dev.sygii.realpain;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.entity.PlayerBody;
import dev.sygii.realpain.config.ClientConfig;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.DamageUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PainClient implements ClientModInitializer {

	public static ClientConfig CLIENT = ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);

	public static PlayerEntityModel<AbstractClientPlayerEntity> model = null;
	/*private static final RenderLayer SOLID_COLOR = RenderLayer.of(
			"solid_color",
			VertexFormats.POSITION_COLOR,                // use only position+color
			VertexFormat.DrawMode.QUADS,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
					.shader(new RenderPhase.ShaderProgram(GameRenderer::getPositionColorProgram)) // vanilla solid shader
					.transparency(RenderPhase.NO_TRANSPARENCY)
					.cull(RenderPhase.DISABLE_CULLING)
					.lightmap(RenderPhase.DISABLE_LIGHTMAP) // ignore lighting if you want flat look
					.build(false)
	);*/

	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			PainMain.log("Requesting from server!");
			PainAttachments.body.requestFromServer(client.player);
			if (client.player != null) {
				boolean slim = Objects.equals(client.player.getModel(), "slim");
				model = new PlayerEntityModel<>(client.getEntityModelLoader().getModelPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim);
			}
		});

		HudRenderCallback.EVENT.register((drawContext, delta) -> {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			MatrixStack matrices = drawContext.getMatrices();
			if (player != null) {
				if (MinecraftClient.getInstance().interactionManager.hasStatusBars()) {
					PlayerBody body = PainAttachments.body.get(player);
					if (CLIENT.debug) {
						if (body != null) {
							AtomicInteger yOffset = new AtomicInteger();
							//drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(DamageUtil.getDamageLeft(2, 8, 3) + " "), 200, 210, -1, true);

							//drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(body.getAmplifier(body.getPart(PainMain.id("right_leg"))) + " "), 200, 200, -1, true);
							body.getBodyParts().forEach((id, part) -> {
								String text = "";
								text += part.getPartHealth() + "/" + part.getPartMaxHealth() + " | " + id.toString() + " " + (part.parent == null ? "" : part.parent.id) + " ";
								if (!part.children.isEmpty()) {
									for (BodyPart child : part.children) {
										text += child.id.toString();
									}
								}
								drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(text), 200, 100 + yOffset.get(), -1, true);
								yOffset.addAndGet(10);
							});
						}
					}
					/*int color;
					//matrices.push();
					//InventoryScreen.drawEntity(drawContext, 51, 75, 30, 0, 0, player);
					PlayerEntityModel<AbstractClientPlayerEntity> model =
							new PlayerEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER), true);

					VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

					matrices.push();
					matrices.translate(51, 75, 0);
					//matrices.multiplyPositionMatrix((new Matrix4f()).scaling((float)30, (float)30, (float)(-30)));
					matrices.scale(30.0f, 30.0f, -30); // scale up
					//Quaternionf quaternionf = (new Quaternionf()).rotateX(10);
					//matrices.multiply(quaternionf);

					// Get texture
					Identifier skin = player.getSkinTexture();
					VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skin));

					int light = 0xF000F0; // full brightness
					int overlay = OverlayTexture.DEFAULT_UV;
					//DiffuseLighting.method_34742();

					// Example: tint parts differently
					DiffuseLighting.disableGuiDepthLighting();
					//DiffuseLighting.enableGuiDepthLighting();
					model.head.render(matrices, vc, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f); // red head
					model.body.render(matrices, vc, light, overlay, 0.0f, 1.0f, 0.0f, 1.0f); // green body
					model.leftArm.render(matrices, vc, light, overlay, 0.0f, 0.0f, 1.0f, 1.0f); // blue left arm
					model.rightArm.render(matrices, vc, light, overlay, 1.0f, 1.0f, 0.0f, 1.0f); // yellow right arm
					model.leftLeg.render(matrices, vc, light, overlay, 1.0f, 0.0f, 1.0f, 1.0f); // purple left leg
					model.rightLeg.render(matrices, vc, light, overlay, 0.0f, 1.0f, 1.0f, 1.0f); // cyan right leg
					//DiffuseLighting.disableGuiDepthLighting();
					//DiffuseLighting.enableGuiDepthLighting();
					//DiffuseLighting.enableGuiDepthLighting();
					vertexConsumers.draw();
					matrices.pop();*/

					//vertexConsumers.draw();
					//drawContext.getMatrices().pop();

					VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
					Identifier skin = player.getSkinTexture();
					VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skin));

					matrices.push();
					//matrices.translate(drawContext.getScaledWindowWidth() / 2 - 104, drawContext.getScaledWindowHeight() - 28, 16);
					//matrices.translate(-(16 + 8), -(16), 0);

					//matrices.multiplyPositionMatrix((new Matrix4f()).scaling((float)32, (float)32, (float)(-32)));

					float scale = CLIENT.guiScale.get() * (CLIENT.renderModel ? 8 : 1);
					//matrices.scale(scale, scale, -scale);
					//matrices.multiplyPositionMatrix((new Matrix4f()).scaling((float)32, (float)32, (float)(-32)));
					//matrices.translate((float) ((CLIENT.guiX) / scale), (float) ((CLIENT.guiY) / scale), 0);
					/*matrices.translate(16 + 8, 16, 0);
					matrices.scale(scale, scale, 0);
					matrices.translate((float) scale / 2, (float) scale /2, 0);*/
					//matrices.translate(drawContext.getScaledWindowWidth() / 2 - 108, drawContext.getScaledWindowHeight() - 30, 16);
					matrices.scale(scale, scale, -scale);
					matrices.translate(CLIENT.guiX.get() /scale, CLIENT.guiY.get()/ scale, 0.0);
					//matrices.translate(-CLIENT.guiX, -CLIENT.guiY, 0.0);

					// Rotation based on world time
					//float time = (MinecraftClient.getInstance().world.getTime() * 1 + delta) % 360 ;
					/*matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time));   // spin around Y
					matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-time));   // tilt slightly forward*/
					//matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(1));
					//matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time));

					int light = 0xF000F0;
					int overlay = OverlayTexture.DEFAULT_UV;

					DiffuseLighting.disableGuiDepthLighting();

					// Tint example
					//matrices.translate(0, (float) -1 /30, 0);
					//RenderSystem.runAsFancy(() -> model.head.render(matrices, vc, light, overlay, 1f, 1f, 1f, 1f));
					if (body != null) {
						body.getBodyParts().forEach((id, part) -> {
							if (CLIENT.renderModel && model != null) {
								part.renderModel(model, matrices, vc, light, overlay);
							}else {
								part.renderFlat(drawContext, matrices, vc, light, overlay);
							}
						});
					}
					/*if (model != null) {
						model.jacket.render(matrices, vc, light, overlay, 1f, 1f, 1f, 1f);
						model.leftSleeve.render(matrices, vc, light, overlay, 1f, 1f, 1f, 1f);
						model.rightSleeve.render(matrices, vc, light, overlay, 1f, 1f, 1f, 1f);
						//matrices.translate(0, (float) 1 /30, 0);
						model.body.render(matrices, vc, light, overlay, 1f, 1f, 1f, 1f);

						//matrices.translate((float) 1 /30, 0, 0);
						model.leftArm.render(matrices, vc, light, overlay, 1f, 1f, 1f, 1f);
						//matrices.translate((float) -1 /30, 0, 0);
						//matrices.translate((float) -1 /30, 0, 0);
						model.rightArm.render(matrices, vc, light, overlay, 1f, 1f, 1f, 1f);
						//matrices.translate((float) 1 /30, 0, 0);

						//matrices.translate(0, (float) 1 /30, 0);
						model.leftLeg.render(matrices, vc, light, overlay, 1f, 1f, 1f, 1f);
						//matrices.translate((float) 1 /30, 0, 0);
						model.rightLeg.render(matrices, vc, light, overlay, 1f, 1f, 1f, 1f);
					}*/

					//DiffuseLighting.enableGuiDepthLighting();

					vertexConsumers.draw();
					matrices.pop();
				}
			}
		});
	}
	public static int[] damageColors = {0xff5b5b5b, 0xffb70000, 0xfff87c00, 0xffffd966, 0xff8fce00, 0xff38761d};

	public static void drawHealthRectangle(DrawContext drawContext, int startX, int startY, int width, int height, int color){
		int endX = startX+width;
		int endY = startY+height;

		drawContext.fill(startX, startY, endX, endY, 0xff191919);
		int BORDER_SIZE = 1;
		drawContext.fill(startX + BORDER_SIZE, startY + BORDER_SIZE, endX - BORDER_SIZE, endY - BORDER_SIZE, color);
	}
}
