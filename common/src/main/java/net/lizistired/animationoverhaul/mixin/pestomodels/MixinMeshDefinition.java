package net.lizistired.animationoverhaul.mixin.pestomodels;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelData.class)
public class MixinMeshDefinition {

    @Inject(method = "getRoot", at = @At("HEAD"))
    private void startModel(CallbackInfoReturnable<ModelPartData> cir){
        System.out.println("");
        System.out.println("");
        System.out.println("-----------------Begin new entity--------------");
        System.out.println("");
    }
}
