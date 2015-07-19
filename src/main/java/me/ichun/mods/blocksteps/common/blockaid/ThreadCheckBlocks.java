package me.ichun.mods.blocksteps.common.blockaid;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadCheckBlocks extends Thread
{
    public final List<CheckBlockInfo> checks = Collections.synchronizedList(new ArrayList<CheckBlockInfo>());

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
            boolean execute = true;
            while(execute)
            {
                synchronized(checks)
                {
                    if(!checks.isEmpty())
                    {
                        CheckBlockInfo info = checks.get(0);
                        if(info.world == Minecraft.getMinecraft().theWorld)
                        {
                            info.doCheck();
                        }
                        checks.remove(0);
                    }
                }
                Thread.sleep(50L);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
