package me.ichun.mods.blocksteps.common.gui;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.gui.window.WindowEditWaypoint;
import me.ichun.mods.blocksteps.common.gui.window.WindowWaypoints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;

import java.util.ArrayList;

public class GuiWaypoints extends IWorkspace
{
    public WindowWaypoints windowWaypoints;
    public WindowEditWaypoint windowEditWaypoint;

    public GuiWaypoints()
    {
        VARIABLE_LEVEL = 0;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        levels.clear();

        ArrayList<Window> level = new ArrayList<Window>();

        windowWaypoints = new WindowWaypoints(this, 10, 10, 105, height - 20);
        windowEditWaypoint = new WindowEditWaypoint(this, 115, 10, width - 125, height - 20);

        level.add(windowWaypoints);
        level.add(windowEditWaypoint);

        levels.add(level);
        levels.add(new ArrayList<Window>());
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        Blocksteps.eventHandler.cleanWaypoints();
    }

    @Override
    public boolean canClickOnElement(Window window, Element element)
    {
        return true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderTick)
    {
        if(mc == null)
        {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0.0D, -5000.0D, 5000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();

        GlStateManager.pushMatrix();

        drawDefaultBackground();

        boolean onWindow = drawWindows(mouseX, mouseY);

        int scroll = Mouse.getDWheel();

        updateElementHovered(mouseX, mouseY, scroll);

        GlStateManager.popMatrix();

        updateKeyStates();

        updateWindowDragged(mouseX, mouseY);

        updateElementDragged(mouseX, mouseY);

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
    }

    @Override
    public void keyTyped(char c, int key)
    {
        if (key == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
        else if(elementSelected != null)
        {
            elementSelected.keyInput(c, key);
            if(elementSelected.parent == windowEditWaypoint)
            {
                windowEditWaypoint.keyInput(elementSelected);
            }
        }
    }
}
