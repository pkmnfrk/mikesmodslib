package com.mike_caron.mikesmodslib.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class FacingBlockBase extends BlockBase
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public FacingBlockBase(Material material, String name)
    {
        super(material, name);

        setDefaultState(addStateProperties(this.blockState.getBaseState()));
    }

    @Override
    protected IBlockState addStateProperties(IBlockState blockState)
    {
        return super.addStateProperties(blockState).withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    protected void addAdditionalPropeties(List<IProperty<?>> properties)
    {
        super.addAdditionalPropeties(properties);

        properties.add(FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex() - 2;
    }

    public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entity)
    {
        return EnumFacing.getFacingFromVector(
                (float) entity.posX - clickedBlock.getX(),
                (float) entity.posY - clickedBlock.getY(),
                (float) entity.posZ - clickedBlock.getZ()
        );
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        //return super.rotateBlock(world, pos, axis);

        IBlockState state = world.getBlockState(pos);
        IProperty<EnumFacing> prop = FACING;

        EnumFacing facing = state.getValue(prop);

        IBlockState newState = state.withProperty(prop, facing.rotateY());

        world.setBlockState(pos, newState);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.byIndex((meta & 3) + 2))
        ;

    }
}
