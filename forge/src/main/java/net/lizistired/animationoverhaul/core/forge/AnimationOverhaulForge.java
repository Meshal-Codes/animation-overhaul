package net.lizistired.animationoverhaul.core.forge;

import net.lizistired.animationoverhaul.AnimationOverhaulMain;
import net.minecraftforge.fml.common.Mod;

@Mod(AnimationOverhaulMain.MOD_ID)
public class AnimationOverhaulForge {
    public AnimationOverhaulForge() {
        AnimationOverhaulMain.PLATFORM.setup();
    }
}
