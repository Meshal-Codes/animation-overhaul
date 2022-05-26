package net.lizistired.animationoverhaul.mixin.pestomodels;

import net.lizistired.animationoverhaul.access.CubeDefinitionAccess;
import net.lizistired.animationoverhaul.access.CubeDeformationAccess;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelCuboidData.class)
public class MixinCubeDefinition implements CubeDefinitionAccess {
    @Shadow @Final private Vec3f dimensions;

    @Shadow @Final private Vec3f offset;

    @Shadow @Final private Dilation extraSize;

    public void outputString() {
        String cubeString = "cube = cmds.polyCube(width=" + this.dimensions.getX() + ", height=" + this.dimensions.getY() + ", depth=" + this.dimensions.getZ() + ")";
        System.out.println(cubeString);
        if(((CubeDeformationAccess)this.extraSize).getGrow() > 0){
            String growString = "cmds.polyExtrudeFacet(cube, thickness=" + ((CubeDeformationAccess)this.extraSize).getGrow() + ")";
            System.out.println(growString);
        }
        String xformString = "cmds.xform(cube, relative=True, translation=[" + (-(this.dimensions.getX() / 2) - this.offset.getX()) + "," + (-(this.dimensions.getY() / 2) - this.offset.getY())  + "," + (-(this.dimensions.getZ() / 2) - this.offset.getZ())  + "])";
        System.out.println(xformString);
        String parentString = "cmds.parent(cube, modelPartGroup)";
        System.out.println(parentString);
    }
}
