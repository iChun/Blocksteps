package me.ichun.mods.blocksteps.common.render;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.core.ChunkStore;
import me.ichun.mods.blocksteps.common.core.Waypoint;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderGlobalProxy extends RenderGlobal
{
    public boolean setupTerrain = false;

    public RenderGlobalProxy(Minecraft par1Minecraft)
    {
        super(par1Minecraft);
    }

    @Override
    public void loadRenderers()
    {
        if (this.theWorld != null && !setupTerrain)
        {
            this.displayListEntitiesDirty = true;
            Blocks.LEAVES.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            Blocks.LEAVES2.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            this.renderDistanceChunks = Blocksteps.config.renderDistance > 0 ? Blocksteps.config.renderDistance : this.mc.gameSettings.renderDistanceChunks;
            boolean flag = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();

            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactoryBlocksteps();

            if (flag != this.vboEnabled)
            {
                this.generateStars();
                this.generateSky();
                this.generateSky2();
            }

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
            }

            this.stopChunkUpdates();
            this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);

            if (this.theWorld != null)
            {
                Entity entity = this.mc.getRenderViewEntity();

                if (entity != null)
                {
                    this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    @Override
    public void generateSky2()
    {
        if (this.sky2VBO != null)
        {
            this.sky2VBO.deleteGlBuffers();
        }

        if (this.glSkyList2 >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }

        if (this.vboEnabled)
        {
            this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
        }
        else
        {
            this.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
            GL11.glEndList();
        }
    }

    @Override
    public void generateSky()
    {
        if (this.skyVBO != null)
        {
            this.skyVBO.deleteGlBuffers();
        }

        if (this.glSkyList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }

        if (this.vboEnabled)
        {
            this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
        }
        else
        {
            this.glSkyList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
            GL11.glEndList();
        }
    }

    @Override
    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks)
    {
        int pass = net.minecraftforge.client.MinecraftForgeClient.getRenderPass();
        if (this.renderEntitiesStartupCounter > 0)
        {
            if (pass > 0) return;
            --this.renderEntitiesStartupCounter;
        }
        else
        {
            double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
            double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
            double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRendererDispatcher.instance.prepare(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.objectMouseOver, partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            if (pass == 0) // no indentation to shrink patch
            {
                this.countEntitiesTotal = 0;
                this.countEntitiesRendered = 0;
                this.countEntitiesHidden = 0;
            }
            Entity entity1 = this.mc.getRenderViewEntity();
            double d3 = entity1.lastTickPosX + (entity1.posX - entity1.lastTickPosX) * (double)partialTicks;
            double d4 = entity1.lastTickPosY + (entity1.posY - entity1.lastTickPosY) * (double)partialTicks;
            double d5 = entity1.lastTickPosZ + (entity1.posZ - entity1.lastTickPosZ) * (double)partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d3;
            TileEntityRendererDispatcher.staticPlayerY = d4;
            TileEntityRendererDispatcher.staticPlayerZ = d5;
            this.renderManager.setRenderPosition(d3, d4, d5);
            this.mc.entityRenderer.enableLightmap();
            this.theWorld.theProfiler.endStartSection("global");
            List list = this.theWorld.getLoadedEntityList();
            if (pass == 0) // no indentation to shrink patch
            {
                this.countEntitiesTotal = list.size();
            }

            RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
            float viewX = rendermanager.playerViewX;
            float viewY = rendermanager.playerViewY;
            rendermanager.playerViewX = Blocksteps.eventHandler.angleX;
            rendermanager.setPlayerViewY(Blocksteps.eventHandler.angleY + 180F);

            int i;
            Entity entity2;

            for (i = 0; i < this.theWorld.weatherEffects.size(); ++i)
            {
                entity2 = (Entity)this.theWorld.weatherEffects.get(i);
                if (!entity2.shouldRenderInPass(pass)) continue;
                ++this.countEntitiesRendered;

                if (shouldRenderEntity(entity2) && entity2.isInRangeToRender3d(d0, d1, d2))
                {
                    this.renderManager.renderEntityStatic(entity2, partialTicks, false);
                }
            }

            this.theWorld.theProfiler.endStartSection("entities");
            Iterator iterator = this.renderInfos.iterator();
            RenderGlobal.ContainerLocalRenderInformation containerlocalrenderinformation;

            while (iterator.hasNext())
            {
                containerlocalrenderinformation = (RenderGlobal.ContainerLocalRenderInformation)iterator.next();
                Chunk chunk = this.theWorld.getChunkFromBlockCoords(containerlocalrenderinformation.renderChunk.getPosition());
                Iterator iterator2 = chunk.getEntityLists()[containerlocalrenderinformation.renderChunk.getPosition().getY() / 16].iterator();

                while (iterator2.hasNext())
                {
                    Entity entity3 = (Entity)iterator2.next();
                    if (!entity3.shouldRenderInPass(pass)) continue;
                    boolean flag2 = this.renderManager.shouldRender(entity3, camera, d0, d1, d2) || entity3.riddenByEntity == this.mc.thePlayer;

                    if (flag2)
                    {
                        if (!shouldRenderEntity(entity3) || entity3.posY >= 0.0D && entity3.posY < 256.0D && !this.theWorld.isBlockLoaded(new BlockPos(entity3)))
                        {
                            continue;
                        }

                        ++this.countEntitiesRendered;
                        this.renderManager.renderEntitySimple(entity3, partialTicks);
                    }

                    if (!flag2 && entity3 instanceof EntityWitherSkull && shouldRenderEntity(entity3))
                    {
                        this.mc.getRenderManager().renderWitherSkull(entity3, partialTicks);
                    }
                }
            }

            if(!Blocksteps.eventHandler.hideWaypoints)
            {
                ArrayList<Waypoint> points = Blocksteps.eventHandler.getWaypoints(mc.theWorld.provider.getDimension());
                for(Waypoint wp : points)
                {
                    double dx = mc.thePlayer.posX - (wp.pos.getX() + 0.5D);
                    double dz = mc.thePlayer.posZ - (wp.pos.getZ() + 0.5D);
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if(wp.renderRange == 0 || dist < wp.renderRange * 10D)
                    {
                        wp.render(renderManager.renderPosX, renderManager.renderPosY, renderManager.renderPosZ, partialTicks, false);
                    }
                }
            }

            rendermanager.playerViewX = viewX;
            rendermanager.setPlayerViewY(viewY);

            this.theWorld.theProfiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();
            iterator = this.renderInfos.iterator();
            TileEntity tileentity;

            while (iterator.hasNext())
            {
                containerlocalrenderinformation = (RenderGlobal.ContainerLocalRenderInformation)iterator.next();
                Iterator iterator1 = containerlocalrenderinformation.renderChunk.getCompiledChunk().getTileEntities().iterator();

                while (iterator1.hasNext())
                {
                    tileentity = (TileEntity)iterator1.next();
                    if (!tileentity.shouldRenderInPass(pass) || !camera.isBoundingBoxInFrustum(tileentity.getRenderBoundingBox())) continue;
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, -1);
                }
            }

            this.preRenderDamagedBlocks();
            iterator = this.damagedBlocks.values().iterator();

            while (iterator.hasNext())
            {
                DestroyBlockProgress destroyblockprogress = (DestroyBlockProgress)iterator.next();
                BlockPos blockpos = destroyblockprogress.getPosition();
                tileentity = this.theWorld.getTileEntity(blockpos);

                if (tileentity instanceof TileEntityChest)
                {
                    TileEntityChest tileentitychest = (TileEntityChest)tileentity;

                    if (tileentitychest.adjacentChestXNeg != null)
                    {
                        blockpos = blockpos.offset(EnumFacing.WEST);
                        tileentity = this.theWorld.getTileEntity(blockpos);
                    }
                    else if (tileentitychest.adjacentChestZNeg != null)
                    {
                        blockpos = blockpos.offset(EnumFacing.NORTH);
                        tileentity = this.theWorld.getTileEntity(blockpos);
                    }
                }

                Block block = this.theWorld.getBlockState(blockpos).getBlock();

                if (tileentity != null && tileentity.shouldRenderInPass(pass) && tileentity.canRenderBreaking() && camera.isBoundingBoxInFrustum(tileentity.getRenderBoundingBox()))
                {
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, destroyblockprogress.getPartialBlockDamage());
                }
            }

            this.postRenderDamagedBlocks();
            this.mc.entityRenderer.disableLightmap();
            this.mc.mcProfiler.endSection();
        }
    }

    public boolean shouldRenderEntity(Entity entity)
    {
        BlockPos pos = new BlockPos(entity);

        Minecraft mc = Minecraft.getMinecraft();

        if(Blocksteps.config.mapType == 3 || entity == mc.getRenderViewEntity() || entity instanceof EntityPlayer && Blocksteps.config.trackOtherPlayers == 1 || mc.getRenderViewEntity() != null && ((entity == mc.getRenderViewEntity().riddenByEntity || entity == mc.getRenderViewEntity().ridingEntity) || mc.getRenderViewEntity().ridingEntity != null && entity == mc.getRenderViewEntity().ridingEntity.ridingEntity))
        {
            return true;
        }
        else if(Blocksteps.config.mapType == 4)
        {
            int rangeHori = (Blocksteps.config.renderDistance == 0 ? (mc.gameSettings.renderDistanceChunks - 1) : (Blocksteps.config.renderDistance - 1)) * 16;
            double dx = pos.getX() - mc.thePlayer.posX;
            double dz = pos.getZ() - mc.thePlayer.posZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            return dist < rangeHori;
        }

        return Blocksteps.config.mapShowEntities == 1 && (ChunkStore.contains(pos) || ChunkStore.contains(pos.add(0, -1, 0)) || ChunkStore.contains(pos.add(0, -2, 0)));
    }

    public void markAllForUpdateFromPos(BlockPos ref)
    {
        int rangeHori = Math.max((Blocksteps.config.renderDistance == 0 ? (mc.gameSettings.renderDistanceChunks) : (Blocksteps.config.renderDistance)), 1) * 16;
        BlockPos min = ref.add(-rangeHori, 0, -rangeHori);
        BlockPos max = ref.add(rangeHori, 0, rangeHori);
        markBlocksForUpdate(min.getX(), 0, min.getZ(), max.getX(), theWorld.getActualHeight(), max.getZ());
    }

    @Override
    public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos){}

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch){}

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data){}

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) //Remove every case that plays sound instead
    {
        Random random = this.theWorld.rand;

        switch (type)
        {
            case 2000:
                int i1 = data % 3 - 1;
                int i = data / 3 % 3 - 1;
                double d8 = (double)blockPosIn.getX() + (double)i1 * 0.6D + 0.5D;
                double d10 = (double)blockPosIn.getY() + 0.5D;
                double d12 = (double)blockPosIn.getZ() + (double)i * 0.6D + 0.5D;

                for (int k1 = 0; k1 < 10; ++k1)
                {
                    double d13 = random.nextDouble() * 0.2D + 0.01D;
                    double d14 = d8 + (double)i1 * 0.01D + (random.nextDouble() - 0.5D) * (double)i * 0.5D;
                    double d17 = d10 + (random.nextDouble() - 0.5D) * 0.5D;
                    double d20 = d12 + (double)i * 0.01D + (random.nextDouble() - 0.5D) * (double)i1 * 0.5D;
                    double d23 = (double)i1 * d13 + random.nextGaussian() * 0.01D;
                    double d25 = -0.03D + random.nextGaussian() * 0.01D;
                    double d27 = (double)i * d13 + random.nextGaussian() * 0.01D;
                    this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d14, d17, d20, d23, d25, d27, new int[0]);
                }

                return;
            case 2001:
                Block block = Block.getBlockById(data & 4095);
                this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 255));
                break;
            case 2002:
                double d6 = (double)blockPosIn.getX();
                double d7 = (double)blockPosIn.getY();
                double d9 = (double)blockPosIn.getZ();

                for (int j1 = 0; j1 < 8; ++j1)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d6, d7, d9, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] { Item.getIdFromItem(Items.SPLASH_POTION)});
                }

                PotionType potiontype = PotionType.getPotionTypeForID(data);
                int k = PotionUtils.getPotionColor(potiontype);
                float f = (float)(k >> 16 & 255) / 255.0F;
                float f1 = (float)(k >> 8 & 255) / 255.0F;
                float f2 = (float)(k >> 0 & 255) / 255.0F;
                EnumParticleTypes enumparticletypes = potiontype.hasInstantEffect() ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;

                for (int i2 = 0; i2 < 100; ++i2)
                {
                    double d16 = random.nextDouble() * 4.0D;
                    double d19 = random.nextDouble() * Math.PI * 2.0D;
                    double d22 = Math.cos(d19) * d16;
                    double d24 = 0.01D + random.nextDouble() * 0.5D;
                    double d26 = Math.sin(d19) * d16;
                    Particle particle1 = this.spawnEntityFX(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d6 + d22 * 0.1D, d7 + 0.3D, d9 + d26 * 0.1D, d22, d24, d26, new int[0]);

                    if (particle1 != null)
                    {
                        float f5 = 0.75F + random.nextFloat() * 0.25F;
                        particle1.setRBGColorF(f * f5, f1 * f5, f2 * f5);
                        particle1.multiplyVelocity((float)d16);
                    }
                }
                break;
            case 2003:
                double d0 = (double)blockPosIn.getX() + 0.5D;
                double d1 = (double)blockPosIn.getY();
                double d2 = (double)blockPosIn.getZ() + 0.5D;

                for (int j = 0; j < 8; ++j)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d0, d1, d2, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] {Item.getIdFromItem(Items.ENDER_EYE)});
                }

                for (double d11 = 0.0D; d11 < (Math.PI * 2D); d11 += 0.15707963267948966D)
                {
                    this.spawnParticle(EnumParticleTypes.PORTAL, d0 + Math.cos(d11) * 5.0D, d1 - 0.4D, d2 + Math.sin(d11) * 5.0D, Math.cos(d11) * -5.0D, 0.0D, Math.sin(d11) * -5.0D, new int[0]);
                    this.spawnParticle(EnumParticleTypes.PORTAL, d0 + Math.cos(d11) * 5.0D, d1 - 0.4D, d2 + Math.sin(d11) * 5.0D, Math.cos(d11) * -7.0D, 0.0D, Math.sin(d11) * -7.0D, new int[0]);
                }

                return;
            case 2004:

                for (int l1 = 0; l1 < 20; ++l1)
                {
                    double d15 = (double)blockPosIn.getX() + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double d18 = (double)blockPosIn.getY() + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double d21 = (double)blockPosIn.getZ() + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d15, d18, d21, 0.0D, 0.0D, 0.0D, new int[0]);
                    this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d15, d18, d21, 0.0D, 0.0D, 0.0D, new int[0]);
                }

                return;
            case 2005:
                ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, data);
                break;
            case 2006:

                for (int l = 0; l < 200; ++l)
                {
                    float f3 = random.nextFloat() * 4.0F;
                    float f4 = random.nextFloat() * ((float)Math.PI * 2F);
                    double d3 = (double)(MathHelper.cos(f4) * f3);
                    double d4 = 0.01D + random.nextDouble() * 0.5D;
                    double d5 = (double)(MathHelper.sin(f4) * f3);
                    Particle particle = this.spawnEntityFX(EnumParticleTypes.DRAGON_BREATH.getParticleID(), false, (double)blockPosIn.getX() + d3 * 0.1D, (double)blockPosIn.getY() + 0.3D, (double)blockPosIn.getZ() + d5 * 0.1D, d3, d4, d5, new int[0]);

                    if (particle != null)
                    {
                        particle.multiplyVelocity(f3);
                    }
                }
                break;
            case 3000:
                this.theWorld.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, true, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                break;
            case 3001:
        }
    }
}
