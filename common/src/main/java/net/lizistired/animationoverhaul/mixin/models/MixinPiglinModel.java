package net.lizistired.animationoverhaul.mixin.models;

import net.lizistired.animationoverhaul.access.ModelAccess;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Unique
@Mixin(PiglinEntityModel.class)
public class MixinPiglinModel implements ModelAccess {

    private ModelPart rootModelPart;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void getRootModelPart(ModelPart modelPart, CallbackInfo ci){
        this.rootModelPart = modelPart;
    }

    @Override
    public ModelPart getModelPart(String identifier) {
        return this.rootModelPart.getChild(identifier);
    }

    @Override
    public ModelPart getRootModelPart() {
        return this.rootModelPart;
    }
}
