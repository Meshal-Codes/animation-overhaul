package gg.moonflower.animationoverhaul.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class GamemodeGrabber {
    public static boolean bl;
    public boolean isCreative(LivingEntity livingEntity) {
        Player playerEntity = (Player) livingEntity;
        bl = playerEntity.getAbilities().flying;
        return bl;
        /*if (playerEntity.isCreative()) {
            bl = playerEntity.getAbilities().flying;
            return bl;
            //return playerEntity.getAbilities().mayfly
            //return playerEntity.getAbilities().flying;
        }
        else {return false;}*/
    }

    /*public boolean isFlying(LivingEntity livingEntity){
        Player playerEntity = (Player) livingEntity;
        if (playerEntity.fall)
        if (playerEntity.getBlockY() >= 5)

    }*/

    public String getPlayerName(LivingEntity livingEntity){
        Player playerEntity = (Player) livingEntity;
        return playerEntity.getScoreboardName();
    }
}
