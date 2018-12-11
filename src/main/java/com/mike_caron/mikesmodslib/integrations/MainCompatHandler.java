package com.mike_caron.mikesmodslib.integrations;

import net.minecraftforge.fml.common.Loader;

public class MainCompatHandler
{
    public static void registerAllPreInit()
    {
        registerTOP();
        registerWaila();
    }

    public static void registerAllInit()
    {
    }

    public static void registerTOP()
    {
        if(Loader.isModLoaded("theoneprobe"))
        {
            TOPCompatibility.register();
        }
    }

    public static void registerWaila()
    {
        if(Loader.isModLoaded("waila"))
        {
            WailaCompatibility.register();
        }
    }
}
