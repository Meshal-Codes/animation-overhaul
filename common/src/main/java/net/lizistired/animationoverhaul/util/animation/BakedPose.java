package net.lizistired.animationoverhaul.util.animation;

import com.google.common.collect.Maps;
import net.lizistired.animationoverhaul.AnimationOverhaulMain;
import java.util.HashMap;
import java.util.Objects;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.util.math.MathHelper;

public class BakedPose {

    private HashMap<LocatorRig.LocatorEntry, ModelTransform> pose = Maps.newHashMap();
    private HashMap<LocatorRig.LocatorEntry, ModelTransform> poseOld = Maps.newHashMap();
    public boolean hasPose;

    public BakedPose(){
        this.hasPose = false;
    }

    public void pushToOld(){
        this.poseOld = pose;
    }

    public void setPose(HashMap<LocatorRig.LocatorEntry, ModelTransform> hashMap){
        this.pose = hashMap;
    }

    public void bakeToModelParts(ModelPart modelPart, float partialTicks){
        for(LocatorRig.LocatorEntry locatorEntry : pose.keySet()){
            if(locatorEntry.modelPartIdentifier != null){
                ModelPart finalModelPart = modelPart;
                for(String individualPartString : locatorEntry.modelPartIdentifier.split("\\.")){
                    finalModelPart = finalModelPart.getChild(individualPartString);
                }
                ModelTransform partPose = lerpPartPose(partialTicks, poseOld.get(locatorEntry), pose.get(locatorEntry));
                finalModelPart.setTransform(partPose);
            }
        }
    }

    private static ModelTransform lerpPartPose(float value, ModelTransform partPoseOld, ModelTransform partPose){
        if(partPose.pitch - partPoseOld.pitch > MathHelper.PI){
            partPose = ModelTransform.of(partPose.pivotX, partPose.pivotY, partPose.pivotZ, partPose.pitch - MathHelper.TAU, partPose.yaw, partPose.roll);
        }
        if(partPose.pitch - partPoseOld.pitch < -MathHelper.PI){
            partPose = ModelTransform.of(partPose.pivotX, partPose.pivotY, partPose.pivotZ, partPose.pitch + MathHelper.TAU, partPose.yaw, partPose.roll);
        }
        if(Math.abs(partPose.pitch - partPoseOld.pitch) > MathHelper.PI){
            AnimationOverhaulMain.LOGGER.warn("Snapping on the X axis of {} degrees", Math.toDegrees(Math.abs(partPose.pitch - partPoseOld.pitch)));
        }
        if(Math.abs(partPose.yaw - partPoseOld.yaw) > MathHelper.PI){
            AnimationOverhaulMain.LOGGER.warn("Snapping on the Y axis of {} degrees", Math.toDegrees(Math.abs(partPose.yaw - partPoseOld.yaw)));
        }
        if(Math.abs(partPose.roll - partPoseOld.roll) > MathHelper.PI){
            AnimationOverhaulMain.LOGGER.warn("Snapping on the Z axis of {} degrees", Math.toDegrees(Math.abs(partPose.roll - partPoseOld.roll)));
        }
        return ModelTransform.of(
                MathHelper.lerp(value, partPoseOld.pivotX, partPose.pivotX),
                MathHelper.lerp(value, partPoseOld.pivotY, partPose.pivotY),
                MathHelper.lerp(value, partPoseOld.pivotZ, partPose.pivotZ),
                MathHelper.lerpAngleDegrees(value, partPoseOld.pitch, partPose.pitch),
                MathHelper.lerpAngleDegrees(value, partPoseOld.yaw, partPose.yaw),
                MathHelper.lerpAngleDegrees(value, partPoseOld.roll, partPose.roll)
        );
    }

    /*
    private static Locator lerpLocator(float value, Locator locatorOld, Locator locator){
        Locator locatorNew = new Locator(locator.getIdentifier());
        locatorNew.translateX = Mth.lerp(value, locatorOld.translateX, locator.translateX);
        locatorNew.translateY = Mth.lerp(value, locatorOld.translateY, locator.translateY);
        locatorNew.translateZ = Mth.lerp(value, locatorOld.translateZ, locator.translateZ);
        locatorNew.rotateX = Mth.rotLerp(value, locatorOld.rotateX, locator.rotateX);
        locatorNew.rotateY = Mth.rotLerp(value, locatorOld.rotateY, locator.rotateY);
        locatorNew.rotateZ = Mth.rotLerp(value, locatorOld.rotateZ, locator.rotateZ);
        return locatorNew;
    }

     */

    public Locator getLocator(String identifier, float partialTicks){

        for(LocatorRig.LocatorEntry locatorEntry : this.pose.keySet()){
            if(Objects.equals(locatorEntry.getLocator().getIdentifier(), identifier)){
                return Locator.fromPartPose(lerpPartPose(partialTicks, this.poseOld.get(locatorEntry), this.pose.get(locatorEntry)), identifier);
            }
        }
        return new Locator("null");
    }

    public boolean containsLocator(String identifier){
        for(LocatorRig.LocatorEntry locatorEntry : this.pose.keySet()){
            if(locatorEntry.getLocator().getIdentifier() == identifier){
                return true;
            }
        }
        return false;
    }
}
