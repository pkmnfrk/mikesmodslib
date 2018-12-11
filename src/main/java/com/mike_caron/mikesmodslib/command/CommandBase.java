package com.mike_caron.mikesmodslib.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CommandBase
    implements ICommand
{
    private final List<String> aliases = new ArrayList<>();
    private final Map<String, Tuple<TriAction<MinecraftServer, ICommandSender, String[]>, Boolean>> subCommands = new HashMap<>();

    public CommandBase()
    {
        aliases.add(getName());
    }

    protected void addAlias(@Nonnull String alias)
    {
        aliases.add(alias);
    }

    protected void addSubCommand(String cmd, TriAction<MinecraftServer, ICommandSender, String[]> action, boolean opOnly)
    {
        subCommands.put(cmd, new Tuple<>(action, opOnly));
    }

    @Override
    @Nonnull
    public final String getUsage(@Nonnull ICommandSender iCommandSender)
    {
        return "Usage: " + getName() + " " + String.join(" | ", subCommands.keySet());
    }

    @Override
    @Nonnull
    public final List<String> getAliases()
    {
        return aliases;
    }

    @Override
    public void execute(@Nullable MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if(args.length == 0 || !subCommands.containsKey(args[0]))
        {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }
        String[] newArgs = Arrays.stream(args).skip(1).toArray(String[]::new);
        Tuple<TriAction<MinecraftServer, ICommandSender, String[]>, Boolean> action = subCommands.get(args[0]);

        if(server != null && action.getSecond())
        {
            if(!sender.canUseCommand(2, ""))
            {
                throw new CommandException("You do not have permission to use that command");
            }
        }

        action.getFirst().execute(server, sender, newArgs);

    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer minecraftServer, @Nonnull ICommandSender iCommandSender)
    {
        return true;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer minecraftServer, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args, @Nullable BlockPos blockPos)
    {
        List<String> ret = new ArrayList<>();
        if(args.length == 1)
        {
            ret = subCommands.keySet().stream().filter( s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return ret;
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int i)
    {
        return false;
    }

    @Override
    public int compareTo(@Nonnull ICommand o)
    {
        return 0;
    }

    interface TriAction<X,Y,Z>
    {
        void execute(@Nullable X x, @Nonnull Y y, @Nonnull Z z)
            throws CommandException;
    }
}
