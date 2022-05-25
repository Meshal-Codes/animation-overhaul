package net.lizistired.animationoverhaul.animations;

import com.google.common.collect.Maps;
import net.lizistired.animationoverhaul.AnimationOverhaulMain;
import net.lizistired.animationoverhaul.animations.entity.LivingEntityPartAnimator;
import net.lizistired.animationoverhaul.util.animation.BakedPose;
import net.lizistired.animationoverhaul.util.data.EntityAnimationData;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class AnimatorDispatcher {
    public static final AnimatorDispatcher INSTANCE = new AnimatorDispatcher();

    private final HashMap<UUID, EntityAnimationData> entityAnimationDataMap = Maps.newHashMap();
    private final HashMap<UUID, BakedPose> bakedPoseMap = Maps.newHashMap();

    public AnimatorDispatcher(){
    }

    public void tickEntity(LivingEntity livingEntity, LivingEntityPartAnimator<?, ?> livingEntityPartAnimator){
        if(!entityAnimationDataMap.containsKey(livingEntity.getUuid())){
            entityAnimationDataMap.put(livingEntity.getUuid(), new EntityAnimationData());
        }
        livingEntityPartAnimator.tickMethods(livingEntity);
    }

    public <T extends LivingEntity, M extends EntityModel<T>> boolean animateEntity(T livingEntity, M entityModel, MatrixStack poseStack, float partialTicks){
        if(entityAnimationDataMap.containsKey(livingEntity.getUuid())){
            if(AnimationOverhaulMain.ENTITY_ANIMATORS.contains(livingEntity.getType())){
                LivingEntityPartAnimator<T, M> livingEntityPartAnimator = (LivingEntityPartAnimator<T, M>) AnimationOverhaulMain.ENTITY_ANIMATORS.get(livingEntity.getType());
                livingEntityPartAnimator.animate(livingEntity, entityModel, poseStack, entityAnimationDataMap.get(livingEntity.getUuid()), partialTicks);
                return true;
            }
        }
        return false;
    }

    public void saveBakedPose(UUID uuid, BakedPose bakedPose){
        this.bakedPoseMap.put(uuid, bakedPose);
    }

    public BakedPose getBakedPose(UUID uuid){
        if(this.bakedPoseMap.containsKey(uuid)){
            return this.bakedPoseMap.get(uuid);
        }
        return new BakedPose();
    }

    public boolean hasAnimationData(UUID uuid){
        return this.entityAnimationDataMap.containsKey(uuid);
    }

    public EntityAnimationData getEntityAnimationData(UUID uuid){
        if(entityAnimationDataMap.containsKey(uuid)){
            return entityAnimationDataMap.get(uuid);
        }
        return new EntityAnimationData();
    }

    public <T extends Entity> EntityAnimationData getEntityAnimationData(T entity){
        return getEntityAnimationData(entity.getUuid());
    }
}
