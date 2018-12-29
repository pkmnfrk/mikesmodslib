package com.mike_caron.mikesmodslib.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public abstract class TEContainerBase<T extends TileEntityBase>
    extends ContainerBase
{
    @Nonnull
    protected final T te;

    public TEContainerBase(IInventory player, T te)
    {
        super(player);

        this.te = te;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return te.canInteractWith(entityPlayer);
    }

    public BlockPos getPos()
    {
        return te.getPos();
    }

    public int getDimension()
    {
        return te.getWorld().provider.getDimension();
    }
}
