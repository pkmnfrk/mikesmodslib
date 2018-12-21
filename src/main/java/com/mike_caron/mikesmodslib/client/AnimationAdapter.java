package com.mike_caron.mikesmodslib.client;

import com.mike_caron.mikesmodslib.block.IAnimationEventHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.animation.AnimationTESR;
import net.minecraftforge.common.animation.Event;

public class AnimationAdapter<T extends TileEntity>
    extends AnimationTESR<T>
{
    @Override
    public void handleEvents(T te, float time, Iterable<Event> pastEvents)
    {
        if(te instanceof IAnimationEventHandler)
        {
            ((IAnimationEventHandler) te).handleAnimationEvent(time, pastEvents);
        }
    }
}
