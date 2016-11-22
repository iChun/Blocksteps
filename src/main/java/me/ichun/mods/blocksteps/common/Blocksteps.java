package me.ichun.mods.blocksteps.common;

import me.ichun.mods.blocksteps.common.core.Config;
import me.ichun.mods.blocksteps.common.core.EventHandlerClient;
import me.ichun.mods.blocksteps.common.entity.EntityWaypoint;
import me.ichun.mods.blocksteps.common.render.RenderWaypoint;
import me.ichun.mods.ichunutil.common.core.Logger;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Blocksteps.MOD_NAME, name = Blocksteps.MOD_NAME, clientSideOnly = true,
        version = Blocksteps.VERSION,
        guiFactory = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:iChunUtil@[" + iChunUtil.VERSION_MAJOR +".0.0," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0)",
        acceptableRemoteVersions = "*"
)
public class Blocksteps
{
    public static final String VERSION = iChunUtil.VERSION_MAJOR + ".0.0";
    public static final String MOD_NAME = "Blocksteps";
    public static final String MOD_ID = "blocksteps";

    public static final Logger LOGGER = Logger.createLogger(MOD_NAME);

    @Mod.Instance(MOD_ID)
    public static Blocksteps instance;

    public static Config config;

    public static EventHandlerClient eventHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        eventHandler = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        RenderingRegistry.registerEntityRenderingHandler(EntityWaypoint.class, new RenderWaypoint.RenderFactory());

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, true));
    }
}
