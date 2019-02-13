package com.mike_caron.mikesmodslib.inventory;

import com.google.common.base.Preconditions;
import com.mike_caron.mikesmodslib.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class ItemInventory extends GenericInventory {

    public static final String TAG_INVENTORY = "inventory";

    @Nonnull
    protected ItemStack containerStack;

    public ItemInventory(@Nonnull ItemStack containerStack, int size) {
        super("", false, size);
        Preconditions.checkNotNull(containerStack);
        this.containerStack = containerStack;
        final NBTTagCompound tag = ItemUtils.getItemTag(containerStack);
        readFromNBT(getInventoryTag(tag));

    }

    @Override
    public void onInventoryChanged(int slotNumber) {
        super.onInventoryChanged(slotNumber);

        NBTTagCompound tag = ItemUtils.getItemTag(containerStack);
        NBTTagCompound inventoryTag = getInventoryTag(tag);
        writeToNBT(inventoryTag);
        tag.setTag(TAG_INVENTORY, inventoryTag);
        containerStack.setTagCompound(tag);
    }

    public static NBTTagCompound getInventoryTag(NBTTagCompound tag) {
        return tag.getCompoundTag(TAG_INVENTORY);
    }

}
