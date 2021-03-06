package com.mike_caron.mikesmodslib.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidBase extends Fluid
{
    //protected int mapColor = 0xFFFFFFFF;
    protected float overlayAlpha = 0.2F;
    protected SoundEvent emptySound = SoundEvents.ITEM_BUCKET_EMPTY;
    protected SoundEvent fillSound = SoundEvents.ITEM_BUCKET_FILL;
    protected Material material = Material.WATER;

    public FluidBase(String fluidName, ResourceLocation still, ResourceLocation flowing)
    {
        this(fluidName, still, flowing, true);
    }

    public FluidBase(String fluidName, ResourceLocation still, ResourceLocation flowing, boolean autoRegister)
    {
        super(fluidName, still, flowing);

        this
            .setGaseous(false);

        if(autoRegister)
        {
            FluidRegistry.registerFluid(this);
            FluidRegistry.addBucketForFluid(this);
        }
    }

    /*
    public FluidBase(String fluidName, ResourceLocation still, ResourceLocation flowing, int mapColor)
    {
        this(fluidName, still, flowing);
        setColor(mapColor);
    }

    public FluidBase(String fluidName, ResourceLocation still, ResourceLocation flowing, int mapColor, float overlayAlpha)
    {
        this(fluidName, still, flowing, mapColor);
        setAlpha(overlayAlpha);
    }
    */

    @Override
    public int getColor()
    {
        return color;
    }

    public FluidBase setColor(int parColor)
    {
        color = parColor;
        return this;
    }

    public float getAlpha()
    {
        return overlayAlpha;
    }

    public FluidBase setAlpha(float parOverlayAlpha)
    {
        overlayAlpha = parOverlayAlpha;
        return this;
    }

    @Override
    public FluidBase setEmptySound(SoundEvent parSound)
    {
        emptySound = parSound;
        return this;
    }

    @Override
    public SoundEvent getEmptySound()
    {
        return emptySound;
    }

    @Override
    public FluidBase setFillSound(SoundEvent parSound)
    {
        fillSound = parSound;
        return this;
    }

    @Override
    public SoundEvent getFillSound()
    {
        return fillSound;
    }

    public FluidBase setMaterial(Material parMaterial)
    {
        material = parMaterial;
        return this;
    }

    public Material getMaterial()
    {
        return material;
    }

    @Override
    public boolean doesVaporize(FluidStack fluidStack)
    {
        if (block == null)
            return false;
        return block.getDefaultState().getMaterial() == getMaterial();
    }
}
