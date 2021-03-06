package com.mike_caron.mikesmodslib.util;

import com.google.common.base.Preconditions;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemUtils
{
    @GameRegistry.ItemStackHolder("minecraft:clock")
    public static ItemStack CLOCK;

    public static final Pattern leadingCount = Pattern.compile("^(\\d+)x");

    @Nonnull
    public static ItemStack getStackFromTag(String tag)
    {
        try
        {
            int count = 1;

            Matcher res = leadingCount.matcher(tag);
            while(res.find())
            {
                count = Integer.parseInt(res.group(1));
                tag = tag.substring(res.end());
            }


            String[] parts = tag.split(":");
            if (parts.length == 1)
            {
                //assume minecraft:item:0
                Item item = Item.getByNameOrId(parts[0]);
                Preconditions.checkNotNull(item);
                return new ItemStack(item, count);
            }
            else if (parts.length == 2)
            {
                // this can either be mod:item:0 or minecraft:item:meta

                Item item = Item.getByNameOrId(tag);

                if (item != null)
                {
                    return new ItemStack(item, count);
                }

                // try minecraft:item:meta
                int meta;

                if(parts[1].equals("*"))
                {
                    meta = OreDictionary.WILDCARD_VALUE;
                }
                else
                {
                    meta = Integer.parseInt(parts[1]);
                }

                item = Item.getByNameOrId("minecraft:" + parts[0]);

                Preconditions.checkNotNull(item);

                return new ItemStack(item, count, meta);
            }
            else if (parts.length == 3)
            {
                //this has to be mod:item:meta
                int meta;
                if(parts[2].equals("*"))
                {
                    meta = OreDictionary.WILDCARD_VALUE;
                }
                else
                {
                    meta = Integer.parseInt(parts[2]);
                }
                Item item = Item.getByNameOrId(parts[0] + ":" + parts[1]);

                Preconditions.checkNotNull(item, "Can't locate the item " + tag);
                return new ItemStack(item, count, meta);

            }
        }
        catch (NullPointerException ex)
        {
            //handled below
        }
        catch (NumberFormatException ex)
        {
            throw new RuntimeException("Can't locate the item " + tag, ex);
        }

        //return null;
        throw new RuntimeException("I don't understand the item " + tag);
    }

    @Nonnull
    public static Item getItemFromTag(String tag)
    {
        try
        {
            Item item = Item.getByNameOrId(tag);
            Preconditions.checkNotNull(item);
            return item;

        }
        catch (NullPointerException ex)
        {
            //handled below
        }

        //return null;
        throw new RuntimeException("I don't understand the item " + tag);
    }

    @Nonnull
    public static String getTagFromItem(@Nonnull Item item)
    {
        ResourceLocation res = Item.REGISTRY.getNameForObject(item);

        Preconditions.checkNotNull(res);

        if(res.getNamespace().equals("minecraft"))
        {
            return res.getPath();
        }

        return res.toString();
    }

    public static String getTagFromStack(ItemStack stack)
    {
        StringBuilder ret = new StringBuilder();

        ret.append(stack.getItem().getRegistryName().toString());

        if(stack.getMetadata() != 0)
        {
            ret.append(":");
            ret.append(stack.getMetadata());
        }

        return ret.toString();
    }

    public static boolean areEqual(@Nullable NonNullList<ItemStack> a, @Nullable NonNullList<ItemStack> b)
    {
        if(a == b)
            return true;

        if(a != null && b == null)
            return false;

        if(a == null) //b is implied not null here
            return false;

        if(a.size() != b.size())
            return false;

        for(int i = 0; i < a.size(); i++)
        {
            ItemStack ai = a.get(i);
            ItemStack bi = b.get(i);

            if(!ai.isItemEqual(bi))
                return false;
        }

        return true;
    }

    public static void giveToPlayerOrDrop(@Nonnull ItemStack itemStack, @Nonnull EntityPlayer player)
    {
        ItemStack newStack = itemStack.copy();

        if(!player.inventory.addItemStackToInventory(newStack))
        {
            dropItem(player.getEntityWorld(), newStack, player.posX, player.posY, player.posZ);
        }
    }

    public static void dropItem(@Nonnull World world, @Nonnull ItemStack itemStack, double x, double y, double z)
    {
        EntityItem item = new EntityItem(world, x, y, z, itemStack);
        world.spawnEntity(item);
    }

    @Nonnull
    public static ItemStack insertItemIfPossible(@Nonnull ItemStack itemStack, @Nonnull IItemHandler itemHandler)
    {

        for(int i = 0; i < itemHandler.getSlots(); i++)
        {
            if(!itemHandler.isItemValid(i,itemStack))
                continue;

            ItemStack ret = itemHandler.insertItem(i, itemStack, true);

            if(ret != itemStack)
            {
                ret = itemHandler.insertItem(i, itemStack, false);
                return ret;
            }
        }

        return itemStack;
    }

    @Nonnull
    public static NBTTagCompound getItemTag(@Nonnull ItemStack stack)
    {
        NBTTagCompound ret = stack.getTagCompound();
        if(ret == null)
        {
            ret = new NBTTagCompound();
            stack.setTagCompound(ret);
        }
        return ret;
    }
}
