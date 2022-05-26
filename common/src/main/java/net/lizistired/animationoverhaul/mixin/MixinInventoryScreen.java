package net.lizistired.animationoverhaul.mixin;

import net.lizistired.animationoverhaul.access.LivingEntityAccess;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {
    @Inject(method = "drawEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;setRenderShadows(Z)V"))
    private static void setRendererToEntity(int i, int j, int k, float f, float g, LivingEntity livingEntity, CallbackInfo ci){
        ((LivingEntityAccess)livingEntity).setUseInventoryRenderer(true);
    }
}
