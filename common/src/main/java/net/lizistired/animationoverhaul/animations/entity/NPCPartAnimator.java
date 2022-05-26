package net.lizistired.animationoverhaul.animations.entity;

import net.lizistired.animationoverhaul.util.animation.Locator;
import net.lizistired.animationoverhaul.util.data.EntityAnimationData;
import net.lizistired.animationoverhaul.util.data.TimelineGroupData;
import net.lizistired.animationoverhaul.util.time.TimerProcessor;
import java.util.List;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;

public class NPCPartAnimator<T extends LivingEntity, M extends EntityModel<T>> extends LivingEntityPartAnimator<T, M> {

    protected static final EntityAnimationData.DataKey<Float> WALK_TO_STOP = new EntityAnimationData.DataKey<>("walk_to_stop", 0F);
    protected static final EntityAnimationData.DataKey<Float> AGGRO_WEIGHT = new EntityAnimationData.DataKey<>("aggro_weight", 0F);
    protected static final EntityAnimationData.DataKey<Float> AGGRO_TIMER = new EntityAnimationData.DataKey<>("aggro_timer", 0F);


    protected void tickWalkToStopTimer(EntityAnimationData entityAnimationData, float threshold, int durationInTicks){
        entityAnimationData.incrementInTicksOrResetFromCondition(WALK_TO_STOP, entityAnimationData.getValue(ANIMATION_SPEED) > threshold || entityAnimationData.getValue(ANIMATION_SPEED_Y) > 0, durationInTicks);
    }

    protected void tickAggroTimers(EntityAnimationData entityAnimationData, LivingEntity livingEntity, float aggroTimerFrameDuration, int aggroWeightTickDuration){
        if(livingEntity instanceof HostileEntity){
            entityAnimationData.incrementInTicksFromCondition(AGGRO_WEIGHT, ((HostileEntity) livingEntity).isAttacking(), aggroWeightTickDuration, aggroWeightTickDuration);
            entityAnimationData.incrementInFramesOrResetFromCondition(AGGRO_TIMER, !((HostileEntity) livingEntity).isAttacking(), aggroTimerFrameDuration);
        }
    }

    protected void poseHeadRotation(Locator headLocator){
        headLocator.rotateX = getDataValue(HEAD_X_ROT);
        headLocator.rotateY = getDataValue(HEAD_Y_ROT);
    }

    protected void poseLookLean(List<Locator> locatorList, TimelineGroupData.TimelineGroup verticalTimelineGroup, TimelineGroupData.TimelineGroup horizontalTimelineGroup){
        this.locatorRig.animateMultipleLocatorsAdditive(locatorList, verticalTimelineGroup, getLookUpDownTimer(LEAN_X_ROT), 1);
        this.locatorRig.animateMultipleLocatorsAdditive(locatorList, horizontalTimelineGroup, getLookLeftRightTimer(LEAN_Y_ROT), 1);
    }

    protected void poseWalkToStop(List<Locator> locatorList, TimelineGroupData.TimelineGroup timelineGroup){
        this.locatorRig.animateMultipleLocatorsAdditive(locatorList, timelineGroup, getDataValue(WALK_TO_STOP), 1);
    }

    protected void poseAggro(List<Locator> locatorList, TimelineGroupData.TimelineGroup loopTimelineGroup, TimelineGroupData.TimelineGroup startTimelineGroup){
        this.locatorRig.animateMultipleLocatorsAdditive(locatorList, loopTimelineGroup, new TimerProcessor(this.livingEntity.age).repeat(loopTimelineGroup).getValue(), getDataValue(AGGRO_WEIGHT), false);
        this.locatorRig.animateMultipleLocatorsAdditive(locatorList, startTimelineGroup, getDataValue(AGGRO_TIMER), 1, false);
    }
}
