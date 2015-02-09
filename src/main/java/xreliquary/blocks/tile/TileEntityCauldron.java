package xreliquary.blocks.tile;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.blocks.tile.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.BlockApothecaryCauldron;
import xreliquary.client.particle.EntityCauldronBubbleFX;
import xreliquary.client.particle.EntityCauldronSteamFX;
import xreliquary.items.ItemPotionEssence;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.util.potions.PotionEssence;

import java.util.ArrayList;
import java.util.List;

public class TileEntityCauldron extends TileEntityBase {

    public int redstoneCount = 0;
    public PotionEssence potionEssence = null;
    public boolean hasGlowstone = false;
    public boolean hasGunpowder = false;
    public boolean hasNetherwart = false;
    public int cookTime = 0;


    public TileEntityCauldron() {
    }

    @Override
    public void updateEntity() {
        //Item addition gets handled by the block's onEntityCollided method.
        if (getHeatSources().contains(worldObj.getBlock(xCoord, yCoord - 1, zCoord)) && worldObj.getBlockMetadata(xCoord, yCoord, zCoord) > 0) {
            if (potionEssence != null) {
                if(cookTime < getCookTime())
                    cookTime++;
            }
            if(worldObj.isRemote) {
                for (int particleCount = 0; particleCount <= 2; ++particleCount)
                    spawnBoilingParticles();
                if (hasGunpowder) spawnGunpowderParticles();
                if (hasGlowstone) spawnGlowstoneParticles();
                if (hasNetherwart) {
                    spawnNetherwartParticles();
                    if (potionEssence != null) {
                        spawnFinishedParticles();
                    }
                }
                if (redstoneCount > 0) spawnRedstoneParticles();
            }
        }
    }

