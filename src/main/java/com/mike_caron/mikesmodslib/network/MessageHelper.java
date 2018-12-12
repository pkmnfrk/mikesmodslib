package com.mike_caron.mikesmodslib.network;

import com.mike_caron.mikesmodslib.Mod;

public class MessageHelper
{
    private MessageHelper(){}

    public static void send(CtoSMessage message)
    {
        Mod.networkWrapper.sendToServer(message);
    }

    public static void send(MessageUpdateGui message)
    {
        Mod.networkWrapper.sendToServer(message);
    }
}
