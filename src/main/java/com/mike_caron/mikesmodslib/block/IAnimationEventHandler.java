package com.mike_caron.mikesmodslib.block;

import net.minecraftforge.common.animation.Event;

public interface IAnimationEventHandler
{
    void handleAnimationEvent(float time, Iterable<Event> pastEvents);
}
