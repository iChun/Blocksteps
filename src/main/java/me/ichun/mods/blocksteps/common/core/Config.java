package me.ichun.mods.blocksteps.common.core;

import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 10000)
    public int blockCount = 500;

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
    @IntMinMax(min = 0, max = 100)
    public int camPosX = 90;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 100)
    public int camPosY = 85;

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
