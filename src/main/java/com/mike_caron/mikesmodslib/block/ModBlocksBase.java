package com.mike_caron.mikesmodslib.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ModBlocksBase
{

    public static void registerBlocks(Class<? extends ModBlocksBase> clazz)
    {
        visitAllBlocks(clazz, (block, field) -> {
            if(block.getClass().isAnnotationPresent(RegisterOreDictionary.class))
            {
                RegisterOreDictionary ann = block.getClass().getAnnotation(RegisterOreDictionary.class);

                OreDictionary.registerOre(ann.value(), block);
            }


        });
    }

    public static void registerItems(RegistryEvent.Register<Item> event, Class<? extends ModBlocksBase> clazz)
    {
        registerItems(event, clazz, null);
    }

    public static void registerItems(RegistryEvent.Register<Item> event, Class<? extends ModBlocksBase> clazz, @Nullable Function<Block, ItemBlock> custom)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        visitAllBlocks(clazz, (block, field) -> {
            if(block == null)
                return;
            if(block.getRegistryName() == null)
                throw new NullPointerException("Registry name is missing for " + block.toString());

            ItemBlock b = null;
            if(custom != null) b = custom.apply(block);
            if(b == null) b = new ItemBlock(block);
            registry.register(
                b.setRegistryName(block.getRegistryName())
            );
        });
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels(Class<? extends ModBlocksBase> clazz)
    {
        visitAllBlocks(clazz, (block, field) ->
        {
            if(block instanceof BlockBase)
            {
                ((BlockBase) block).initModel();
            }
        });
    }

    public static void visitAllBlocks(Class<? extends ModBlocksBase> clazz, BiConsumer<Block, Field> visitor)
    {
        Arrays.stream(clazz.getDeclaredFields()).filter(f -> Modifier.isStatic(f.getModifiers()) && Block.class.isAssignableFrom(f.getType())).forEach(f -> {
            try
            {
                Block ret = (Block)f.get(null);

                visitor.accept(ret, f);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException("Unable to reflect upon myself??");
            }
        });
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Register
    {
         String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RegisterOreDictionary
    {
        String value();
    }

}
