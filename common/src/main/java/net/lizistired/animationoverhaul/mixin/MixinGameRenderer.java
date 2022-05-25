package net.lizistired.animationoverhaul.mixin;

import net.lizistired.animationoverhaul.AnimationOverhaulMain;
import net.lizistired.animationoverhaul.animations.AnimatorDispatcher;
import net.lizistired.animationoverhaul.animations.entity.LivingEntityPartAnimator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow @Final private MinecraftClient client;



    /*
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void adjustTimersForAllEntities(float f, long l, PoseStack poseStack, CallbackInfo ci){
        for(Entity entity : this.minecraft.level.entitiesForRendering()){
            if(entity instanceof LivingEntity){

                EntityType<?> entityType = entity.getType();
                if(AnimationOverhaulMain.ENTITY_ANIMATORS.contains(entityType)){
                    LivingEntityAnimator livingEntityAnimator = AnimationOverhaulMain.ENTITY_ANIMATORS.get(entityType);
                    livingEntityAnimator.setPartialTicks(f);
                    livingEntityAnimator.tick((LivingEntity) entity);
                }
            }
        }
    }

     */


    @Inject(method = "tick", at = @At("TAIL"))
    private void tickEntityInformation(CallbackInfo ci){
        if(this.client.world != null){
            for(Entity entity : this.client.world.getEntities()){
                if(entity instanceof LivingEntity){
                    EntityType<?> entityType = entity.getType();
                    if(AnimationOverhaulMain.ENTITY_ANIMATORS.contains(entityType)){
                        LivingEntityPartAnimator<?, ?> livingEntityAnimator = AnimationOverhaulMain.ENTITY_ANIMATORS.get(entityType);
                        AnimatorDispatcher.INSTANCE.tickEntity((LivingEntity) entity, livingEntityAnimator);
                    }
                }
            }
        }
    }
}
