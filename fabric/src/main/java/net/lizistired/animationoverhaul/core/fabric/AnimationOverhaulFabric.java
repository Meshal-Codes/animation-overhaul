package net.lizistired.animationoverhaul.core.fabric;

import net.lizistired.animationoverhaul.AnimationOverhaulMain;
import net.fabricmc.api.ModInitializer;

public class AnimationOverhaulFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AnimationOverhaulMain.PLATFORM.setup();
    }
}
