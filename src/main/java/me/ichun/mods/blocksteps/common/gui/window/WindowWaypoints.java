package me.ichun.mods.blocksteps.common.gui.window;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.core.Waypoint;
import me.ichun.mods.blocksteps.common.gui.GuiWaypoints;
import me.ichun.mods.blocksteps.common.gui.window.element.ElementWaypointList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import us.ichun.mods.ichunutil.client.gui.Theme;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.client.gui.window.element.ElementButtonTextured;
import us.ichun.mods.ichunutil.client.gui.window.element.ElementListTree;
import us.ichun.mods.ichunutil.client.render.RendererHelper;

import java.util.ArrayList;
import java.util.Collections;

public class WindowWaypoints extends Window
{
    public GuiWaypoints parent;

    public ElementWaypointList list;

    public static final int ID_DONE = -1;
    public static final int ID_NEW = 1;
    public static final int ID_DEL = 2;
    public static final int ID_DEL_MAP = 3;

    public WindowWaypoints(GuiWaypoints parent, int x, int y, int w, int h)
    {
        super(parent, x, y, w, h, 20, 20, "blocksteps.gui.waypoints", true);

        this.parent = parent;

        elements.add(new ElementButtonTextured(this, 5, parent.height - 40 - 5, ID_DONE, false, 0, 1, "gui.done", new ResourceLocation("blocksteps", "textures/icon/done.png")));
        elements.add(new ElementButtonTextured(this, 30, parent.height - 40 - 5, ID_NEW, false, 0, 1, "blocksteps.gui.newWaypoint", new ResourceLocation("blocksteps", "textures/icon/new.png")));
        elements.add(new ElementButtonTextured(this, 55, parent.height - 40 - 5, ID_DEL, false, 0, 1, "blocksteps.gui.deleteWaypoint", new ResourceLocation("blocksteps", "textures/icon/delete.png")));
        elements.add(new ElementButtonTextured(this, 80, parent.height - 40 - 5, ID_DEL_MAP, false, 0, 1, "blocksteps.gui.deleteMap", new ResourceLocation("blocksteps", "textures/icon/delMap.png")));

        list = new ElementWaypointList(this, 4, 14, 105 - 8, parent.height - 50 - 14, 0, false, false);
        elements.add(list);
    }

    @Override
    public void draw(int mouseX, int mouseY)
    {
        super.draw(mouseX, mouseY);
        RendererHelper.drawColourOnScreen(Theme.getAsHex(Theme.getInstance().windowBorder), 255, posX + BORDER_SIZE, posY + height - 25 - BORDER_SIZE, width - (BORDER_SIZE * 2), 1, 0);

        ArrayList<Waypoint> creations = new ArrayList<Waypoint>(Blocksteps.eventHandler.getWaypoints(Minecraft.getMinecraft().theWorld.provider.getDimensionId()));
        ArrayList<Waypoint> keepOut = new ArrayList<Waypoint>();
        for(int i = creations.size() - 1; i >= 0; i--)
        {
            if(creations.get(i).name.contains("New Waypoint") || creations.get(i).name.contains("Death Location"))
            {
                keepOut.add(creations.get(i));
                creations.remove(i);
            }
        }
        list.trees.clear();
        Collections.sort(creations);
        Collections.sort(keepOut);
        for(Waypoint wp : creations)
        {
            list.createTree(null, wp, 13, 0, false, false);
        }
        for(Waypoint wp : keepOut)
        {
            list.createTree(null, wp, 13, 0, false, false);
        }
        for(ElementListTree.Tree tree : list.trees)
        {
            if(list.selectedIdentifier.equals(((Waypoint)tree.attachedObject).getIdentifier()))
            {
                tree.selected = true;
            }
        }
    }

    @Override
    public void elementTriggered(Element element)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(element.id == ID_NEW)
        {
            if(mc.theWorld != null && mc.thePlayer != null)
            {
                ArrayList<Waypoint> waypoints = Blocksteps.eventHandler.getWaypoints(mc.theWorld.provider.getDimensionId());
                waypoints.add(new Waypoint(new BlockPos(mc.thePlayer)));
                list.selectedIdentifier = waypoints.get(waypoints.size() - 1).getIdentifier();
            }
        }
        else if(element.id == ID_DEL)
        {
            for(ElementListTree.Tree tree : list.trees)
            {
                if(tree.selected)
                {
                    workspace.addWindowOnTop(new WindowConfirmDelete(workspace, (Waypoint)tree.attachedObject).putInMiddleOfScreen());
                    break;
                }
            }
        }
        else if(element.id == ID_DEL_MAP)
        {
            workspace.addWindowOnTop(new WindowConfirmDelete(workspace, null).putInMiddleOfScreen());
        }
        else if(element.id == ID_DONE)
        {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }


    @Override
    public void resized()
    {
        posX = 10;
        posY = 10;
        width = 105;
        height = parent.height - 20;
        super.resized();
    }

    @Override
    public int clickedOnBorder(int mouseX, int mouseY, int id)//only left clicks
    {
        return 0;
    }

    @Override
    public boolean canBeDragged()
    {
        return false;
    }

    @Override
    public boolean canMinimize()
    {
        return false;
    }

    @Override
    public boolean isStatic()
    {
        return true;
    }
}