    @Override
    public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
        return oldBlock != newBlock;
    }

    @SideOnly(Side.CLIENT)
    public void spawnBoilingParticles() {
        if (worldObj.rand.nextInt(getCookTime() * getCookTime()) > cookTime * cookTime)
            return;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.33F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.33F;


        int color = potionEssence == null ? Integer.parseInt(Colors.PURE, 16) : getColor(potionEssence);

        float red = (((color >> 16) & 255) / 256F);
        float green = (((color >> 8) & 255) / 256F);
        float blue = (((color >> 0) & 255) / 256F);

        EntityCauldronBubbleFX bubble = new EntityCauldronBubbleFX(worldObj, xCoord + 0.5D + xOffset, yCoord + 0.01D + BlockCauldron.getRenderLiquidLevel(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)), zCoord + 0.5D + zOffset, 0D, 0D, 0D, red, green, blue);
        EntityCauldronSteamFX steam = new EntityCauldronSteamFX(worldObj, xCoord + 0.5D + xOffset, yCoord + 0.01D + BlockCauldron.getRenderLiquidLevel(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)), zCoord + 0.5D + zOffset, 0D, 0.05D + 0.02F * BlockCauldron.getRenderLiquidLevel(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)), 0D, red, green, blue);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(bubble);
        if (worldObj.rand.nextInt(6) == 0)
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(steam);
    }

    public int getColor(PotionEssence essence) {
        //basically we're just using vanillas right now. This is hilarious in comparison to the old method, which is a mile long.
        return  PotionHelper.calcPotionLiquidColor(essence.getEffects());
    }

    @SideOnly(Side.CLIENT)
    public void spawnGunpowderParticles() {
        if (worldObj.rand.nextInt(8) > 0)
            return;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        worldObj.spawnParticle("smoke", xCoord + 0.5D + xOffset, yCoord + BlockCauldron.getRenderLiquidLevel(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)), zCoord + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
    }

    @SideOnly(Side.CLIENT)
    public void spawnGlowstoneParticles() {
        if (worldObj.rand.nextInt(8) > 0)
            return;
        double gauss = 0.5D + worldObj.rand.nextFloat() / 2;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        worldObj.spawnParticle("mobSpell", xCoord + 0.5D + xOffset, yCoord + BlockCauldron.getRenderLiquidLevel(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)), zCoord + 0.5D + zOffset, gauss, gauss, 0.0F);
    }

    @SideOnly(Side.CLIENT)
    public void spawnNetherwartParticles() {
        if (worldObj.rand.nextInt(8) > 0)
            return;
        double gauss = 0.5D + worldObj.rand.nextFloat() / 2;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        worldObj.spawnParticle("mobSpell", xCoord + 0.5D + xOffset, yCoord + BlockCauldron.getRenderLiquidLevel(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)), zCoord + 0.5D + zOffset, gauss, 0.0F, gauss);
    }

    @SideOnly(Side.CLIENT)
    public void spawnRedstoneParticles() {
        if (worldObj.rand.nextInt(10) / this.redstoneCount > 0)
            return;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        worldObj.spawnParticle("reddust", xCoord + 0.5D + xOffset, yCoord + BlockCauldron.getRenderLiquidLevel(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)), zCoord + 0.5D + zOffset, 1D, 0D, 0D);
    }

    @SideOnly(Side.CLIENT)
    public void spawnFinishedParticles() {
        if (worldObj.rand.nextInt(8) > 0)
            return;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        worldObj.spawnParticle("witchMagic", xCoord + 0.5D + xOffset, yCoord + BlockCauldron.getRenderLiquidLevel(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)), zCoord + 0.5D + zOffset, 0D, 0D, 0D);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.hasGlowstone = tag.getBoolean("hasGlowstone");
        this.hasNetherwart = tag.getBoolean("hasNetherwart");
        this.hasGunpowder = tag.getBoolean("hasGunpowder");
        this.redstoneCount = tag.getInteger("redstoneCount");
        this.cookTime = tag.getInteger("cookTime");
        this.potionEssence = new PotionEssence((NBTTagCompound)tag.getTag("potionEssence"));
        if (potionEssence.getEffects().size() == 0)
            this.potionEssence = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("cookTime", cookTime);
        tag.setInteger("redstoneCount", redstoneCount);
        tag.setBoolean("hasGlowstone", hasGlowstone);
        tag.setBoolean("hasGunpowder", hasGunpowder);
        tag.setBoolean("hasNetherwart", hasNetherwart);
        tag.setTag("potionEssence", potionEssence == null ? new NBTTagCompound() : potionEssence.writeToNBT());
    }

    public boolean finishedCooking() {
        return hasNetherwart && potionEssence != null && this.cookTime >= getCookTime();
    }

    public NBTTagCompound removeContainedPotion() {
        if (!hasNetherwart || potionEssence == null || worldObj.getBlockMetadata(xCoord, yCoord, zCoord) <= 0)
            return null;
        BlockApothecaryCauldron cauldron = (BlockApothecaryCauldron)worldObj.getBlock(xCoord, yCoord, zCoord);
        cauldron.setMetaData(worldObj, xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord) - 1);
        NBTTagCompound tag = getFinishedPotion();

        if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) <= 0) {
            clearAllFields();
        }
        return tag;
    }

    public NBTTagCompound getFinishedPotion() {
        NBTTagCompound tag = potionEssence.writeToNBT();
        NBTTagList effectsList = tag.getTagList("effects",10);
        NBTTagCompound newTag = new NBTTagCompound();
        newTag.setTag("effects", effectsList);
        newTag.setBoolean("hasPotion", true);
        if (hasGunpowder) { newTag.setBoolean("splash", true); }
        return newTag;
    }

    public void clearAllFields() {
        this.cookTime = 0;
        this.hasGlowstone = false;
        this.hasGunpowder = false;
        this.hasNetherwart = false;
        this.redstoneCount = 0;
        this.potionEssence = null;
    }

    public boolean isItemValidForInput(ItemStack ist) {
        if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) < 3)
            return false;
        return ((ist.getItem() instanceof ItemPotionEssence && this.potionEssence == null)
                || (ist.getItem() == Items.gunpowder && !this.hasGunpowder)
                || (ist.getItem() == Items.glowstone_dust && !this.hasGlowstone)
                || (ist.getItem() == Items.redstone && this.redstoneCount <= getRedstoneAmpLimit())
                || (ist.getItem() == Items.nether_wart && !this.hasNetherwart));
    }

    public void addItem(ItemStack ist) {
        if (ist.getItem() instanceof ItemPotionEssence) {
            potionEssence = new PotionEssence(ist.getTagCompound());
        } else if (ist.getItem() == Items.gunpowder) {
            this.hasGunpowder = true;
        } else if (ist.getItem() == Items.glowstone_dust) {
            this.hasGlowstone = true;
        } else if (ist.getItem() == Items.redstone) {
            ++this.redstoneCount;
        } else if (ist.getItem() == Items.nether_wart) {
            this.hasNetherwart = true;
        }

        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getRedstoneAmpLimit() {
        return Reliquary.CONFIG.getInt(Names.apothecary_cauldron, "redstone_limit");
    }

    public List<Block> getHeatSources() {
        List<Block> heatSources = new ArrayList<Block>();
        List<String> heatSourceBlockNames = (List<String>) Reliquary.CONFIG.get(Names.apothecary_cauldron, "heat_sources");

        for (String blockName : heatSourceBlockNames) {
            if (!heatSources.contains(Reliquary.CONTENT.getBlock(blockName)))
                heatSources.add(Reliquary.CONTENT.getBlock(blockName));
        }
        //defaults that can't be removed.
        heatSources.add(Blocks.lava);
        heatSources.add(Blocks.flowing_lava);
        heatSources.add(Blocks.fire);
        return heatSources;
    }

    public int getCookTime() {
        return Reliquary.CONFIG.getInt(Names.apothecary_cauldron, "cook_time");
    }
}
