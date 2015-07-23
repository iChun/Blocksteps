package me.ichun.mods.blocksteps.common.gui.window;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.core.Waypoint;
import me.ichun.mods.blocksteps.common.gui.GuiWaypoints;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import us.ichun.mods.ichunutil.client.gui.Theme;
import us.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.client.gui.window.element.ElementButton;

public class WindowConfirmDelete extends Window
{
    public Waypoint waypoint;

    public WindowConfirmDelete(IWorkspace parent, Waypoint waypoint)
    {
        super(parent, 0, 0, 300, 120, 300, 120, "blocksteps.gui.areYouSure", true);

        this.waypoint = waypoint;

        elements.add(new ElementButton(this, width - 140, height - 30, 60, 16, 3, false, 1, 1, "element.button.ok"));
        elements.add(new ElementButton(this, width - 70, height - 30, 60, 16, 0, false, 1, 1, "element.button.cancel"));
    }

    @Override
    public void draw(int mouseX, int mouseY)
    {
        super.draw(mouseX, mouseY);
        if(!minimized)
        {
            workspace.getFontRenderer().drawString(StatCollector.translateToLocal(waypoint == null ? "blocksteps.gui.confirmDeleteMap" : "blocksteps.gui.confirmDeleteWaypoint"), posX + 15, posY + 40, Theme.getAsHex(workspace.currentTheme.font), false);
        }
    }

    @Override
    public void elementTriggered(Element element)
    {
        if(element.id == 0)
        {
            workspace.removeWindow(this, true);
        }
        if(element.id == 3)
        {
            if(workspace.windowDragged == this)
            {
                workspace.windowDragged = null;
            }

            if(waypoint == null)
            {
                Blocksteps.eventHandler.getWaypoints(Minecraft.getMinecraft().theWorld.provider.getDimensionId()).clear();
                Blocksteps.eventHandler.getSteps(Minecraft.getMinecraft().theWorld.provider.getDimensionId()).clear();
                Blocksteps.eventHandler.repopulateBlocksToRender = Blocksteps.eventHandler.purgeRerender = true;
            }
            else
            {
                Blocksteps.eventHandler.getWaypoints(Minecraft.getMinecraft().theWorld.provider.getDimensionId()).remove(waypoint);
            }
            ((GuiWaypoints)workspace).windowWaypoints.list.selectedIdentifier = "";

            workspace.removeWindow(this, true);
        }
    }
}
