package net.lizistired.animationoverhaul.render;

import net.lizistired.animationoverhaul.util.data.TransformChannel;
import net.lizistired.animationoverhaul.util.time.ChannelTimeline;
import net.lizistired.animationoverhaul.util.time.Easing;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockData;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockDataKey;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class LeverBlockRenderer implements TickableBlockRenderer {

    private static final BlockDataKey<Float> PULL_TIMER;
    private static final BlockDataKey<Float> PULL_TIMER_PREVIOUS;
    private static final BlockDataKey<Boolean> PULLED;

    public static final Block[] LEVERS = new Block[]{
            Blocks.LEVER
    };
    private static final ChannelTimeline pullUpTimeline = new ChannelTimeline()
            .addKeyframe(TransformChannel.xRot, 0, 90F)
            .addKeyframe(TransformChannel.xRot, 1, 0F, new Easing.CubicBezier(0.58F, 1.5F, 0.74F, 1F));

    private static final ChannelTimeline pullDownTimeline = new ChannelTimeline()
            .addKeyframe(TransformChannel.xRot, 0, 90F)
            .addKeyframe(TransformChannel.xRot, 1, 0F, Easing.CubicBezier.getInverseBezier(0.58F, 1.5F, 0.74F, 1F));


    public static final ModelIdentifier LEVER_BASE_LOCATION = new ModelIdentifier("lever", "face=wall,facing=west,powered=true");
    public static final ModelIdentifier LEVER_ARM_LOCATION = new ModelIdentifier("lever", "face=wall,facing=west,powered=false");

    @Override
    public void tick(World level, BlockPos blockPos, DataContainer dataContainer) {
        BlockData<Float> pressedTimerData = dataContainer.get(PULL_TIMER);
        BlockData<Float> pressedTimerPreviousData = dataContainer.get(PULL_TIMER_PREVIOUS);
        boolean pressed = dataContainer.get(PULLED).get();

        pressedTimerPreviousData.set(pressedTimerData.get());
        pressedTimerData.set(MathHelper.clamp(pressedTimerData.get() + ((pressed ? 1 : -1) * (1F/5F)), 0, 1));
    }

    @Override
    public void receiveUpdate(World level, BlockPos pos, BlockState oldState, BlockState newState, DataContainer dataContainer) {
        BlockData<Boolean> pressed = dataContainer.get(PULLED);
        if(newState.getBlock() instanceof LeverBlock){
            pressed.set(newState.get(LeverBlock.POWERED));
        } else {
            pressed.set(false);
        }
        //TickableBlockRenderer.super.receiveUpdate(level, pos, oldState, newState, container);
    }

    @Override
    public void render(World level, BlockPos blockPos, DataContainer dataContainer, VertexConsumerProvider multiBufferSource, MatrixStack poseStack, float v, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightTexture, Matrix4f matrix4f, int packedLight, int packedOverlay) {

        BakedModel leverBaseBakedModel = MinecraftClient.getInstance().getBakedModelManager().getModel(LEVER_BASE_LOCATION);
        BakedModel leverArmBakedModel = MinecraftClient.getInstance().getBakedModelManager().getModel(LEVER_ARM_LOCATION);

        BlockState blockState = level.getBlockState(blockPos);

        float pullTimerNew = dataContainer.get(PULL_TIMER).get();
        float pullTimerPrevious = dataContainer.get(PULL_TIMER_PREVIOUS).get();
        float pullTimer = MathHelper.lerp(v, pullTimerPrevious, pullTimerNew);
        boolean pulled = dataContainer.get(PULLED).get();

        int xRot = switch(blockState.get(LeverBlock.FACE)){
            case FLOOR -> 0;
            case WALL -> -90;
            case CEILING -> -180;
        };
        int yRot = switch(blockState.get(LeverBlock.FACING)){
            case DOWN, UP, NORTH -> 0;
            case SOUTH -> 180;
            case EAST -> -90;
            case WEST -> 90;
        };

        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yRot));
        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(xRot));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(poseStack.peek(), multiBufferSource.getBuffer(TexturedRenderLayers.getEntityCutout()), null, leverBaseBakedModel, 1, 1, 1, packedLight, packedOverlay);
        poseStack.push();

        ChannelTimeline channelTimeline = pulled ? pullUpTimeline : pullDownTimeline;

        poseStack.translate(0.5F, 1F/16F, 0.5F);
        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(channelTimeline.getValueAt(TransformChannel.xRot, pullTimer)));
        poseStack.translate(-0.5F, -1F/16F, -0.5F);

        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(poseStack.peek(), multiBufferSource.getBuffer(TexturedRenderLayers.getEntityCutout()), null, leverArmBakedModel, 1, 1, 1, packedLight, packedOverlay);
        poseStack.pop();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    static {
        PULL_TIMER = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(LEVERS).build();
        PULL_TIMER_PREVIOUS = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(LEVERS).build();
        PULLED = BlockDataKey.of(() -> {
            return false;
        }).setBlocks(LEVERS).build();
    }
}
