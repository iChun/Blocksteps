package me.ichun.mods.blocksteps.common.core;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.RandomStringUtils;
import us.ichun.mods.ichunutil.client.gui.window.element.IIdentifiable;
import us.ichun.mods.ichunutil.client.gui.window.element.IListable;
import us.ichun.mods.ichunutil.common.core.config.types.Colour;
import us.ichun.mods.ichunutil.common.module.tabula.common.project.ProjectInfo;

import java.util.Random;

public class Waypoint
        implements Comparable, IIdentifiable, IListable
{
    @SerializedName("i")
    public String ident;
    @SerializedName("n")
    public String name;
    @SerializedName("p")
    public BlockPos pos;
    @SerializedName("c")
    public int colour;
    @SerializedName("v")
    public boolean visible;
    @SerializedName("s")
    public boolean showDistance;
    @SerializedName("b")
    public boolean beam;
    @SerializedName("e")
    public String entityType;

    public transient Entity entityInstance;

    public Waypoint(BlockPos wpPos)
    {
        ident = RandomStringUtils.randomAscii(ProjectInfo.IDENTIFIER_LENGTH);
        name = "New Waypoint";
        pos = wpPos;
        int r = 255;
        int g = 255;
        int b = 255;
        if(Minecraft.getMinecraft().theWorld != null)
        {
            Random rand = Minecraft.getMinecraft().theWorld.rand;
            r = rand.nextInt(256);
            g = rand.nextInt(256);
            b = rand.nextInt(256);
        }
        colour = (r << 16) + (g << 8) + (b);
        visible = true;
        showDistance = true;
        beam = true;
        entityType = "";
    }

    public Waypoint setName(String name)
    {
        this.name = name;
        return this;
    }

    @Override
    public int compareTo(Object arg0)
    {
        if(arg0 instanceof Waypoint)
        {
            Waypoint comp = (Waypoint)arg0;
            return name.compareTo(comp.name);
        }
        return 0;
    }

    @Override
    public String getIdentifier()
    {
        return ident;
    }

    @Override
    public String getName()
    {
        return "   " + name;
    }
}
