package com.mike_caron.mikesmodslib.energy;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

/**
 * This energy store assumes the capacity, etc are set consistently. Only the energy itself is serialized
 */
public class SerializableEnergyStorage
    extends EnergyStorage
    implements INBTSerializable<NBTBase>
{
    public SerializableEnergyStorage(int capacity)
    {
        super(capacity);
    }

    public SerializableEnergyStorage(int capacity, int maxTransfer)
    {
        super(capacity, maxTransfer);
    }

    public SerializableEnergyStorage(int capacity, int maxReceive, int maxExtract)
    {
        super(capacity, maxReceive, maxExtract);
    }

    public SerializableEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy)
    {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public NBTBase serializeNBT()
    {
        return new NBTTagInt(energy);
    }

    @Override
    public void deserializeNBT(NBTBase nbtTagInt)
    {
        if(nbtTagInt instanceof NBTTagInt)
        {
            energy = ((NBTTagInt) nbtTagInt).getInt();

            if (energy > capacity)
                energy = capacity;
        }
        else
        {
            energy = 0;
        }
    }
}
