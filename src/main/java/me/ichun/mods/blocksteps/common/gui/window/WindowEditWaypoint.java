package me.ichun.mods.blocksteps.common.gui.window;

import com.google.common.base.Splitter;
import me.ichun.mods.blocksteps.common.core.Waypoint;
import me.ichun.mods.blocksteps.common.gui.GuiWaypoints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.ichunutil.client.gui.Theme;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.*;
import us.ichun.mods.ichunutil.client.render.RendererHelper;

import java.awt.*;
import java.nio.FloatBuffer;

public class WindowEditWaypoint extends Window
{
    public GuiWaypoints parent;

    public Waypoint selectedWaypoint;

    public ElementTextInput elementName;
    public ElementNumberInput elementPosition;
    public ElementToggle elementVisible;
    public ElementToggle elementShowDistance;
    public ElementSelector elementEntityType;
    public ElementTextInput elementColour;

    public int colourHue;

    public WindowEditWaypoint(GuiWaypoints parent, int x, int y, int w, int h)
    {
        super(parent, x, y, w, h, 20, 20, "blocksteps.gui.editWaypoint", true);

        this.parent = parent;

        elementName = new ElementTextInput(this, 10, 30, width - 20, 12, 0, "blocksteps.waypoint.name");
        elements.add(elementName);

        elementPosition = new ElementNumberInput(this, 10, 60, width - 20, 12, 1, "blocksteps.waypoint.pos", 3, false);
        elements.add(elementPosition);

        elementVisible = new ElementToggle(this, 10, 80, 100, 12, 2, false, 0, 0, "blocksteps.waypoint.visible", "blocksteps.waypoint.visible", false);
        elements.add(elementVisible);

        elementShowDistance = new ElementToggle(this, 120, 80, 100, 12, 3, false, 0, 0, "blocksteps.waypoint.showDistance", "blocksteps.waypoint.showDistance", false);
        elements.add(elementShowDistance);

        elementEntityType = new ElementSelector(this, 10, 110, width - 20, 12, 4, "blocksteps.waypoint.entityType", "Waypoint");
        for(Object o : EntityList.stringToClassMapping.values())
        {
            Class<? extends Entity> clz = (Class)o;
            if(EntityLivingBase.class.isAssignableFrom(clz) && Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(clz) instanceof RendererLivingEntity)
            {
                elementEntityType.choices.put(clz.getSimpleName(), clz);
            }
        }
        elementEntityType.choices.put(EntityPlayer.class.getSimpleName(), EntityPlayer.class);
        elementEntityType.choices.put("Waypoint", EntityArrow.class);//TODO set the waypoint entityrenderer

        elements.add(elementEntityType);

        elementColour = new ElementTextInput(this, 0, 0, 50, 12, 0, "blocksteps.waypoint.colour", 6);
        elements.add(elementColour);
    }

