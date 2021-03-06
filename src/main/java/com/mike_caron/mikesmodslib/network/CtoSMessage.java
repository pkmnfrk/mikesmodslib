package com.mike_caron.mikesmodslib.network;

import com.mike_caron.mikesmodslib.Mod;
import com.mike_caron.mikesmodslib.block.TileEntityBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CtoSMessage implements IMessage
{
    private int dim;
    private BlockPos pos;
    private KindEnum kind;
    private int guiElement;
    private String theString;
    private boolean theBoolean;
    private String extraData;

    // add message-specific fields here

    public CtoSMessage() {}

    public static CtoSMessage forGuiButton(int dim, BlockPos pos, int guiElement, String extraData)
    {
        CtoSMessage ret = new CtoSMessage();
        ret.kind = KindEnum.GuiButton;
        ret.dim = dim;
        ret.pos = pos;
        ret.guiElement = guiElement;
        ret.extraData = extraData;
        return ret;
    }

    public static CtoSMessage forGuiButton(int dim, BlockPos pos, int guiElement)
    {
        return forGuiButton(dim, pos, guiElement, null);
    }

    public static CtoSMessage forGuiString(int dim, BlockPos pos, int guiElement, String string)
    {
        CtoSMessage ret = new CtoSMessage();
        ret.kind = KindEnum.GuiString;
        ret.dim = dim;
        ret.pos = pos;
        ret.guiElement = guiElement;
        ret.theString = string;
        return ret;
    }

    public static CtoSMessage forGuiToggle(int dim, BlockPos pos, int guiElement, boolean bool)
    {
        CtoSMessage ret = new CtoSMessage();
        ret.kind = KindEnum.GuiToggle;
        ret.dim = dim;
        ret.pos = pos;
        ret.guiElement = guiElement;
        ret.theBoolean = bool;
        return ret;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int o = buf.readInt();
        kind = KindEnum.values()[o];
        dim = buf.readInt();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        switch(kind)
        {
            case GuiButton:
                guiElement = buf.readInt();
                extraData = readString(buf);
                break;
            case GuiString:
                guiElement = buf.readInt();
                theString = readString(buf);
                break;
            case GuiToggle:
                guiElement = buf.readInt();
                theBoolean = buf.readBoolean();
                break;
            default:
                throw new RuntimeException("What the? What kind of kind is " + kind);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(kind.ordinal());
        buf.writeInt(dim);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        switch(kind)
        {
            case GuiButton:
                buf.writeInt(guiElement);
                writeString(buf, extraData);
                break;
            case GuiString:
                buf.writeInt(guiElement);
                writeString(buf, theString);
                break;
            case GuiToggle:
                buf.writeInt(guiElement);
                buf.writeBoolean(theBoolean);
                break;
            default:
                throw new RuntimeException("What the? What kind of kind is " + kind);
        }
    }

    public BlockPos getPos() { return pos; }

    public int getGuiElement() { return guiElement; }

    public String getString() { return theString; }

    public KindEnum getKind() { return kind; }

    public boolean getBoolean() { return theBoolean; }

    public String getExtraData() { return extraData; }

    public enum KindEnum {
        Unknown,
        GuiButton,
        GuiString,
        GuiToggle
    }

    private static String readString(ByteBuf byteBuf)
    {
        return ByteBufUtils.readUTF8String(byteBuf);
    }

    private static void writeString(ByteBuf byteBuf, String string)
    {
        if(string == null)
        {
            string = "";
        }

        ByteBufUtils.writeUTF8String(byteBuf, string);
    }

    public static class Handler implements IMessageHandler<CtoSMessage, IMessage>
    {
        @Override
        public IMessage onMessage(final CtoSMessage message, MessageContext ctx)
        {

            final EntityPlayerMP player = ctx.getServerHandler().player;

            final WorldServer world = DimensionManager.getWorld(message.dim);

            ((IThreadListener) world).addScheduledTask(() -> {
                try {
                    if(world.isBlockLoaded(message.getPos()))
                    {
                        TileEntity te = world.getTileEntity(message.getPos());

                        if(!(te instanceof TileEntityBase) || !((TileEntityBase) te).canInteractWith(player))
                        {
                            return;
                        }

                        switch (message.getKind())
                        {
                            case GuiButton:
                            {
                                TileEntityBase teb = (TileEntityBase) te;
                                teb.handleGuiButton(player, message.getGuiElement(), message.getExtraData());
                            }
                            break;
                            case GuiString:
                            {
                                TileEntityBase teb = (TileEntityBase) te;
                                teb.handleGuiString(player, message.getGuiElement(), message.getString());
                            }
                            case GuiToggle:
                            {
                                TileEntityBase teb = (TileEntityBase) te;
                                teb.handleGuiToggle(player, message.getGuiElement(), message.getBoolean());
                            }

                            break;
                        }
                    }

                }
                catch (Exception e)
                {
                    Mod.logger.error("Error while handling message", e);
                }
            });

            return null;
        }
    }
}
