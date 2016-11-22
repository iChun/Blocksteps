package me.ichun.mods.blocksteps.common.gui.window.element;

import me.ichun.mods.blocksteps.common.core.Waypoint;
import me.ichun.mods.blocksteps.common.gui.window.WindowWaypoints;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementListTree;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.util.ResourceLocation;

public class ElementWaypointList extends ElementListTree
{
    public WindowWaypoints parent;
    public int mX;
    public int mY;

    public ElementWaypointList(WindowWaypoints window, int x, int y, int w, int h, int ID, boolean igMin, boolean drag)
    {
        super(window, x, y, w, h, ID, igMin, drag);
        parent = window;
    }

    @Override
    public void draw(int mouseX, int mouseY, boolean hover)
    {
        mX = mouseX;
        mY = mouseY;
        super.draw(mouseX, mouseY, hover);
    }

    @Override
    public void createTree(ResourceLocation loc, Object obj, int h, int attach, boolean expandable, boolean collapse)
    {
        trees.add(new TreeWaypoint(loc, obj, h, attach, expandable, collapse));
    }

    @Override
    public String tooltip()
    {
        int treeHeight = 0;
        int treeHeight1 = 0;
        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);
            treeHeight1 += tree.getHeight();
        }

        int scrollHeight = 0;
        if(treeHeight1 > height)
        {
            scrollHeight = (int)((height - treeHeight1) * sliderProg);
        }

        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);

            if(mX >= posX && mX < posX + width + (treeHeight1 > height ? 10 : 0) && mY >= posY + treeHeight + scrollHeight && mY < posY + treeHeight + scrollHeight + tree.getHeight())
            {
                return ((Waypoint)tree.attachedObject).name;
            }

            treeHeight += tree.getHeight();
        }
        return null; //return null for no tooltip. This is localized.
    }

    public class TreeWaypoint extends Tree
    {
        public TreeWaypoint(ResourceLocation loc, Object obj, int h, int attach, boolean expandable, boolean collapse)
        {
            super(loc, obj, h, attach, expandable, collapse);
        }

        public Tree draw(int mouseX, int mouseY, boolean hover, int width, int treeHeight, boolean hasScroll, int totalHeight, boolean clicking, boolean rClicking)
        {
            Tree tree = super.draw(mouseX, mouseY, hover, width, treeHeight, hasScroll, totalHeight, clicking, rClicking);

            RendererHelper.drawColourOnScreen(((Waypoint)attachedObject).colour, 255, getPosX() + 1, getPosY() + treeHeight + 1, 11, 11, 0);

            return tree;
        }
    }

}
