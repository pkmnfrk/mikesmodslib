package com.mike_caron.mikesmodslib.block;

import com.mike_caron.mikesmodslib.integrations.ITOPInfoProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockBase
    extends Block
    implements ITOPInfoProvider
{
    public BlockBase(Material material, String name)
    {
        super(material);
        setRegistryName(name);
        setTranslationKey(getRegistryName().toString());
    }

    protected IBlockState addStateProperties(IBlockState blockState)
    {
        return blockState;
    }

    @SuppressWarnings("ConstantConditions")
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if(!worldIn.isRemote)
        {

            NonNullList<ItemStack> drops = NonNullList.create();

            this.getExtraDrops(drops, worldIn, pos, state);

            drops.forEach(stack -> InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));

        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        return false;
    }

    protected void getExtraDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state)
    {
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        List<IProperty<?>> props = new ArrayList<>();
        addAdditionalPropeties(props);

        List<IUnlistedProperty<?>> unlistedProps = new ArrayList<>();
        addAdditionalUnlistedProperties(unlistedProps);

        if(unlistedProps.isEmpty())
        {
            return new BlockStateContainer(this, props.toArray(new IProperty<?>[0]));
        }
        else
        {
            return new ExtendedBlockState(this, props.toArray(new IProperty<?>[0]), unlistedProps.toArray(new IUnlistedProperty[0]));
        }
    }

    protected void addAdditionalPropeties(List<IProperty<?>> properties)
    {

    }
    protected void addAdditionalUnlistedProperties(List<IUnlistedProperty<?>> properties)
    {

    }

    @Nullable
    @SuppressWarnings({"unchecked"})
    protected <T extends TileEntityBase> T getTileEntity(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, @Nonnull Class<T> clss)
    {
        TileEntity ret = worldIn.getTileEntity(pos);

        if(clss.isInstance(ret))
            return (T)ret;

        return null;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        TileEntity te = world.getTileEntity(data.getPos());

        if(!(te instanceof ITOPInfoProvider))
            return;

        ((ITOPInfoProvider)te).addProbeInfo(mode, probeInfo, player, world, blockState, data);
    }

    @Override
    public boolean hasInfo(EntityPlayer player)
    {
        if(hasTileEntity())
            return true;
        return false;
    }
}
