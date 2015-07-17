package blocksteps.common;

import blocksteps.common.core.TickHandlerClient;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.config.Config;
import ichun.common.core.config.ConfigHandler;
import ichun.common.core.config.IConfigUser;
import ichun.common.iChunUtil;
import net.minecraft.block.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod(modid = "Blocksteps", name = "Blocksteps",
        version = Blocksteps.version,
        dependencies = "required-after:iChunUtil@["+ iChunUtil.versionMC + ".0.0,)"
)
public class Blocksteps
        implements IConfigUser
{
    public static final String version = iChunUtil.versionMC + ".0.0";

    private static final Logger logger = LogManager.getLogger("Blocksteps");

    @Instance("Blocksteps")
    public static Blocksteps instance;

    public static Config config;

    public static TickHandlerClient tickHandlerClient;

    public static ArrayList<Class<? extends Block>> periphBlockTypes = new ArrayList<Class<? extends Block>>();

    @Override
    public boolean onConfigChange(Config cfg, Property prop) { return true; }

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            console("You're loading Blocksteps on a server! This is a client-only mod!", true);
            return;
        }

        config = ConfigHandler.createConfig(event.getSuggestedConfigurationFile(), "Blocksteps", "Blocksteps", logger, instance);

        config.setCurrentCategory("clientOnly", "Client Only", "These settings only affect the client running the mod.");
        config.createIntProperty("maxTracked", "Max Blocks Tracked", "Maximum number of blocks to be tracked", true, false, 200, 0, Integer.MAX_VALUE);

        init();
    }

    @SideOnly(Side.CLIENT)
    public static void init()
    {
        tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);

        MinecraftForge.EVENT_BUS.register(tickHandlerClient);

        addPeripheralBlock(BlockTorch.class, BlockLever.class, BlockSapling.class, BlockRailBase.class, BlockBush.class, BlockSlab.class, BlockFire.class,
                BlockRedstoneWire.class, BlockCrops.class, BlockDoor.class, BlockLadder.class, BlockSign.class, BlockBasePressurePlate.class, BlockSnow.class, BlockReed.class,
                BlockFence.class, BlockCake.class, BlockRedstoneDiode.class, BlockPane.class, BlockFenceGate.class, BlockEnchantmentTable.class, BlockBrewingStand.class, BlockCauldron.class,
                BlockDragonEgg.class, BlockTripWire.class, BlockBeacon.class, BlockWall.class, BlockFlowerPot.class, BlockSkull.class, BlockAnvil.class, BlockHopper.class, BlockCarpet.class

        );
        addPeripheralBlock(BlockDynamicLiquid.class);//TODO think about this.
        addPeripheralBlock(BlockBed.class);//TODO think about this.
        addPeripheralBlock(BlockButton.class); //TODO think about this
        addPeripheralBlock(BlockLilyPad.class);//TODO think about these:
        //TODO check if the block is a periph by checking the collision?
    }

    public static void addPeripheralBlock(Class...clzs)
    {
        for(Class clz : clzs)
        {
            if(Block.class.isAssignableFrom(clz))
            {
                console("Adding block " + clz.getName() + " to periphs", false);
                periphBlockTypes.add(clz);
            }
        }
    }

    public static boolean isPeripheralBlock(Class<? extends Block> clz)
    {
        for(Class clzz : periphBlockTypes)
        {
            if(clzz.isAssignableFrom(clz))
            {
                return true;
            }
        }
        return false;
    }

    public static void console(String s, boolean warning)
    {
        StringBuilder sb = new StringBuilder();
        logger.log(warning ? Level.WARN : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
    }
}
