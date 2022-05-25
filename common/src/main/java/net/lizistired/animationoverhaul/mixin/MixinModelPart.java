package net.lizistired.animationoverhaul.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
public class MixinModelPart {

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;renderCuboids(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void addDebugRendering(MatrixStack poseStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k, CallbackInfo ci){
        /*
        MultiBufferSource multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer1 = multiBufferSource.getBuffer(RenderType.LINES);
        AABB shape = new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
        LevelRenderer.renderLineBox(poseStack, vertexConsumer1, shape, 0.5F, 1, 1, 1);
        vertexConsumer1.endVertex();
        
         */
    }

}
