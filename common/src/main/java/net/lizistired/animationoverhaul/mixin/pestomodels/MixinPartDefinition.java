package net.lizistired.animationoverhaul.mixin.pestomodels;

import net.lizistired.animationoverhaul.pestomodels.ModelOutputImpl;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelPartData.class)
public class MixinPartDefinition {
    @Shadow @Final private ModelTransform rotationData;

    @Inject(method = "addChild", at = @At("HEAD"))
    private void addModelPartOutput(String string, ModelPartBuilder cubeListBuilder, ModelTransform partPose, CallbackInfoReturnable<ModelPartData> cir){
        ModelOutputImpl.outputPartDefinition(string, partPose, cubeListBuilder, this.rotationData);
    }
}
