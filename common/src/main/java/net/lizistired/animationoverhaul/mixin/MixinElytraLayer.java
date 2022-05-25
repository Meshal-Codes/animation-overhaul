package net.lizistired.animationoverhaul.mixin;

import net.lizistired.animationoverhaul.animations.AnimatorDispatcher;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraFeatureRenderer.class)
public abstract class MixinElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public MixinElytraLayer(FeatureRendererContext<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "render*", at = @At("HEAD"))
    private void transformElytra(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci){
        if(this.getContextModel() instanceof BipedEntityModel && isValidForElytraTransformation(livingEntity)){
            poseStack.push();
            ModelPart body = ((BipedEntityModel<?>) this.getContextModel()).body;
            body.rotate(poseStack);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"))
    private void transformElytraFinalized(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci){
        if(this.getContextModel() instanceof BipedEntityModel && isValidForElytraTransformation(livingEntity)){
            poseStack.pop();
        }
    }

    private boolean isValidForElytraTransformation(LivingEntity livingEntity){
        return AnimatorDispatcher.INSTANCE.hasAnimationData(livingEntity.getUuid());
    }
}
