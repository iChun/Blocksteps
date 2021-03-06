package me.ichun.mods.blocksteps.common.core;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.ichunutil.common.core.config.types.Colour;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 10000000)
    public int renderBlockCount = 100000;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 32)
    public int renderDistance = 6;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int renderSky = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 1, max = 1000)
    public int renderSkySize = 75;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int renderCompass = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 200)
    public int saveInterval = 20 * 60 * 5;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int trackOtherPlayers = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int hideNamePlates = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int lockMapToHeadYaw = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int lockMapToHeadPitch = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 1, max = 15)
    public int stepRadius = 3;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int stepPeripherals = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int treeDetection = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int endTowerDetection = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int brightMap = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int waypointOnDeath = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int waypointIndicator = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int waypointIndicatorThroughBlocks = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int waypointLabelThroughBlocks = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int waypointLabelSize = 20;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int waypointBeamHeightAdjust = 15;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int waypointRenderInWorld = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = -180, max = 180)
    public int camStartHorizontal = 45;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = -90, max = 90)
    public int camStartVertical = 30;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 1000000)
    public int camStartScale = 800;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int camPanHorizontal = 90;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int camPanVertical = 15;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int camZoom = 100;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = -500, max = 500)
    public int camPosX = 50;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = -500, max = 500)
    public int camPosY = 50;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 1, max = 256)
    public int surfaceDepth = 2;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 1, max = 256)
    public int surfaceHorizontalUpdateDistance = 16;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 1, max = 3)
    public int mapType = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int mapFreq = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 20, max = 500)
    public int mapLoad = 20;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int mapShowEntities = 0;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int mapShowCoordinates = 0;

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
    @IntBool
    public int easterEgg = 1;

    @ConfigProp
    public KeyBind keyCamUp = new KeyBind(Keyboard.KEY_NUMPAD8);

    @ConfigProp
    public KeyBind keyCamDown = new KeyBind(Keyboard.KEY_NUMPAD2);

    @ConfigProp
    public KeyBind keyCamLeft = new KeyBind(Keyboard.KEY_NUMPAD4);

    @ConfigProp
    public KeyBind keyCamRight = new KeyBind(Keyboard.KEY_NUMPAD6);

    @ConfigProp
    public KeyBind keyCamUpFS = new KeyBind(Keyboard.KEY_UP);

    @ConfigProp
    public KeyBind keyCamDownFS = new KeyBind(Keyboard.KEY_DOWN);

    @ConfigProp
    public KeyBind keyCamLeftFS = new KeyBind(Keyboard.KEY_LEFT);

    @ConfigProp
    public KeyBind keyCamRightFS = new KeyBind(Keyboard.KEY_RIGHT);

    @ConfigProp
    public KeyBind keyCamZoomIn = new KeyBind(Keyboard.KEY_NUMPAD9);

    @ConfigProp
    public KeyBind keyCamZoomOut = new KeyBind(Keyboard.KEY_NUMPAD3);

    @ConfigProp
    public KeyBind keyToggle = new KeyBind(Keyboard.KEY_NUMPAD5);

    @ConfigProp
    public KeyBind keyToggleFullscreen = new KeyBind(Keyboard.KEY_NUMPAD7);

    @ConfigProp
    public KeyBind keySwitchMapMode = new KeyBind(Keyboard.KEY_NUMPAD1);

    @ConfigProp
    public KeyBind keyWaypoints = new KeyBind(Keyboard.KEY_NUMPAD0);

    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return Blocksteps.MOD_ID;
    }

    @Override
    public String getModName()
    {
        return Blocksteps.MOD_NAME;
    }
}
