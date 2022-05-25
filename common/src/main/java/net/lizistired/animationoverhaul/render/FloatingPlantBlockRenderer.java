package net.lizistired.animationoverhaul.render;

import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockDataKey;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

public class FloatingPlantBlockRenderer implements TickableBlockRenderer {

    private static final Random RANDOM = new Random();
    private static final BlockDataKey<Integer> TIME;

    public static final Block[] FLOATING_PLANTS = new Block[]{
            Blocks.LILY_PAD
    };

    @Override
    public void tick(World level, BlockPos blockPos, DataContainer dataContainer) {
        dataContainer.get(TIME).set(dataContainer.get(TIME).get() + 1);
    }

    @Override
    public void render(World level, BlockPos blockPos, DataContainer dataContainer, VertexConsumerProvider multiBufferSource, MatrixStack poseStack, float v, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightTexture, Matrix4f matrix4f, int packedLight, int packedOverlay) {
        float time = dataContainer.get(TIME).get() + v;

        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.sin(time / 13) * 4));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        poseStack.translate(
                MathHelper.sin(time / 9F) * (1.5F / 16F),
                MathHelper.sin(time / 20F) * (0.5F / 16F),
                MathHelper.sin(time / 7F) * (1.5F / 16F)
        );

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(level.getBlockState(blockPos), poseStack, multiBufferSource, packedLight, packedOverlay);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    static {
        TIME = BlockDataKey.of(() -> {
            return RANDOM.nextInt(32767);
        }).setBlocks(FLOATING_PLANTS).build();
    }
}
