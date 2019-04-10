package com.mike_caron.mikesmodslib.util;

import com.mike_caron.mikesmodslib.block.BlockBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.function.BiFunction;

public class MultiblockUtil
{
    private MultiblockUtil(){}


    public static BlockPos walkMultiblock(@Nonnull World world, @Nonnull BlockPos start, Class<? extends BlockBase> medium, BiFunction<IBlockState, BlockPos, Boolean> shouldStop)
    {
        return walkMultiblock(world, start, (blockState, pos) -> medium.isInstance(blockState.getBlock()), shouldStop);
    }

    public static BlockPos walkMultiblock(@Nonnull World world, @Nonnull BlockPos start, BiFunction<IBlockState, BlockPos, Boolean> shouldEnter, BiFunction<IBlockState, BlockPos, Boolean> shouldStop)
    {
        HashSet<Long> explored = new HashSet<>();
        PriorityQueue<DistPos> toExplore = new PriorityQueue<>(DistPos::compare);

        toExplore.add(new DistPos(start, 0));

        while(!toExplore.isEmpty())
        {
            DistPos spot = toExplore.remove();

            IBlockState block = world.getBlockState(spot);

            if(!shouldEnter.apply(block, spot))
                continue;

            if(shouldStop.apply(block, spot))
                return spot;

            for(EnumFacing dir : EnumFacing.VALUES)
            {
                DistPos newPos = spot.offset(dir);
                if(!explored.contains(newPos.toLong()))
                {
                    explored.add(newPos.toLong());
                    toExplore.add(spot.offset(dir));
                }
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
