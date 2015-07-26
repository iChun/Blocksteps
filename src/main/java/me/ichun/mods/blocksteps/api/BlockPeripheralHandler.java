package me.ichun.mods.blocksteps.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * BlockPeripheralHandler class
 * This class is used to identify blocks as "peripherals" for Blocksteps, and to get their respective blocks as well
 * Look at the source of blocksteps for classes that extends this class to get a general idea on how to use it.
 *
 * @author iChun
 */
public abstract class BlockPeripheralHandler
{
    /**
     * Checks the block to see if it is a valid peripheral block
     * @param world The world object.
     * @param pos The position of this block peripheral.
     * @param state The block state of this block peripheral.
     * @param availableBlocks Available "solid" blocks surrounding said peripheral. Used by levers and torches to see if the block it's attached to is in the list or not.
     * @return block is a valid peripheral
     */
    public boolean isValidPeripheral(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        return !getRelativeBlocks(world, pos, state, availableBlocks).isEmpty();
    }

    /**
     * Gets the list of relative peripheral blocks to be rendered by Blocksteps.
     * @param world The world object.
     * @param pos The position of this block peripheral.
     * @param state The block state of this block peripheral.
     * @param availableBlocks Available "solid" blocks surrounding said peripheral. Used by levers and torches to see if the block it's attached to is in the list or not.
     * @return Returns the list of peripheral blocks in relation to this handler.
     */
    public List<BlockPos> getRelativeBlocks(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        ArrayList<BlockPos> poses = new ArrayList<BlockPos>();
        poses.add(pos);
        return poses;
    }

    /**
     * If the peripheral is also a solid block. Used by handlers for logs and obsidian, etc.
     * @return block is also a solid block
     */
    public boolean isAlsoSolidBlock()
    {
        return false;
    }

    /**
     * For resource-heavy checks/calls, this makes a thread do the checks instead of the main Minecraft thread, to reduce lag
     * @return to use thread or not.
     */
    public boolean requireThread()
    {
        return false;
    }

    /**
     * Call this method to register your peripheral handler.
     * Why am I reflecting to do it? I'm lazy, that's why.
     * @param clz Class for handler
     * @param handler Actual handler
     */
    public static void registerBlockPeripheralHandler(Class<? extends Block> clz, BlockPeripheralHandler handler)
    {
        try
        {
            Class stepHandler = Class.forName("me.ichun.mods.blocksteps.common.blockaid.BlockStepHandler");
            ((HashMap)ObfuscationReflectionHelper.getPrivateValue(stepHandler, null, "blockPeripheralRegistry")).put(clz, handler);
        }
        catch(Exception e)
        {
            System.out.println("[Blocksteps] Error registering block peripheral handler");
            e.printStackTrace();
        }
    }
}
