package com.mike_caron.mikesmodslib.util;

import com.mike_caron.mikesmodslib.Mod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.UUID;

public class LastResortUtils
{
    private LastResortUtils() {}

    public static EntityPlayer getPlayer(UUID uuid)
    {
        WorldServer worldServer = DimensionManager.getWorld(0);

        if(worldServer.isRemote)
            throw new RuntimeException("The author of " + Mod.modId + " made an error! Please report this!");

        return worldServer.getPlayerEntityByUUID(uuid);
    }
}
