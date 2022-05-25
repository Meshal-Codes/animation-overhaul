package net.lizistired.animationoverhaul.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lizistired.animationoverhaul.AnimationOverhaulMain;
import net.lizistired.animationoverhaul.util.data.EntityAnimationData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class DebugGadgetRenderer extends DrawableHelper {

    public static final DebugGadgetRenderer INSTANCE = new DebugGadgetRenderer();

    private final MinecraftClient minecraft;
    private Entity entity;
    private final EntityAnimationData animationData = new EntityAnimationData();

    private static final Identifier COMPASS_TEXTURE = new Identifier(AnimationOverhaulMain.MOD_ID, "textures/gui/gadgets/compass.png");
    private static final EntityAnimationData.DataKey<Float> COMPASS_ROTATION_SPEED = new EntityAnimationData.DataKey<>("compass_rotation_speed", 0F);
    private static final EntityAnimationData.DataKey<Float> COMPASS_ROTATION_POSITION = new EntityAnimationData.DataKey<>("compass_rotation_position", 0F);

    public DebugGadgetRenderer(){
        this.minecraft = MinecraftClient.getInstance();
        this.entity = this.minecraft.getCameraEntity();
    }

    public void tick(){
        this.entity = this.minecraft.getCameraEntity();
        if(this.entity != null){
            float targetCompassRotation = (this.entity.getYaw() + 180) % 360;
            float compassRotationSpeed = this.animationData.get(COMPASS_ROTATION_SPEED).get();
            float compassRotationPosition = this.animationData.get(COMPASS_ROTATION_POSITION).get();
            compassRotationSpeed += (targetCompassRotation > compassRotationPosition ? 1 : -1) * 1.1F;
            if(MathHelper.abs(targetCompassRotation - compassRotationPosition) < 20){
                compassRotationSpeed *= 0.75F;
            }
            compassRotationPosition += compassRotationSpeed;
            this.animationData.get(COMPASS_ROTATION_SPEED).set(compassRotationSpeed);
            this.animationData.get(COMPASS_ROTATION_POSITION).set(compassRotationPosition);
        }
    }

    public void render(MatrixStack poseStack, float partialTicks){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, COMPASS_TEXTURE);

        poseStack.push();
        poseStack.translate(0, 50, 0);
        this.drawTexture(poseStack, 0, 0, 0, 0, 160, 256);

        poseStack.push();
        poseStack.translate(80, 83, 0);

        poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(animationData.getLerped(COMPASS_ROTATION_POSITION, partialTicks)));
        this.drawTexture(poseStack, -3, -52, 160, 13, 6, 62);
        poseStack.pop();
        poseStack.pop();
    }
}
