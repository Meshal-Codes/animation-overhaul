package net.lizistired.animationoverhaul.mixin;

import net.lizistired.animationoverhaul.animations.AnimatorDispatcher;
import net.lizistired.animationoverhaul.animations.entity.LivingEntityPartAnimator;
import net.lizistired.animationoverhaul.util.animation.BakedPose;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    protected MixinLivingEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    private final String ROOT = "root";

    @Shadow protected M model;
    @Shadow public abstract M getModel();

    @Shadow protected abstract void setupTransforms(T livingEntity, MatrixStack poseStack, float f, float g, float h);

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"))
    private void redirectSetupAnim(M entityModel, Entity t, float a, float b, float c, float d, float e, T livingEntity, float f, float g, MatrixStack poseStack){
        if(!AnimatorDispatcher.INSTANCE.animateEntity(livingEntity, entityModel, poseStack, g)){
            entityModel.setAngles(livingEntity, a, b, c, d, e);
        }
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V"))
    private void overwriteSetupRotations(LivingEntityRenderer<T,M> instance, T livingEntity, MatrixStack poseStack, float bob, float bodyRot, float frameTime){

        //poseStack.translate(Mth.sin(bob / 6), 0, 0);
        //poseStack.mulPose(Vector3f.ZP.rotation(Mth.sin(bob / 6) / 4));

        BakedPose bakedPose = AnimatorDispatcher.INSTANCE.getBakedPose(livingEntity.getUuid());

        if(shouldUseAlternateRotations(bakedPose)){

            poseStack.pop();
            poseStack.push();

            if(livingEntity.getPose() == EntityPose.SLEEPING){
                Direction i = ((LivingEntity)livingEntity).getSleepingDirection();
                float j = i != null ? sleepDirectionToRotation(i) : bodyRot;
                poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(j - 90));
            } else {
                bodyRot = AnimatorDispatcher.INSTANCE.getEntityAnimationData(livingEntity).getLerped(LivingEntityPartAnimator.BODY_Y_ROT, frameTime);
                if(livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof AbstractMinecartEntity){
                    bodyRot = MathHelper.lerpAngleDegrees(frameTime, ((LivingEntity)livingEntity).prevHeadYaw, ((LivingEntity)livingEntity).headYaw);
                }

                poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180 - bodyRot));
            }

        } else {
            this.setupTransforms(livingEntity, poseStack, bob, bodyRot, frameTime);
        }
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V", ordinal = 0))
    private void removeBedTranslation(MatrixStack instance, double d, double e, double f, T livingEntity){
        BakedPose bakedPose = AnimatorDispatcher.INSTANCE.getBakedPose(livingEntity.getUuid());
        if(!shouldUseAlternateRotations(bakedPose)){
            instance.translate(d, e, f);
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"))
    private void translateAndRotateAfterScale(T livingEntity, float f, float g, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, CallbackInfo ci){
        BakedPose bakedPose = AnimatorDispatcher.INSTANCE.getBakedPose(livingEntity.getUuid());
        if(shouldUseAlternateRotations(bakedPose)){
            poseStack.translate(0, -1.5, 0);
            bakedPose.getLocator(ROOT, g).translateAndRotatePoseStack(poseStack);
            poseStack.translate(0, 1.5, 0);
        }
    }

    private boolean shouldUseAlternateRotations(BakedPose bakedPose){
        if(bakedPose != null){
            return bakedPose.containsLocator(ROOT);
        }
        return false;
    }

    private static float sleepDirectionToRotation(Direction direction) {
        switch (direction) {
            case SOUTH -> {
                return 90.0f;
            }
            case WEST -> {
                return 0.0f;
            }
            case NORTH -> {
                return 270.0f;
            }
            case EAST -> {
                return 180.0f;
            }
        }
        return 0.0f;
    }
}
