package net.lizistired.animationoverhaul.mixin;

import net.lizistired.animationoverhaul.AnimationOverhaulMain;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModel;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import net.minecraft.util.Identifier;

@Mixin(GeometryModelManager.class)
public class MixinGeometryLoader {
    @Shadow @Final private static Map<Identifier, GeometryModel> MODELS;

    @Inject(method = "getModel", at = @At("HEAD"))
    private static void bbbb(Identifier location, CallbackInfoReturnable<GeometryModel> cir){
        AnimationOverhaulMain.LOGGER.warn(MODELS.keySet());
    }
}
