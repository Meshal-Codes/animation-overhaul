package net.lizistired.animationoverhaul.mixin;

import net.lizistired.animationoverhaul.render.DebugGadgetRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinGui {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"))
    private void injectDebugGadgetRenderMethod(MatrixStack poseStack, float f, CallbackInfo ci){
        poseStack.push();
        DebugGadgetRenderer.INSTANCE.render(poseStack, f);
        poseStack.pop();
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void injectDebugGadgetTickMethod(CallbackInfo ci){
        DebugGadgetRenderer.INSTANCE.tick();
    }
}
