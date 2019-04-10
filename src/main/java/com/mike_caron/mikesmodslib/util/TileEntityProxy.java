package com.mike_caron.mikesmodslib.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityProxy <T extends TileEntity>
    implements INBTSerializable<NBTTagCompound>
{
    BlockPos pos;
    int lastCheck = 0;

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        if(pos != null)
        {
            ret.setInteger("x", pos.getX());
            ret.setInteger("y", pos.getY());
            ret.setInteger("z", pos.getZ());
        }
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound)
    {
        int x = compound.getInteger("x");
        int y = compound.getInteger("y");
        int z = compound.getInteger("z");

        pos = new BlockPos(x, y, z);
    }

    @Nullable
    @SuppressWarnings({"unchecked"})
    public T getTileEntity(@Nonnull World world)
    {
        if(world.isRemote)
            return null;

        if(pos != null)
        {
            TileEntity ret = world.getTileEntity(pos);

            try
            {
                return (T)ret;
            }
            catch(ClassCastException ex)
            {
                //hmm
                pos = null;
            }
        }

        lastCheck += 1;
        if(lastCheck >= 20)
        {
            lastCheck = 0;

            BlockPos p = findTileEntity(world);

            if(p != null)
                pos = p;

            return getTileEntity(world);
        }

        return null;
    }

    public boolean isValid()
    {
        return pos != null;
    }

    @Nullable
    protected abstract BlockPos findTileEntity(@Nonnull World world);
}
