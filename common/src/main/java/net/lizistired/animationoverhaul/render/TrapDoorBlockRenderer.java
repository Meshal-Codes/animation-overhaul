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
import net.minecraft.block.TrapdoorBlock;
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

public class TrapDoorBlockRenderer implements TickableBlockRenderer {

    private static final BlockDataKey<Float> OPEN_TIMER;
    private static final BlockDataKey<Float> OPEN_TIMER_PREVIOUS;
    private static final BlockDataKey<Boolean> OPEN;

    public static final Block[] TRAPDOORS = new Block[]{
            Blocks.ACACIA_TRAPDOOR,
            Blocks.BIRCH_TRAPDOOR,
            Blocks.DARK_OAK_TRAPDOOR,
            Blocks.JUNGLE_TRAPDOOR,
            Blocks.OAK_TRAPDOOR,
            Blocks.SPRUCE_TRAPDOOR,
            Blocks.CRIMSON_TRAPDOOR,
            Blocks.WARPED_TRAPDOOR,
            Blocks.IRON_TRAPDOOR
    };

    private static final ChannelTimeline openTimeline = new ChannelTimeline()
            .addKeyframe(TransformChannel.xRot, 0, -90F)
            .addKeyframe(TransformChannel.xRot, 1, 0F, new Easing.CubicBezier(0.71F, 1.95F, 0.57F, 0.75F));

    private static final ChannelTimeline closeTimeline = new ChannelTimeline()
            .addKeyframe(TransformChannel.xRot, 0, -90F)
            .addKeyframe(TransformChannel.xRot, 1, 0F, Easing.CubicBezier.getInverseBezier(0.71F, 1.95F, 0.57F, 0.75F));

    @Override
    public void tick(World level, BlockPos blockPos, DataContainer dataContainer) {
        BlockData<Float> pressedTimerData = dataContainer.get(OPEN_TIMER);
        BlockData<Float> pressedTimerPreviousData = dataContainer.get(OPEN_TIMER_PREVIOUS);
        boolean pressed = dataContainer.get(OPEN).get();

        pressedTimerPreviousData.set(pressedTimerData.get());
        pressedTimerData.set(MathHelper.clamp(pressedTimerData.get() + ((pressed ? 1 : -1) * (1F/5F)), 0, 1));
    }

    @Override
    public void receiveUpdate(World level, BlockPos pos, BlockState oldState, BlockState newState, DataContainer dataContainer) {
        BlockData<Boolean> pressed = dataContainer.get(OPEN);
        if(newState.getBlock() instanceof TrapdoorBlock){
            pressed.set(newState.get(TrapdoorBlock.OPEN));
        } else {
            pressed.set(false);
        }
        //TickableBlockRenderer.super.receiveUpdate(level, pos, oldState, newState, container);
    }

    @Override
    public void render(World level, BlockPos blockPos, DataContainer dataContainer, VertexConsumerProvider multiBufferSource, MatrixStack poseStack, float v, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightTexture, Matrix4f matrix4f, int packedLight, int packedOverlay) {
        BlockState blockState = level.getBlockState(blockPos);

        if(level.getBlockState(blockPos).getBlock() instanceof TrapdoorBlock){

        }
        float openTimerNew = dataContainer.get(OPEN_TIMER).get();
        float openTimerPrevious = dataContainer.get(OPEN_TIMER_PREVIOUS).get();
        float openTimer = MathHelper.lerp(v, openTimerPrevious, openTimerNew);
        boolean open = dataContainer.get(OPEN).get();

        int zRot = switch(blockState.get(TrapdoorBlock.HALF)){
            case TOP -> 180;
            case BOTTOM -> 0;
        };
        int yRot = switch(blockState.get(TrapdoorBlock.FACING)){
            case DOWN, NORTH, UP -> 0;
            case SOUTH -> 180;
            case EAST -> -90;
            case WEST -> 90;
        };

        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yRot));
        poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(zRot));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        poseStack.push();

        ChannelTimeline channelTimeline = open ? openTimeline : closeTimeline;


        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.translate(0, -13F/32F, 13F/32F);
        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(channelTimeline.getValueAt(TransformChannel.xRot, openTimer)));
        poseStack.translate(0, 13F/32F, -13F/32F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(level.getBlockState(blockPos).getBlock().getDefaultState().with(TrapdoorBlock.OPEN, true), poseStack, multiBufferSource, packedLight, packedOverlay);

        poseStack.pop();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    static {
        OPEN_TIMER = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(TRAPDOORS).build();
        OPEN_TIMER_PREVIOUS = BlockDataKey.of(() -> {
            return 0F;
        }).setBlocks(TRAPDOORS).build();
        OPEN = BlockDataKey.of(() -> {
            return false;
        }).setBlocks(TRAPDOORS).build();
    }
}
