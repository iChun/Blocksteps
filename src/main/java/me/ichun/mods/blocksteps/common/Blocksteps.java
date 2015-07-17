package me.ichun.mods.blocksteps.common;

import me.ichun.mods.blocksteps.common.core.Config;
import me.ichun.mods.blocksteps.common.core.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import us.ichun.mods.ichunutil.common.core.Logger;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;

@Mod(modid = Blocksteps.MODNAME, name = Blocksteps.MODNAME, clientSideOnly = true,
        version = Blocksteps.VERSION,
        guiFactory = "us.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:iChunUtil@[" + iChunUtil.versionMC +".4.0," + (iChunUtil.versionMC + 1) + ".0.0)"
)
public class Blocksteps
{
    public static final String MODNAME = "Blocksteps";
    public static final String VERSION = iChunUtil.versionMC + ".0.0";

    public static final Logger logger = Logger.createLogger(MODNAME);

    @Mod.Instance(Blocksteps.MODNAME)
    public static Blocksteps instance;

    public static Config config;

    public static EventHandler eventHandler;

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        config = (Config)ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        eventHandler = new EventHandler();
        FMLCommonHandler.instance().bus().register(eventHandler);
        MinecraftForge.EVENT_BUS.register(eventHandler);

        ModVersionChecker.register_iChunMod(new ModVersionInfo(MODNAME, iChunUtil.versionOfMC, VERSION, true));
    }
}
