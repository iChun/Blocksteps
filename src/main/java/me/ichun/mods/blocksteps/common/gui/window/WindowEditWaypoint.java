package me.ichun.mods.blocksteps.common.gui.window;

import com.google.common.base.Splitter;
import me.ichun.mods.blocksteps.common.core.Waypoint;
import me.ichun.mods.blocksteps.common.entity.EntityWaypoint;
import me.ichun.mods.blocksteps.common.gui.GuiWaypoints;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.*;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

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
    public ElementToggle elementBeam;
    public ElementSelector elementEntityType;
    public ElementTextInput elementColour;
    public ElementNumberInput elementRenderRange;

    public static final int ID_CURRENT_POS = 100;

    public int colourHue;
    public Entity entInstance;

    public WindowEditWaypoint(GuiWaypoints parent, int x, int y, int w, int h)
    {
        super(parent, x, y, w, h, 20, 20, "blocksteps.gui.editWaypoint", true);

        this.parent = parent;

        elementName = new ElementTextInput(this, 10, 30, width - 20, 12, 0, "blocksteps.waypoint.name");
        elements.add(elementName);

        elementPosition = new ElementNumberInput(this, 10, 60, width - 20, 12, 1, "blocksteps.waypoint.pos", 3, false);
        elements.add(elementPosition);

        elementVisible = new ElementToggle(this, 10, 80, 90, 12, 2, false, 0, 0, "blocksteps.waypoint.visible", "blocksteps.waypoint.visible", false);
        elements.add(elementVisible);

        elementShowDistance = new ElementToggle(this, 105, 80, 90, 12, 3, false, 0, 0, "blocksteps.waypoint.showDistance", "blocksteps.waypoint.showDistance", false);
        elements.add(elementShowDistance);

        elementBeam = new ElementToggle(this, 200, 80, 90, 12, 3, false, 0, 0, "blocksteps.waypoint.beam", "blocksteps.waypoint.beam", false);
        elements.add(elementBeam);

        elementEntityType = new ElementSelector(this, 10, 110, width - 20, 12, 4, "blocksteps.waypoint.entityType", "Waypoint");
        for(Object o : EntityList.NAME_TO_CLASS.values())
        {
            Class<? extends Entity> clz = (Class)o;
            if(!(EntityPainting.class.isAssignableFrom(clz) || EntityItem.class.isAssignableFrom(clz)))
            {
                elementEntityType.choices.put(clz.getSimpleName(), clz);
            }
        }
        elementEntityType.choices.put(EntityPlayer.class.getSimpleName(), EntityPlayer.class);
        elementEntityType.choices.put("Waypoint", EntityArrow.class);//TODO set the waypoint entityrenderer

        elements.add(elementEntityType);

        elementRenderRange = new ElementNumberInput(this, parent.width - 70, 140, 80, 12, 1, "blocksteps.waypoint.renderRangeTooltip", 1, false, 0, Integer.MAX_VALUE) {
            @Override
            public void resized()
            {
                posX = parent.width - width - 10;
                for(int i = 0; i < textFields.size(); i++)
                {
                    textFields.get(i).xPosition = parent.posX + posX + 2 + ((width / textFields.size()) * i);
                    textFields.get(i).yPosition = parent.posY + posY + 2;
                    textFields.get(i).width = (width / textFields.size()) - 18;
                    textFields.get(i).setCursorPositionZero();
                }
            }
        };
        elements.add(elementRenderRange);

        elementColour = new ElementTextInput(this, 0, 0, 50, 12, 0, "blocksteps.waypoint.colour", 6);
        elements.add(elementColour);

        elements.add(new ElementButtonTooltip(this, 12 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(I18n.translateToLocal("blocksteps.waypoint.pos")), 49, 9, 9, ID_CURRENT_POS, false, 0, 0, "X", "blocksteps.waypoint.currentPos"));
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
                    elementBeam.toggledState = selectedWaypoint.beam;
                    elementEntityType.selected = selectedWaypoint.entityType.isEmpty() ? "Waypoint" : Splitter.on(".").splitToList(selectedWaypoint.entityType).get(Splitter.on(".").splitToList(selectedWaypoint.entityType).size() - 1);
                    createEntityInstance(selectedWaypoint.entityType);

                    float[] hsb = Color.RGBtoHSB(selectedWaypoint.colour >> 16 & 0xff, selectedWaypoint.colour >> 8 & 0xff, selectedWaypoint.colour & 0xff, null);
                    hsb[1] = hsb[2] = 1F;
                    colourHue = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                    String clr = Integer.toHexString(selectedWaypoint.colour);
                    while(clr.length() < 6)
                    {
                        clr = "0" + clr;
                    }
                    elementColour.textField.setText(clr);
                    elementRenderRange.textFields.get(0).setText(Integer.toString(selectedWaypoint.renderRange));
                }
                break;
            }
        }
        if(selectedWaypoint != null)
        {
            elementColour.width = 60 - 7;

            super.draw(mouseX, mouseY);
            workspace.getFontRenderer().drawString(I18n.translateToLocal("blocksteps.waypoint.name"), posX + 11, posY + 20, Theme.getAsHex(workspace.currentTheme.font), false);
            workspace.getFontRenderer().drawString(I18n.translateToLocal("blocksteps.waypoint.pos"), posX + 11, posY + 50, Theme.getAsHex(workspace.currentTheme.font), false);
            workspace.getFontRenderer().drawString(I18n.translateToLocal("blocksteps.waypoint.entityType"), posX + 11, posY + 100, Theme.getAsHex(workspace.currentTheme.font), false);
            workspace.getFontRenderer().drawString(I18n.translateToLocal("blocksteps.waypoint.renderRange"), posX + width - 10 - workspace.getFontRenderer().getStringWidth(I18n.translateToLocal("blocksteps.waypoint.renderRange")), posY + 130, Theme.getAsHex(workspace.currentTheme.font), false);

            int x = posX + 11;
            int y = posY + height - 12;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0D);
            GlStateManager.rotate(-90F, 0F, 0F, 1F);
            GlStateManager.translate(-(x), -(y), 0D);
            RendererHelper.endGlScissor();
            workspace.getFontRenderer().drawString(I18n.translateToLocal("blocksteps.waypoint.colour"), x, y, Theme.getAsHex(workspace.currentTheme.font), false);
            GlStateManager.popMatrix();
            int size = height - 140;
            RendererHelper.drawGradientOnScreen(0xff000000, 0xff000000, 0xffffffff, 0xff000000 | colourHue, posX + 20, posY + height - size - 11, size, size, 0D);

            RendererHelper.drawHueStripOnScreen(255, posX + 20 + size + 3, posY + height - size - 11, 10, size, 0D);

            workspace.getFontRenderer().drawString("#", posX + 20 + size + 16, posY + height - 20, Theme.getAsHex(workspace.currentTheme.font), false);
            RendererHelper.drawColourOnScreen(selectedWaypoint.colour, 255, posX + 20 + size + 16, posY + height - 23 - (size - 15) - 3, 60, (size - 15), 0D);

            if(entInstance != null)
            {
                GlStateManager.enableColorMaterial();
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(posX + 20 + size + 16 + 30), (float)(posY + height - 23 - 12 - 3 + 5), 50.0F);
                float aScale = 25F;
                GlStateManager.scale(-aScale, aScale, aScale);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(((float)Math.atan((double)(((float)-(posY + height - 23 - 12 - ((size - 15) / 2) - 3 + 5) + (posY + mouseY)) / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate((float)Math.atan((double)(((float)-(posX + 20 + size + 16 + 30) + (posX + mouseX)) / 40.0F)) * 40.0F, 0.0F, 1.0F, 0.0F);

                RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
                float viewY = rendermanager.playerViewY;
                rendermanager.setPlayerViewY(180.0F);
                RendererHelper.setColorFromInt(selectedWaypoint.colour);
                RendererHelper.startGlScissor(posX + 20 + size + 16, posY + height - 23 - (size - 15) - 3, 60, (size - 15));

                //                EntityHelperBase.storeBossStatus();
                if(entInstance instanceof EntityDragon)
                {
                    GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);
                }
                try
                {
                    rendermanager.setRenderShadow(false);
                    rendermanager.doRenderEntity(entInstance, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
                    rendermanager.setRenderShadow(true);
                }
                catch(Exception ignored){}
                if(entInstance instanceof EntityDragon)
                {
                    GlStateManager.rotate(180F, 0.0F, -1.0F, 0.0F);
                }
                //                EntityHelperBase.restoreBossStatus();
                RendererHelper.startGlScissor(posX + 1, posY + 1, getWidth() - 2, getHeight() - 2);
                RendererHelper.setColorFromInt(0xffffff);
                rendermanager.setPlayerViewY(viewY);

                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.disableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.disableLighting();
            }

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

    public void createEntityInstance(String s)
    {
        try
        {
            if(!s.isEmpty())
            {
                entInstance = (Entity)Class.forName(s).getConstructor(World.class).newInstance(Minecraft.getMinecraft().theWorld);
            }
            else
            {
                entInstance = new EntityWaypoint(Minecraft.getMinecraft().theWorld);
            }
        }
        catch(Exception ignored)
        {
            entInstance = null;
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
            else if(e == elementBeam)
            {
                selectedWaypoint.beam = elementBeam.toggledState;
            }
            else if(e == elementEntityType)
            {
                selectedWaypoint.entityType = elementEntityType.selected.equals("Waypoint") ? "" : ((Class)elementEntityType.choices.get(elementEntityType.selected)).getName();
                createEntityInstance(selectedWaypoint.entityType);
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
            else if(e == elementRenderRange)
            {
                try
                {
                    int i = Integer.parseInt(elementRenderRange.textFields.get(0).getText());
                    selectedWaypoint.renderRange = i;
                }
                catch(NumberFormatException ignored){}
            }
        }
    }

    @Override
    public void elementTriggered(Element e)
    {
        keyInput(e);
        if(e.id == ID_CURRENT_POS)
        {
            BlockPos pos = new BlockPos(Minecraft.getMinecraft().thePlayer);
            elementPosition.textFields.get(0).setText(Integer.toString(pos.getX()));
            elementPosition.textFields.get(1).setText(Integer.toString(pos.getY()));
            elementPosition.textFields.get(2).setText(Integer.toString(pos.getZ()));
        }
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
