package me.ichun.mods.blocksteps.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityWaypoint extends Entity
{
    public EntityWaypoint(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)
    {
        return false;
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void setCurrentItemOrArmor(int i, ItemStack itemstack) {
    }

    @Override
    public ItemStack[] getInventory()
    {
        return new ItemStack[0];
    }
}
