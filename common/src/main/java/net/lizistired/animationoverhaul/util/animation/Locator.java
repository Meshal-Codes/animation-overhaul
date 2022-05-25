package net.lizistired.animationoverhaul.util.animation;

import net.lizistired.animationoverhaul.util.math.RotationMatrix;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class Locator {

    public float translateX;
    public float translateY;
    public float translateZ;
    public float rotateX;
    public float rotateY;
    public float rotateZ;
    private String identifier;

    public Locator(String identifier){
        this.translateX = 0F;
        this.translateY = 0F;
        this.translateZ = 0F;
        this.rotateX = 0F;
        this.rotateY = 0F;
        this.rotateZ = 0F;
        this.identifier = identifier;
    }

    public void translatePoseStack(MatrixStack poseStack){
        poseStack.translate((this.translateX / 16.0F), (this.translateY / 16.0F), (this.translateZ / 16.0F));
    }

    public void rotatePoseStack(MatrixStack poseStack){
        if (this.rotateZ != 0.0F) {
            poseStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(this.rotateZ));
        }

        if (this.rotateY != 0.0F) {
            poseStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(this.rotateY));
        }

        if (this.rotateX != 0.0F) {
            poseStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(this.rotateX));
        }
    }

    public void translateAndRotatePoseStack(MatrixStack poseStack){
        translatePoseStack(poseStack);
        rotatePoseStack(poseStack);
    }

    public void translatePoseStackInverse(MatrixStack poseStack){
        poseStack.translate((this.translateX / -16.0F), (this.translateY / -16.0F), (this.translateZ / -16.0F));
    }

    public void rotatePoseStackInverse(MatrixStack poseStack){
        if (this.rotateX != 0.0F) {
            poseStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(-this.rotateX));
        }

        if (this.rotateY != 0.0F) {
            poseStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(-this.rotateY));
        }

        if (this.rotateZ != 0.0F) {
            poseStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(-this.rotateZ));
        }


    }
    public void translateAndRotatePoseStackInverse(MatrixStack poseStack){
        rotatePoseStackInverse(poseStack);
        translatePoseStackInverse(poseStack);
    }

    @Deprecated
    public void bakeToModelPart(ModelPart modelPart){
        modelPart.pivotX = this.translateX;
        modelPart.pivotY = this.translateY;
        modelPart.pivotZ = this.translateZ;
        modelPart.pitch = this.rotateX;
        modelPart.yaw = this.rotateY;
        modelPart.roll = this.rotateZ;
    }

    public void additiveApplyPose(ModelTransform partPose){
        this.translateX += partPose.pivotX;
        this.translateY += partPose.pivotY;
        this.translateZ += partPose.pivotZ;
        this.rotateX += partPose.pitch;
        this.rotateY += partPose.yaw;
        this.rotateZ += partPose.roll;
    }

    public ModelTransform getPartPose(){
        return ModelTransform.of(this.translateX, this.translateY, this.translateZ, this.rotateX, this.rotateY, this.rotateZ);
    }

    public static Locator fromPartPose(ModelTransform partPose, String identifier){
        Locator locator = new Locator(identifier);
        locator.translateX = partPose.pivotX;
        locator.translateY = partPose.pivotY;
        locator.translateZ = partPose.pivotZ;
        locator.rotateX = partPose.pitch;
        locator.rotateY = partPose.yaw;
        locator.rotateZ = partPose.roll;
        return locator;
    }

    public void rotateWorldSpace(float x, float y, float z){
        Vec3f baseRotation = new Vec3f(this.rotateX, this.rotateY, this.rotateZ);
        Vec3f multRotation = new Vec3f(x, y, z);

        RotationMatrix baseRotationMatrix = RotationMatrix.fromEulerAngles(baseRotation);
        RotationMatrix multRotationMatrix = RotationMatrix.fromEulerAngles(multRotation);

        baseRotationMatrix.mult(multRotationMatrix);

        Vec3f finalRotation = baseRotationMatrix.toEulerAngles();
        this.rotateX = finalRotation.getX();
        this.rotateY = finalRotation.getY();
        this.rotateZ = finalRotation.getZ();
    }

    public void reset(){
        this.translateX = 0;
        this.translateY = 0;
        this.translateZ = 0;
        this.rotateX = 0;
        this.rotateY = 0;
        this.rotateZ = 0;
    }

    public void weightedClearTransforms(float weight){
        this.translateX = MathHelper.lerp(weight, this.translateX, 0);
        this.translateY = MathHelper.lerp(weight, this.translateY, 0);
        this.translateZ = MathHelper.lerp(weight, this.translateZ, 0);
        this.rotateX = MathHelper.lerp(weight, this.rotateX, 0);
        this.rotateY = MathHelper.lerp(weight, this.rotateY, 0);
        this.rotateZ = MathHelper.lerp(weight, this.rotateZ, 0);
    }

    public String getIdentifier(){
        return this.identifier;
    }

}
