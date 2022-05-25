package net.lizistired.animationoverhaul.render;

import net.lizistired.animationoverhaul.util.data.TransformChannel;
import net.lizistired.animationoverhaul.util.time.ChannelTimeline;
import net.lizistired.animationoverhaul.util.time.Easing;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockDataKey;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import net.minecraft.block.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import java.util.Random;

public class EndPortalFrameBlockRenderer implements TickableBlockRenderer {

    private static final BlockDataKey<Float> RANDOM_FLOAT;
    private static final BlockDataKey<Float> EYE_TIMER;
    private static final BlockDataKey<Float> EYE_TIMER_PREVIOUS;
    private static final BlockDataKey<Float> ACTIVATE_TIMER;
    private static final BlockDataKey<Float> ACTIVATE_TIMER_PREVIOUS;
    private static final BlockDataKey<Boolean> FILLED;

    public static final Block[] END_PORTAL_BLOCKS = new Block[]{
            Blocks.END_PORTAL_FRAME
    };
    private static final ChannelTimeline eyeInsertTimeline = new ChannelTimeline()
            .addKeyframe(TransformChannel.y, 0, 0F)
            .addKeyframe(TransformChannel.y, 1, -2F/16F)
            .addKeyframe(TransformChannel.y, 5, 0F, new Easing.CubicBezier(.35F,1.0F,.63F,1F));

    public static final ModelIdentifier PORTAL_BASE_LOCATION = new ModelIdentifier("end_portal_frame", "eye=false,facing=east");
    public static final ModelIdentifier PORTAL_EYE_LOCATION = new ModelIdentifier("end_portal_frame", "eye=false,facing=north");

    @Override
    public void tick(World level, BlockPos blockPos, DataContainer dataContainer) {
        //BlockData<Float> pressedTimerData = dataContainer.get(EYE_TIMER);
        //BlockData<Float> pressedTimerPreviousData = dataContainer.get(EYE_TIMER_PREVIOUS);
        //boolean pressed = dataContainer.get(FILLED).get();
//
        //BlockData<Float> activateTimerData = dataContainer.get(ACTIVATE_TIMER);
        //BlockData<Float> activateTimerPreviousData = dataContainer.get(ACTIVATE_TIMER_PREVIOUS);
//
        //pressedTimerPreviousData.set(pressedTimerData.get());
        //pressedTimerData.set(Mth.clamp(pressedTimerData.get() + ((pressed ? 1 : -1) * (1F/6F)), 0, 1));
//
        //boolean activated =
        //        level.getBlockState(blockPos.offset(-1, 0, 0)).getBlock() == Blocks.END_PORTAL ||
        //        level.getBlockState(blockPos.offset(0, 0, -1)).getBlock() == Blocks.END_PORTAL ||
        //        level.getBlockState(blockPos.offset(1, 0, 0)).getBlock() == Blocks.END_PORTAL ||
        //        level.getBlockState(blockPos.offset(0, 0, 1)).getBlock() == Blocks.END_PORTAL;
//
        //activateTimerPreviousData.set(activateTimerData.get());
        //activateTimerData.set(Mth.clamp(activateTimerData.get() + ((activated ? 1 : -1) * (1F/12F)), 0, 1));
    }

    @Override
    public void receiveUpdate(World level, BlockPos pos, BlockState oldState, BlockState newState, DataContainer dataContainer) {
        //if(newState.getBlock() instanceof EndPortalFrameBlock){
        //    BlockData<Boolean> pressed = dataContainer.get(FILLED);
        //    pressed.set(newState.getValue(EndPortalFrameBlock.HAS_EYE));
        //}
        //TickableBlockRenderer.super.receiveUpdate(level, pos, oldState, newState, container);
    }

    @Override
    public void render(World level, BlockPos blockPos, DataContainer dataContainer, VertexConsumerProvider multiBufferSource, MatrixStack poseStack, float v, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightTexture, Matrix4f matrix4f, int packedLight, int packedOverlay) {

        /*BakedModel portalBaseBakedModel = MinecraftClient.getInstance().getBakedModelManager().getModel(PORTAL_BASE_LOCATION);
        BakedModel portalEyeBakedModel = MinecraftClient.getInstance().getBakedModelManager().getModel(PORTAL_EYE_LOCATION);

        BlockState blockState = level.getBlockState(blockPos);

        float eyeTimerNew = dataContainer.get(EYE_TIMER).get();
        float eyeTimerPrevious = dataContainer.get(EYE_TIMER_PREVIOUS).get();
        float eyeTimer = MathHelper.lerp(v, eyeTimerPrevious, eyeTimerNew);
        float activateTimerNew = dataContainer.get(ACTIVATE_TIMER).get();
        float activateTimerPrevious = dataContainer.get(ACTIVATE_TIMER_PREVIOUS).get();
        float activateTimer = MathHelper.lerp(v, activateTimerPrevious, activateTimerNew);
        boolean filled = dataContainer.get(FILLED).get();

        int yRot = switch(blockState.get(EndPortalFrameBlock.FACING)){
            case DOWN, UP, NORTH -> 0;
            case SOUTH -> 180;
            case EAST -> -90;
            case WEST -> 90;
        };

        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yRot));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(poseStack.peek(), multiBufferSource.getBuffer(TexturedRenderLayers.getEntityCutout()), null, portalBaseBakedModel, 1, 1, 1, packedLight, packedOverlay);
        if(filled){
            poseStack.push();

            eyeTimer = activateTimer > 0 ? 0 : eyeTimer;
            poseStack.translate(0, eyeInsertTimeline.getValueAt(TransformChannel.y, eyeTimer) * 0.5F, 0);

            poseStack.translate(0, eyeInsertTimeline.getValueAt(TransformChannel.y, activateTimer), 0);
            poseStack.translate(0, MathHelper.abs(MathHelper.sin(Util.getMeasuringTimeMs() / MathHelper.lerp(dataContainer.get(RANDOM_FLOAT).get(), 70F, 120F))) * -0.1F/16F * activateTimer, 0);


            MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(poseStack.peek(), multiBufferSource.getBuffer(TexturedRenderLayers.getEntityCutout()), null, portalEyeBakedModel, 1, 1, 1, packedLight, packedOverlay);
            poseStack.pop();
        }*/
    }

    /*@Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }*/

    static {
        RANDOM_FLOAT = BlockDataKey.of(() -> {
            return new Random().nextFloat();
        }).setBlocks(END_PORTAL_BLOCKS).build();
        EYE_TIMER = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(END_PORTAL_BLOCKS).build();
        EYE_TIMER_PREVIOUS = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(END_PORTAL_BLOCKS).build();
        ACTIVATE_TIMER = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(END_PORTAL_BLOCKS).build();
        ACTIVATE_TIMER_PREVIOUS = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(END_PORTAL_BLOCKS).build();
        FILLED = BlockDataKey.of(() -> {
            return false;
        }).setBlocks(END_PORTAL_BLOCKS).build();
    }
}
