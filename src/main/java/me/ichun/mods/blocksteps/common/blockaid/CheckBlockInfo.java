package me.ichun.mods.blocksteps.common.blockaid;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.api.BlockPeripheralHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class CheckBlockInfo
{
    public final World world;
    public final BlockPos oriPos;
    public final BlockPos pos;
    public final IBlockState state;
    public final BlockPeripheralHandler handler;
    public final List<BlockPos> availableBlocks;
    public List<BlockPos> blocksToRender;

    public CheckBlockInfo(World world, BlockPos oriPos, BlockPos pos, IBlockState state, BlockPeripheralHandler handler, List<BlockPos> availableBlocks)
    {
        this.world = world;
        this.oriPos = oriPos;
        this.pos = pos;
        this.state = state;
        this.handler = handler;
        this.availableBlocks = availableBlocks;
    }

    public void doCheck()
    {
        blocksToRender = handler.getRelativeBlocks(world, pos, state, availableBlocks);
        synchronized(Blocksteps.eventHandler.blocksToAdd)
        {
            Blocksteps.eventHandler.blocksToAdd.add(this);
        }
    }
}
