package me.ichun.mods.blocksteps.common.thread;

import me.ichun.mods.blocksteps.common.blockaid.CheckBlockInfo;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadCheckBlocks extends Thread
{
    public final List<CheckBlockInfo> checks = Collections.synchronizedList(new ArrayList<CheckBlockInfo>());
    public ArrayList<CheckBlockInfo> checksList = new ArrayList<>();

    public ThreadCheckBlocks()
    {
        this.setName("Blocksteps Block Checker Thread");
        this.setDaemon(true);
    }

    @Override
    public void run()
    {
        try
        {
            while(true)
            {
                synchronized(checks)
                {
                    if(!checks.isEmpty())
                    {
                        checksList.addAll(checks);
                        checks.clear();
                    }
                }
                if(!checksList.isEmpty())
                {
                    CheckBlockInfo info = checksList.get(0);
                    if(info.world == Minecraft.getMinecraft().theWorld)
                    {
                        info.doCheck();
                    }
                    checksList.remove(0);
                    Thread.sleep(2L);
                }
                else
                {
                    Thread.sleep(50L);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
