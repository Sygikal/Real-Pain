package dev.sygii.realpain.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.realpain.PainAttachments;
import dev.sygii.realpain.body.entity.PlayerBody;
import dev.sygii.realpain.util.AABBAlignedBoundingBox;
import dev.sygii.realpain.util.PlayerSizeHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Debug(export =true)
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
	private void handleDamage(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		if (livingEntity instanceof PlayerEntity) {
			EntityRenderDispatcher renderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
			if (renderDispatcher.shouldRenderHitboxes()) {
				matrixStack.push();
				//See PlayerRenderer.getRenderOffset
				if (livingEntity.isSneaking()) {
					matrixStack.translate(0D, 0.125D, 0D);
				}
				Box aabb = livingEntity.getBoundingBox();


				Collection<AABBAlignedBoundingBox> allBoxes = PlayerSizeHelper.getBoxes(livingEntity).values();
				float r2 = 0.25F;
				float g2 = 1.0F;
				float b2 = 1.0F;

				for (AABBAlignedBoundingBox box : allBoxes) {
					Box bbox = box.createAABB(aabb);
					WorldRenderer.drawBox(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getLines()), bbox.expand(0.02D).offset(-livingEntity.getX(), -livingEntity.getY(), -livingEntity.getZ()), r2, g2, b2, 1.0F);
					r2 += 0.25F;
					g2 += 0.5F;
					b2 += 0.1F;

					r2 %= 1.0F;
					g2 %= 1.0F;
					b2 %= 1.0F;
				}
				matrixStack.pop();
			}
		}
	}
}
