package com.mike_caron.mikesmodslib.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

public class TileEntityBase extends TileEntity
{
    public TileEntityBase()
    {

    }

    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return !isInvalid()
            && entityPlayer.dimension == world.provider.getDimension()
            && entityPlayer.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D //8 blocks
        ;
    }

    public void handleGuiButton(EntityPlayerMP player, int button,  String extraData) {}

    public void handleGuiString(EntityPlayerMP player, int element, String string) {}

    public void handleGuiToggle(EntityPlayerMP player, int element, boolean bool) {}

    @Nonnull
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(getPos(), 1, nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(super.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();

        if(world.isRemote) return;

        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        if(world.isRemote) return;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEvent(WorldEvent.Unload event)
    {
        if(event.getWorld() == world)
            MinecraftForge.EVENT_BUS.unregister(this);
    }

    protected void markAndNotify()
    {
        IBlockState state = this.world.getBlockState(getPos());
        markAndNotify(state, state);
    }

    protected void markAndNotify(IBlockState oldState, IBlockState newState)
    {
        this.world.markAndNotifyBlock(getPos(), null, oldState, newState, 3);
    }
}
