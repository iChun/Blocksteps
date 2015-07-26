package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.api.BlockPeripheralHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ObsidianHandler extends BlockPeripheralHandler
{
    @Override
    public boolean isValidPeripheral(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        return Blocksteps.config.endTowerDetection == 1 && world instanceof World && ((World)world).provider.getDimensionId() == 1;
    }

    @Override
    public List<BlockPos> getRelativeBlocks(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        ArrayList<BlockPos> poses = new ArrayList<BlockPos>();

        if(world instanceof World && ((World)world).provider.getDimensionId() == 1)
        {
            for(int j = 0; j <= 100; j++)
            {
                for(int i = -7; i <= 7; i++)
                {
                    for(int k = -7; k <= 7; k++)
                    {
                        BlockPos newPos = pos.add(i, j, k);
                        if(world.getBlockState(newPos).getBlock() == Blocks.obsidian || world.getBlockState(newPos).getBlock() == Blocks.bedrock || world.getBlockState(newPos).getBlock() == Blocks.fire)
                        {
                            poses.add(newPos);
                        }
                    }
                }
            }
        }
        return poses;
    }

    @Override
    public boolean isAlsoSolidBlock()
    {
        return true;
    }

    @Override
    public boolean requireThread()
    {
        return true;
    }
}
