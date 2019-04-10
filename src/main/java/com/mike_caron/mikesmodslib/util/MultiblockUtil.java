package com.mike_caron.mikesmodslib.util;

import com.mike_caron.mikesmodslib.block.BlockBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.function.Function;

public class MultiblockUtil
{
    private MultiblockUtil(){}

    public static BlockPos findClosest(@Nonnull World world, @Nonnull BlockPos start, Class<? extends BlockBase> medium, Function<IBlockState, Boolean> search)
    {
        HashSet<Long> explored = new HashSet<>();
        PriorityQueue<DistPos> toExplore = new PriorityQueue<>(DistPos::compare);

        toExplore.add(new DistPos(start, 0));

        while(!toExplore.isEmpty())
        {
            DistPos spot = toExplore.remove();

            if(explored.contains(spot.toLong()))
                continue;

            explored.add(spot.toLong());

            IBlockState block = world.getBlockState(spot);

            if(!medium.isInstance(block.getBlock()))
                continue;

            if(search.apply(block))
                return spot;

            for(EnumFacing dir : EnumFacing.VALUES)
            {
                toExplore.add(spot.offset(dir));
            }
        }

        return null;
    }

    private static class DistPos
        extends BlockPos
    {
        public final int dist;

        DistPos(BlockPos pos, int distance)
        {
            super(pos.getX(), pos.getY(), pos.getZ());

            this.dist = distance;
        }

        public static int compare(DistPos a, DistPos b)
        {
            int ret = b.dist - a.dist;

            if(ret != 0) return ret;

            return a.compareTo(b);
        }

        @Override
        @Nonnull
        public DistPos offset(@Nonnull EnumFacing facing)
        {
            return new DistPos(super.offset(facing), dist + 1);
        }
    }
}