    @Override
    public void draw(int mouseX, int mouseY)
    {
        Waypoint oldWaypoint = selectedWaypoint;
        selectedWaypoint = null;
        for(ElementListTree.Tree tree : parent.windowWaypoints.list.trees)
        {
            if(tree.selected)
            {
                selectedWaypoint = (Waypoint)tree.attachedObject;
                if(oldWaypoint != selectedWaypoint)
                {
                    elementName.textField.setText(selectedWaypoint.name);
                    elementPosition.textFields.get(0).setText(Integer.toString(selectedWaypoint.pos.getX()));
                    elementPosition.textFields.get(1).setText(Integer.toString(selectedWaypoint.pos.getY()));
                    elementPosition.textFields.get(2).setText(Integer.toString(selectedWaypoint.pos.getZ()));
                    elementVisible.toggledState = selectedWaypoint.visible;
                    elementShowDistance.toggledState = selectedWaypoint.showDistance;
                    elementEntityType.selected = selectedWaypoint.entityType.isEmpty() ? "Waypoint" : Splitter.on(".").splitToList(selectedWaypoint.entityType).get(Splitter.on(".").splitToList(selectedWaypoint.entityType).size() - 1);

                    float[] hsb = Color.RGBtoHSB(selectedWaypoint.colour >> 16 & 0xff, selectedWaypoint.colour >> 8 & 0xff, selectedWaypoint.colour & 0xff, null);
                    hsb[1] = hsb[2] = 1F;
                    colourHue = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                    String clr = Integer.toHexString(selectedWaypoint.colour);
                    while(clr.length() < 6)
                    {
                        clr = "0" + clr;
                    }
                    elementColour.textField.setText(clr);
                }
                break;
            }
        }
        if(selectedWaypoint != null)
        {
            elementColour.width = 60 - 7;

            super.draw(mouseX, mouseY);
            workspace.getFontRenderer().drawString(StatCollector.translateToLocal("blocksteps.waypoint.name"), posX + 11, posY + 20, Theme.getAsHex(workspace.currentTheme.font), false);
            workspace.getFontRenderer().drawString(StatCollector.translateToLocal("blocksteps.waypoint.pos"), posX + 11, posY + 50, Theme.getAsHex(workspace.currentTheme.font), false);
            workspace.getFontRenderer().drawString(StatCollector.translateToLocal("blocksteps.waypoint.entityType"), posX + 11, posY + 100, Theme.getAsHex(workspace.currentTheme.font), false);

            int x = posX + 11;
            int y = posY + height - 12;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0D);
            GlStateManager.rotate(-90F, 0F, 0F, 1F);
            GlStateManager.translate(-(x), -(y), 0D);
            RendererHelper.endGlScissor();
            workspace.getFontRenderer().drawString(StatCollector.translateToLocal("blocksteps.waypoint.colour"), x, y, Theme.getAsHex(workspace.currentTheme.font), false);
            GlStateManager.popMatrix();
            int size = height - 140;
            RendererHelper.drawGradientOnScreen(0xff000000, 0xff000000, 0xffffffff, 0xff000000 | colourHue, posX + 20, posY + height - size - 11, size, size, 0D);

            RendererHelper.drawHueStripOnScreen(255, posX + 20 + size + 3, posY + height - size - 11, 10, size, 0D);

            workspace.getFontRenderer().drawString("#", posX + 20 + size + 16, posY + height - 20, Theme.getAsHex(workspace.currentTheme.font), false);
            RendererHelper.drawColourOnScreen(selectedWaypoint.colour, 255, posX + 20 + size + 16, posY + height - 23 - 12 - 3, 60, 12, 0D);

            if(Mouse.isButtonDown(0))
            {
                if(mouseX >= 20 && mouseY >= height - size - 10 && mouseX < 20 + size && mouseY < height - 11)
                {
                    //is clicking on colour area
                    FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
                    GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
                    float r = buffer.get();
                    float g = buffer.get();
                    float b = buffer.get();
                    selectedWaypoint.colour = ((int)(r * 255F) << 16) + ((int)(g * 255F) << 8) + ((int)(b * 255F));

                    String clr = Integer.toHexString(selectedWaypoint.colour);
                    while(clr.length() < 6)
                    {
                        clr = "0" + clr;
                    }
                    elementColour.textField.setText(clr);
                }
                else if(mouseX >= 20 + size + 3 && mouseY >= height - size - 10 && mouseX < 20 + size + 3 + 10 && mouseY < height - 11)
                {
                    // is clicking on hue strip
                    FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
                    GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
                    float r = buffer.get();
                    float g = buffer.get();
                    float b = buffer.get();
                    float[] hsb = Color.RGBtoHSB((int)(r * 255F) & 0xff, (int)(g * 255F) & 0xff, (int)(b * 255F) & 0xff, null);
                    hsb[1] = hsb[2] = 1F;
                    colourHue = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                }
            }

            if(!(selectedWaypoint.pos.getX() == Integer.parseInt(elementPosition.textFields.get(0).getText()) && selectedWaypoint.pos.getY() == Integer.parseInt(elementPosition.textFields.get(1).getText()) && selectedWaypoint.pos.getZ() == Integer.parseInt(elementPosition.textFields.get(2).getText())))
            {
                selectedWaypoint.pos = new BlockPos(Integer.parseInt(elementPosition.textFields.get(0).getText()), Integer.parseInt(elementPosition.textFields.get(1).getText()), Integer.parseInt(elementPosition.textFields.get(2).getText()));
            }
            if(selectedWaypoint.entityType.isEmpty() && !elementEntityType.selected.equals("Waypoint") || !elementEntityType.selected.equals(Splitter.on(".").splitToList(selectedWaypoint.entityType).get(Splitter.on(".").splitToList(selectedWaypoint.entityType).size() - 1)))
            {
                keyInput(elementEntityType);
            }
        }
    }

    public void keyInput(Element e)
    {
        if(selectedWaypoint != null)
        {
            if(e == elementName)
            {
                selectedWaypoint.name = elementName.textField.getText();
            }
            else if(e == elementVisible)
            {
                selectedWaypoint.visible = elementVisible.toggledState;
            }
            else if(e == elementShowDistance)
            {
                selectedWaypoint.showDistance = elementShowDistance.toggledState;
            }
            else if(e == elementEntityType)
            {
                selectedWaypoint.entityType = elementEntityType.selected.equals("Waypoint") ? "" : ((Class)elementEntityType.choices.get(elementEntityType.selected)).getName();
            }
            else if(e == elementColour)
            {
                try
                {
                    int i = Integer.decode("#" + elementColour.textField.getText());

                    float[] hsb = Color.RGBtoHSB(i >> 16 & 0xff, i >> 8 & 0xff, i & 0xff, null);
                    hsb[1] = hsb[2] = 1F;
                    colourHue = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                    selectedWaypoint.colour = i;
                }
                catch(NumberFormatException ignored){}
            }
        }
    }

    @Override
    public void elementTriggered(Element e)
    {
        keyInput(e);
    }

    @Override
    public void resized()
    {
        posX = 115;
        posY = 10;
        width = parent.width - 125;
        height = parent.height - 20;

        int size = height - 140;
        elementColour.posX = 20 + size + 16 + 7;
        elementColour.posY = height - 23;
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
