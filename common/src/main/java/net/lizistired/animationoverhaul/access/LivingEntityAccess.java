package net.lizistired.animationoverhaul.access;

import net.lizistired.animationoverhaul.util.animation.LocatorRig;
import net.minecraft.util.math.BlockPos;

public interface LivingEntityAccess {
    //float getAnimationVariable(String animationVariable);
    //void setAnimationVariable(String animationVariable, float newValue);
    void setRecordPlayerNearbyValues(String songName, boolean songPlaying, BlockPos songOrigin);
    boolean getIsSongPlaying();
    BlockPos getSongOrigin();
    String getSongName();
    String getPreviousEquippedArmor();
    void setEquippedArmor(String currentArmor);

    boolean getUseInventoryRenderer();
    void setUseInventoryRenderer(boolean bool);

    LocatorRig getLocatorRig();
    void storeLocatorRig(LocatorRig locatorRig);
}
