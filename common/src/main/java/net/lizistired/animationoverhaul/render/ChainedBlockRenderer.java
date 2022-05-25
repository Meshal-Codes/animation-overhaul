package net.lizistired.animationoverhaul.render;

import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockData;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockDataKey;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

import java.util.Objects;
import java.util.Random;

public class ChainedBlockRenderer implements TickableBlockRenderer {
        private static final Random RANDOM = new Random();
        private static final BlockPos.Mutable CHAIN_POS = new BlockPos.Mutable();
        private static final BlockDataKey<Integer> TIME;
        private static final BlockDataKey<BlockPos> TOP;
        private static final BlockDataKey<Boolean> ATTACHED;

        public static final Block[] CHAINED_BLOCKS = new Block[]{
                Blocks.CHAIN,
                Blocks.LANTERN,
                Blocks.SOUL_LANTERN
        };

        public ChainedBlockRenderer() {
        }

        public void tick(World level, BlockPos pos, BlockRenderer.DataContainer container) {
            BlockData<Integer> time = container.get(TIME);
            BlockData<Boolean> attached = container.get(ATTACHED);
            BlockData<BlockPos> top = container.get(TOP);
            time.set((Integer)time.get() + 1);
            CHAIN_POS.set(pos).move(Direction.DOWN);
            if (!level.getBlockState(CHAIN_POS).isOf(Blocks.CHAIN)) {
                boolean shouldAttach = level.getBlockState(pos).isOf(Blocks.CHAIN) && Block.sideCoversSmallSquare(level, CHAIN_POS, Direction.UP);
                if (!((Boolean)attached.get()).equals(shouldAttach)) {
                    attached.set(shouldAttach);
                    if (level.getBlockState(CHAIN_POS.set(pos).move(Direction.UP)).isOf(Blocks.CHAIN)) {
                        container.updateNeighbor(Direction.DOWN);
                    }
                }
            }

            CHAIN_POS.set(pos).move(Direction.UP);
            if (!level.getBlockState(CHAIN_POS).isOf(Blocks.CHAIN) && !Objects.equals(top.get(), pos)) {
                top.set(pos);
                if (level.getBlockState(CHAIN_POS.set(pos).move(Direction.DOWN)).isOf(Blocks.CHAIN)) {
                    container.updateNeighbor(Direction.UP);
                }
            }

        }

        public void receiveUpdate(World level, BlockPos pos, BlockState oldState, BlockState newState, BlockRenderer.DataContainer container) {
            BlockData<BlockPos> top = container.get(TOP);
            BlockData<Boolean> attached = container.get(ATTACHED);
            CHAIN_POS.set(pos).move(Direction.DOWN);
            if (level.getBlockState(CHAIN_POS).isOf(Blocks.CHAIN) && !((Boolean)attached.get()).equals(container.get(ATTACHED, CHAIN_POS).get())) {
                attached.set((Boolean)container.get(ATTACHED, CHAIN_POS).get());
                container.updateNeighbor(Direction.UP);
            }

            CHAIN_POS.set(pos).move(Direction.UP);
            if (!level.getBlockState(CHAIN_POS).isOf(Blocks.CHAIN)) {
                top.set(pos);
                container.updateNeighbor(Direction.DOWN);
            } else if (level.getBlockState(CHAIN_POS).isOf(Blocks.CHAIN)) {
                top.set((BlockPos)container.get(TOP, CHAIN_POS).get());
                container.updateNeighbor(Direction.DOWN);
            }
            //BlockTags.ACACIA_LOGS.getValues().toArray();
        }

        public void render(World level, BlockPos blockPos, BlockRenderer.DataContainer container, VertexConsumerProvider buffer, MatrixStack poseStack, float partialTicks, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmap, Matrix4f projection, int packedLight, int packedOverlay) {
            if (!(Boolean)container.get(ATTACHED).get()) {
                BlockPos top = container.get(TOP).get() != null && level.getBlockState((BlockPos)container.get(TOP).get()).isOf(Blocks.CHAIN) ? (BlockPos)container.get(TOP).get() : blockPos;
                int distance = top.getY() - blockPos.getY();
                float time = (float)(Integer)container.get(TIME, top).get() + partialTicks;

                float xPos = MathHelper.sin((time / 16F) - distance * 0.25F) * Math.min(distance, 5F) / 5F * 1.1F/16F;
                float xPosBelow = MathHelper.sin((time / 16F) - (distance + 1) * 0.25F) * Math.min((distance + 1), 5F) / 5F * 1.1F/16F;
                float zRot = (float) -Math.atan((xPos - xPosBelow));

                poseStack.translate(0.5D, 1, 0.5D);

                poseStack.translate(xPos, 0, 0);
                poseStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(zRot));

                poseStack.translate(-0.5D, -1, -0.5D);
            }
            /*
            if (!(Boolean)container.get(ATTACHED).get()) {
                BlockPos top = container.get(TOP).get() != null && level.getBlockState((BlockPos)container.get(TOP).get()).is(Blocks.CHAIN) ? (BlockPos)container.get(TOP).get() : pos;
                int distance = top.getY() - pos.getY() + 1;
                float time = (float)(Integer)container.get(TIME, top).get() + partialTicks;
                poseStack.translate(0.5D, (double)distance, 0.5D);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(time / 20.0F)));
                poseStack.translate(-0.5D, (double)(-distance), -0.5D);
            }
             */

            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(level.getBlockState(blockPos), poseStack, buffer, packedLight, packedOverlay);
        }

        public BlockRenderType getRenderShape(BlockState state) {
            if (state.isOf(Blocks.CHAIN) && state.get(ChainBlock.AXIS) == Direction.Axis.Y) {
                return BlockRenderType.INVISIBLE;
            } else {
                return (state.isOf(Blocks.LANTERN) || state.isOf(Blocks.SOUL_LANTERN)) && (Boolean)state.get(LanternBlock.HANGING) ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
            }
        }

static {
        TIME = BlockDataKey.of(() -> {
        return RANDOM.nextInt(32767);
        }).setBlocks(CHAINED_BLOCKS).build();
        TOP = BlockDataKey.of(() -> {
        return BlockPos.fromLong(0);
        }).setBlocks(CHAINED_BLOCKS).build();
        ATTACHED = BlockDataKey.of(() -> {
        return false;
        }).setBlocks(CHAINED_BLOCKS).build();
        }
}
