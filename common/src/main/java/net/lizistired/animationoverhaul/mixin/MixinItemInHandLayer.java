package net.lizistired.animationoverhaul.mixin;

import net.lizistired.animationoverhaul.animations.AnimatorDispatcher;
import net.lizistired.animationoverhaul.util.animation.BakedPose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemFeatureRenderer.class)
public abstract class MixinItemInHandLayer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    public MixinItemInHandLayer(FeatureRendererContext<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getInstance()Lnet/minecraft/client/MinecraftClient;"))
    private void transformItemInHandLayer(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Mode transformType, Arm humanoidArm, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, CallbackInfo ci){
        if(shouldTransformItemInHand(livingEntity)){
            poseStack.pop();
            poseStack.push();
            ((ModelWithArms)this.getContextModel()).setArmAngle(humanoidArm, poseStack);
            poseStack.translate((humanoidArm == Arm.LEFT ? 1 : -1) /16F, 8/16F, 0);

            String locatorIdentifier = humanoidArm == Arm.LEFT ? "leftHand" : "rightHand";
            AnimatorDispatcher.INSTANCE.getBakedPose(livingEntity.getUuid()).getLocator(locatorIdentifier, MinecraftClient.getInstance().getTickDelta()).translateAndRotatePoseStack(poseStack);
            poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
            poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));

            //poseStack.mulPose(Vector3f.XP.rotationDegrees(Util.getMillis() / 10F));
            poseStack.translate(0, 2/16F, -2/16F);
        }
    }
    private boolean shouldTransformItemInHand(LivingEntity livingEntity){
        BakedPose bakedPose = AnimatorDispatcher.INSTANCE.getBakedPose(livingEntity.getUuid());
        if(bakedPose != null){
            if(bakedPose.containsLocator("leftHand") && bakedPose.containsLocator("rightHand")){
                return true;
            }
        }
        return false;
    }
}
