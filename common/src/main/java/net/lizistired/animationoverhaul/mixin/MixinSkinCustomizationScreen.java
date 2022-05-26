package net.lizistired.animationoverhaul.mixin;

import net.lizistired.animationoverhaul.gui.TestScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Unique
@Mixin(SkinOptionsScreen.class)
public class MixinSkinCustomizationScreen {

    private Screen previousScreen;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void getPreviousScreen(Screen screen, GameOptions options, CallbackInfo ci){
        this.previousScreen = screen;
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void redirectSkinCustomizationScreen(CallbackInfo ci){
        MinecraftClient.getInstance().setScreen(new TestScreen(previousScreen));
        ci.cancel();
    }
}
