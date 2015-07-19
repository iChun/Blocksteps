package me.ichun.mods.blocksteps.common.core;

import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import us.ichun.mods.ichunutil.common.core.config.types.Colour;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 1000000)
    public int renderBlockCount = 1000;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 32)
    public int renderDistance = 4;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int renderSky = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = -180, max = 180)
    public int camStartHorizontal = 45;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = -90, max = 90)
    public int camStartVertical = 30;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = -90, max = 90)
    public int camStartScale = 100;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int camPanHorizontal = 90;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int camPanVertical = 15;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int camZoom = 10;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = -500, max = 500)
    public int camPosX = 50;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = -500, max = 500)
    public int camPosY = 50;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 1)
    public int mapType = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int mapShowEntities = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 100)
    public int mapStartX = 70;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 100)
    public int mapStartY = 2;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 100)
    public int mapEndX = 99;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 100)
    public int mapEndY = 30;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 100)
    public int mapBorderOpacity = 100;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int mapBorderOutline = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public Colour mapBorderOutlineColour = new Colour(150, 150, 150);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 1, max = 100)
    public int mapBorderSize = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public Colour mapBorderColour = new Colour(34, 34, 34);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 100)
    public int mapBackgroundOpacity = 70;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public Colour mapBackgroundColour = new Colour(0);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind keyCamUp = new KeyBind(Keyboard.KEY_UP, false, true, false, false);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind keyCamDown = new KeyBind(Keyboard.KEY_DOWN, false, true, false, false);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind keyCamLeft = new KeyBind(Keyboard.KEY_LEFT, false, true, false, false);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind keyCamRight = new KeyBind(Keyboard.KEY_RIGHT, false, true, false, false);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind keyCamZoomIn = new KeyBind(Keyboard.KEY_UP, true, true, false, false);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind keyCamZoomOut = new KeyBind(Keyboard.KEY_DOWN, true, true, false, false);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind keyToggle = new KeyBind(Keyboard.KEY_TAB, false, true, false, false);

    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return Blocksteps.MODNAME.toLowerCase();
    }

    @Override
    public String getModName()
    {
        return Blocksteps.MODNAME;
    }
}
