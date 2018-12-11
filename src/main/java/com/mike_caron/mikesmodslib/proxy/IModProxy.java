package com.mike_caron.mikesmodslib.proxy;

import net.minecraftforge.fml.common.event.*;

public interface IModProxy
{
    void preInit(FMLPreInitializationEvent e);
    void init(FMLInitializationEvent e);
    void postInit(FMLPostInitializationEvent e);
}
