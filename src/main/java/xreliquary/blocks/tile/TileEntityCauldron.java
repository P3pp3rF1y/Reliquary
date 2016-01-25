package xreliquary.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.blocks.tile.TileEntityBase;
import net.minecraft.block.Block;
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
import xreliquary.reference.Colors;
import xreliquary.reference.Names;
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

    /* TODO: add additional rendering code
                TileEntityCauldron cauldron = (TileEntityCauldron)world.getTileEntity(x, y, z);
                int color = getColor(cauldron.potionEssence);
                tessellator.setColorOpaque_I(color);
    */

    @Override
    public void update() {
        //Item addition gets handled by the block's onEntityCollided method.
        if (getHeatSources().contains(worldObj.getBlockState(getPos().add(0,-1,0)).getBlock()) && worldObj.getBlockState(getPos()).getValue(BlockApothecaryCauldron.LEVEL) > 0) {
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
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
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

        EntityCauldronBubbleFX bubble = new EntityCauldronBubbleFX(worldObj, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + 0.01D + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0D, 0D, 0D, red, green, blue);
        EntityCauldronSteamFX steam = new EntityCauldronSteamFX(worldObj, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + 0.01D + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0D, 0.05D + 0.02F * getRenderLiquidLevel(), 0D, red, green, blue);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(bubble);
        if (worldObj.rand.nextInt(6) == 0)
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(steam);
    }
    private float getRenderLiquidLevel() {
        int liquidLevel = worldObj.getBlockState(this.getPos()).getValue(BlockApothecaryCauldron.LEVEL);

        int j = MathHelper.clamp_int(liquidLevel, 0, 3);
        return (float)(6 + 3 * j) / 16.0F;
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
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
    }

    @SideOnly(Side.CLIENT)
    public void spawnGlowstoneParticles() {
        if (worldObj.rand.nextInt(8) > 0)
            return;
        double gauss = 0.5D + worldObj.rand.nextFloat() / 2;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, gauss, gauss, 0.0F);
    }

    @SideOnly(Side.CLIENT)
    public void spawnNetherwartParticles() {
        if (worldObj.rand.nextInt(8) > 0)
            return;
        double gauss = 0.5D + worldObj.rand.nextFloat() / 2;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, gauss, 0.0F, gauss);
    }

    @SideOnly(Side.CLIENT)
    public void spawnRedstoneParticles() {
        if (worldObj.rand.nextInt(10) / this.redstoneCount > 0)
            return;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        worldObj.spawnParticle(EnumParticleTypes.REDSTONE, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 1D, 0D, 0D);
    }

    @SideOnly(Side.CLIENT)
    public void spawnFinishedParticles() {
        if (worldObj.rand.nextInt(8) > 0)
            return;
        float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
        worldObj.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0D, 0D, 0D);
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
        IBlockState cauldronState = worldObj.getBlockState(this.getPos());
        if (!hasNetherwart || potionEssence == null || cauldronState.getValue(BlockApothecaryCauldron.LEVEL) <= 0)
            return null;
        BlockApothecaryCauldron cauldron = (BlockApothecaryCauldron)worldObj.getBlockState(this.getPos());
        cauldron.setLiquidLevel(worldObj, this.getPos(), cauldronState, cauldronState.getValue(BlockApothecaryCauldron.LEVEL) - 1);
        NBTTagCompound tag = getFinishedPotion();

        if (cauldronState.getValue(BlockApothecaryCauldron.LEVEL) <= 0) {
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
        if (worldObj.getBlockState(this.getPos()).getValue(BlockApothecaryCauldron.LEVEL) < 3)
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

        worldObj.markBlockForUpdate(this.getPos());
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
