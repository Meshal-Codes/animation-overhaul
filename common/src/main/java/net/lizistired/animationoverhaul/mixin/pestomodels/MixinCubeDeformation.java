package net.lizistired.animationoverhaul.mixin.pestomodels;

import net.lizistired.animationoverhaul.access.CubeDeformationAccess;
import net.minecraft.client.model.Dilation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Dilation.class)
public class MixinCubeDeformation implements CubeDeformationAccess {

    @Shadow @Final private float radiusX;

    @Override
    public float getGrow() {
        return this.radiusX;
    }
}
