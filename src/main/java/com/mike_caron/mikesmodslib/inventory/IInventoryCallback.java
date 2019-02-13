package com.mike_caron.mikesmodslib.inventory;

import com.mike_caron.mikesmodslib.util.OptionalInt;
import net.minecraft.inventory.IInventory;

public interface IInventoryCallback
{
    void onInventoryChanged(IInventory inventory, OptionalInt slotNumber);
}
