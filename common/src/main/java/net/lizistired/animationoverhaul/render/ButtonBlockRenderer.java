package net.lizistired.animationoverhaul.render;

import net.lizistired.animationoverhaul.util.data.TransformChannel;
import net.lizistired.animationoverhaul.util.time.ChannelTimeline;
import net.lizistired.animationoverhaul.util.time.Easing;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockData;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockDataKey;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class ButtonBlockRenderer implements TickableBlockRenderer {

    private static final BlockDataKey<Float> PRESSED_TIMER;
    private static final BlockDataKey<Float> PRESSED_TIMER_PREVIOUS;
    private static final BlockDataKey<Boolean> PRESSED;

    public static final Block[] BUTTONS = new Block[]{
            Blocks.OAK_BUTTON,
            Blocks.SPRUCE_BUTTON,
            Blocks.BIRCH_BUTTON,
            Blocks.JUNGLE_BUTTON,
            Blocks.ACACIA_BUTTON,
            Blocks.DARK_OAK_BUTTON,
            Blocks.CRIMSON_BUTTON,
            Blocks.WARPED_BUTTON,
            Blocks.STONE_BUTTON,
            Blocks.POLISHED_BLACKSTONE_BUTTON
    };
    private static final ChannelTimeline pressDownTimeline = new ChannelTimeline()
            .addKeyframe(TransformChannel.y, 0, 0F)
            .addKeyframe(TransformChannel.y, 1, -1.5F/16F, new Easing.CubicBezier(0.18F, 0.59F, 0.45F, 1.6F))
            .addKeyframe(TransformChannel.y, 2, -1.5F/16F, new Easing.CubicBezier(0.18F, 0.59F, 0.45F, 1.6F));

    private static final ChannelTimeline pressUpTimeline = new ChannelTimeline()
            .addKeyframe(TransformChannel.y, 0, 0F)
            .addKeyframe(TransformChannel.y, 1, -1/4F/16F, Easing.CubicBezier.bezierInCirc())
            .addKeyframe(TransformChannel.y, 2, 0F, Easing.CubicBezier.bezierOutCirc())
            .addKeyframe(TransformChannel.y, 4, -1.5F/16F, new Easing.CubicBezier(0.17F,0.53F,0.47F,1F));



    @Override
    public void tick(World level, BlockPos blockPos, DataContainer dataContainer) {
        BlockData<Float> pressedTimerData = dataContainer.get(PRESSED_TIMER);
        BlockData<Float> pressedTimerPreviousData = dataContainer.get(PRESSED_TIMER_PREVIOUS);
        boolean pressed = dataContainer.get(PRESSED).get();

        pressedTimerPreviousData.set(pressedTimerData.get());
        pressedTimerData.set(MathHelper.clamp(pressedTimerData.get() + ((pressed ? 1 : -1) * (1F/6F)), 0, 1));
    }

    @Override
    public void receiveUpdate(World level, BlockPos pos, BlockState oldState, BlockState newState, DataContainer dataContainer) {

        BlockData<Boolean> pressed = dataContainer.get(PRESSED);
        if(newState.getBlock() instanceof AbstractButtonBlock){
            pressed.set(newState.get(AbstractButtonBlock.POWERED));
        } else {
            pressed.set(false);
        }
        //TickableBlockRenderer.super.receiveUpdate(level, pos, oldState, newState, container);
    }

    @Override
    public void render(World level, BlockPos blockPos, DataContainer dataContainer, VertexConsumerProvider multiBufferSource, MatrixStack poseStack, float v, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightTexture, Matrix4f matrix4f, int packedLight, int packedOverlay) {
        BlockState blockState = level.getBlockState(blockPos);

        float pressedTimerNew = dataContainer.get(PRESSED_TIMER).get();
        float pressedTimerPrevious = dataContainer.get(PRESSED_TIMER_PREVIOUS).get();
        float pressedTimer = MathHelper.lerp(v, pressedTimerPrevious, pressedTimerNew);
        boolean pressed = dataContainer.get(PRESSED).get();

        int xRot = switch(blockState.get(AbstractButtonBlock.FACE)){
            case FLOOR -> 0;
            case WALL -> -90;
            case CEILING -> -180;
        };
        int yRot = switch(blockState.get(AbstractButtonBlock.FACING)){
            case DOWN, UP, NORTH -> 0;
            case SOUTH -> 180;
            case EAST -> -90;
            case WEST -> 90;
        };

        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yRot));
        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(xRot));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        poseStack.push();

        ChannelTimeline channelTimeline = pressed ? pressDownTimeline : pressUpTimeline;
        poseStack.translate(0, channelTimeline.getValueAt(TransformChannel.y, pressedTimer), 0);

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(level.getBlockState(blockPos).getBlock().getDefaultState().with(AbstractButtonBlock.FACE, WallMountLocation.FLOOR), poseStack, multiBufferSource, packedLight, packedOverlay);

        poseStack.pop();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    static {
        PRESSED_TIMER = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(BUTTONS).build();
        PRESSED_TIMER_PREVIOUS = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(BUTTONS).build();
        PRESSED = BlockDataKey.of(() -> {
            return false;
        }).setBlocks(BUTTONS).build();
    }
}
