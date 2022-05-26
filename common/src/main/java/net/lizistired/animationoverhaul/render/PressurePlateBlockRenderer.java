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
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class PressurePlateBlockRenderer implements TickableBlockRenderer {

    private static final BlockDataKey<Float> PRESSED_TIMER;
    private static final BlockDataKey<Float> PRESSED_TIMER_PREVIOUS;
    private static final BlockDataKey<Boolean> PRESSED;

    public static final Block[] PRESSURE_PLATES = new Block[]{
            Blocks.ACACIA_PRESSURE_PLATE,
            Blocks.BIRCH_PRESSURE_PLATE,
            Blocks.CRIMSON_PRESSURE_PLATE,
            Blocks.OAK_PRESSURE_PLATE,
            Blocks.DARK_OAK_PRESSURE_PLATE,
            Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Blocks.JUNGLE_PRESSURE_PLATE,
            Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
            Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            Blocks.SPRUCE_PRESSURE_PLATE,
            Blocks.STONE_PRESSURE_PLATE,
            Blocks.WARPED_PRESSURE_PLATE
    };

    private static final ChannelTimeline pressDownTimeline = new ChannelTimeline()
            .addKeyframe(TransformChannel.y, 0, 0F)
            .addKeyframe(TransformChannel.y, 1, -1/2F/16F, new Easing.CubicBezier(0.18F, 0.59F, 0.45F, 1.13F));

    private static final ChannelTimeline pressUpTimeline = new ChannelTimeline()
            .addKeyframe(TransformChannel.y, 0, 0F)
            .addKeyframe(TransformChannel.y, 1, -1/2F/16F, new Easing.CubicBezier(0.51F, -0.8F, 0.61F, 0.13F));

    @Override
    public void tick(World level, BlockPos blockPos, DataContainer dataContainer) {
        BlockData<Float> pressedTimerData = dataContainer.get(PRESSED_TIMER);
        BlockData<Float> pressedTimerPreviousData = dataContainer.get(PRESSED_TIMER_PREVIOUS);
        boolean pressed = dataContainer.get(PRESSED).get();

        pressedTimerPreviousData.set(pressedTimerData.get());
        pressedTimerData.set(MathHelper.clamp(pressedTimerData.get() + ((pressed ? 1 : -1) * (1F/4F)), 0, 1));
    }

    @Override
    public void receiveUpdate(World level, BlockPos pos, BlockState oldState, BlockState newState, DataContainer dataContainer) {
        BlockData<Boolean> pressed = dataContainer.get(PRESSED);
        if(newState.getBlock() instanceof PressurePlateBlock){
            pressed.set(newState.get(PressurePlateBlock.POWERED));
        } else if(newState.getBlock() instanceof WeightedPressurePlateBlock){
            pressed.set(newState.get(WeightedPressurePlateBlock.POWER) > 0);
        } else {
            pressed.set(false);
        }
        //TickableBlockRenderer.super.receiveUpdate(level, pos, oldState, newState, container);
    }

    @Override
    public void render(World level, BlockPos blockPos, DataContainer dataContainer, VertexConsumerProvider multiBufferSource, MatrixStack poseStack, float v, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightTexture, Matrix4f matrix4f, int packedLight, int packedOverlay) {
        float pressedTimerNew = dataContainer.get(PRESSED_TIMER).get();
        float pressedTimerPrevious = dataContainer.get(PRESSED_TIMER_PREVIOUS).get();
        float pressedTimer = MathHelper.lerp(v, pressedTimerPrevious, pressedTimerNew);

        boolean pressed = dataContainer.get(PRESSED).get();

        ChannelTimeline channelTimeline = pressed ? pressDownTimeline : pressUpTimeline;
        poseStack.translate(0, channelTimeline.getValueAt(TransformChannel.y, pressedTimer), 0);

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(level.getBlockState(blockPos).getBlock().getDefaultState(), poseStack, multiBufferSource, packedLight, packedOverlay);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    static {
        PRESSED_TIMER = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(PRESSURE_PLATES).build();
        PRESSED_TIMER_PREVIOUS = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(PRESSURE_PLATES).build();
        PRESSED = BlockDataKey.of(() -> {
            return false;
        }).setBlocks(PRESSURE_PLATES).build();
    }
}
