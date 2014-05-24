package blocksteps.common.block;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class BlockInfo
{
    public final Block block;
    public final int x;
    public final int y;
    public final int z;
    public final boolean isPeriph;

    public int metadata;
    public int lightLevel;
    public TileEntity tileEntity;

    public BlockInfo periph;

    public BlockInfo(Block blk, int i, int j, int k, boolean perp)
    {
        block = blk;
        x = i;
        y = j;
        z = k;
        isPeriph = perp;
    }

    public boolean sameType(BlockInfo info)
    {
        return info.block == block && info.x == x && info.y == y && info.z == z && info.isPeriph == isPeriph;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof BlockInfo)
        {
            BlockInfo info = (BlockInfo)obj;
            return sameType(info) && (info.periph == null && periph == null || info.periph != null && info.periph.equals(periph));
        }
        return false;
    }

    @Override
    public String toString()
    {
        return String.format("%s[%d, %d, %d]", block.getClass().getSimpleName(), x, y, z);
    }

}
