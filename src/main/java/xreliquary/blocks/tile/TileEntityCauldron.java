package xreliquary.blocks.tile;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.client.particle.EntityCauldronBubbleFX;
import xreliquary.client.particle.EntityCauldronSteamFX;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.items.ItemPotionEssence;
import xreliquary.reference.Settings;
import xreliquary.util.RegistryHelper;
import xreliquary.util.potions.PotionEssence;

import java.util.ArrayList;
import java.util.List;

public class TileEntityCauldron extends TileEntityBase {

    public int redstoneCount = 0;
    public PotionEssence potionEssence = null;
    public int glowstoneCount = 0;
    public boolean hasGunpowder = false;
    public boolean hasNetherwart = false;
    public int cookTime = 0;
    private int liquidLevel = 0;

    public TileEntityCauldron() {
    }

    @Override
    public void update() {
        //Item addition gets handled by the block's onEntityCollided method.
        if (getHeatSources().contains(worldObj.getBlockState(getPos().add(0,-1,0)).getBlock()) && getLiquidLevel() > 0) {
            if (potionEssence != null) {
                if(cookTime < getCookTime())
                    cookTime++;
            }
            if(worldObj.isRemote) {
                for (int particleCount = 0; particleCount <= 2; ++particleCount)
                    spawnBoilingParticles();
                if (hasGunpowder) spawnGunpowderParticles();
                if (glowstoneCount > 0) spawnGlowstoneParticles();
                if (hasNetherwart) {
                    spawnNetherwartParticles();
                    if (finishedCooking()) {
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


        int color = getColor(potionEssence);

        float red = (((color >> 16) & 255) / 256F);
        float green = (((color >> 8) & 255) / 256F);
        float blue = (((color >> 0) & 255) / 256F);

        EntityCauldronBubbleFX bubble = new EntityCauldronBubbleFX(Minecraft.getMinecraft().getTextureManager(), worldObj, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + 0.01D + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0D, 0D, 0D, red, green, blue);
        EntityCauldronSteamFX steam = new EntityCauldronSteamFX(worldObj, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + 0.01D + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0D, 0.05D + 0.02F * getRenderLiquidLevel(), 0D, red, green, blue);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(bubble);
        if (worldObj.rand.nextInt(6) == 0)
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(steam);
    }
    private float getRenderLiquidLevel() {
        int j = MathHelper.clamp_int(getLiquidLevel(), 0, 3);
        return (float)(6 + 3 * j) / 16.0F;
    }

    public int getColor(PotionEssence essence) {
        //basically we're just using vanillas right now. This is hilarious in comparison to the old method, which is a mile long.
        return  PotionHelper.calcPotionLiquidColor(essence == null ? null :essence.getEffects());
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
        this.setLiquidLevel(tag.getShort("liquidLevel"));
        this.glowstoneCount = tag.getInteger("glowstoneCount");
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
        tag.setInteger( "liquidLevel", getLiquidLevel());
        tag.setInteger("cookTime", cookTime);
        tag.setInteger("redstoneCount", redstoneCount);
        tag.setInteger("glowstoneCount", glowstoneCount);
        tag.setBoolean("hasGunpowder", hasGunpowder);
        tag.setBoolean("hasNetherwart", hasNetherwart);
        tag.setTag("potionEssence", potionEssence == null ? new NBTTagCompound() : potionEssence.writeToNBT());
    }

    public boolean finishedCooking() {
        return hasNetherwart && potionEssence != null && this.cookTime >= getCookTime();
    }

    public NBTTagCompound removeContainedPotion(World world) {
        if (!hasNetherwart || potionEssence == null || getLiquidLevel() <= 0)
            return null;

        setLiquidLevel(getLiquidLevel() - 1);
        NBTTagCompound tag = getFinishedPotion();

        if (getLiquidLevel() <= 0) {
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
        this.glowstoneCount = 0;
        this.hasGunpowder = false;
        this.hasNetherwart = false;
        this.redstoneCount = 0;
        this.potionEssence = null;
    }

    public boolean isItemValidForInput(ItemStack ist) {
        if (getLiquidLevel() < 3)
            return false;
        return ((ist.getItem() instanceof ItemPotionEssence && this.potionEssence == null)
                || (ist.getItem() == Items.gunpowder && !this.hasGunpowder)
                || (ist.getItem() == Items.glowstone_dust && this.glowstoneCount < getGlowstoneAmpLimit())
                || (ist.getItem() == Items.redstone && this.redstoneCount < getRedstoneAmpLimit())
                || (ist.getItem() == Items.nether_wart && !this.hasNetherwart));
    }

    public void addItem(ItemStack ist) {
        if (ist.getItem() instanceof ItemPotionEssence) {
            potionEssence = new PotionEssence(ist.getTagCompound());
        } else if (ist.getItem() == Items.gunpowder) {
            this.hasGunpowder = true;
        } else if (ist.getItem() == Items.glowstone_dust) {
            ++this.glowstoneCount;
            potionEssence.addGlowstone(this.glowstoneCount);
        } else if (ist.getItem() == Items.redstone) {
            ++this.redstoneCount;
            potionEssence.addRedstone(this.redstoneCount);
        } else if (ist.getItem() == Items.nether_wart) {
            this.hasNetherwart = true;
        }

        worldObj.markBlockForUpdate(this.getPos());
    }

    public int getGlowstoneAmpLimit()
    {
        return Settings.ApothecaryCauldron.glowstoneLimit;
    }

    public int getRedstoneAmpLimit() {
        return Settings.ApothecaryCauldron.redstoneLimit;
    }

    public List<Block> getHeatSources() {
        List<Block> heatSources = new ArrayList<Block>();
        List<String> heatSourceBlockNames = Settings.ApothecaryCauldron.heatSources;

        for (String blockName : heatSourceBlockNames) {
            if (!heatSources.contains(RegistryHelper.getBlockFromName(blockName)))
                heatSources.add(RegistryHelper.getBlockFromName(blockName));
        }
        //defaults that can't be removed.
        heatSources.add(Blocks.lava);
        heatSources.add(Blocks.flowing_lava);
        heatSources.add(Blocks.fire);
        return heatSources;
    }

    public int getCookTime() {
        return Settings.ApothecaryCauldron.cookTime;
    }

    public void handleCollidingEntity( World world, BlockPos pos, Entity collidingEntity)
    {
        int l = 3;
        float f = (float) pos.getY() + (6.0F + (float) (3 * l)) / 16.0F;
        if (collidingEntity.getEntityBoundingBox().minY <= (double) f) {

            if (collidingEntity.isBurning() && l > 0) {
                collidingEntity.extinguish();
                //this.setLiquidLevel(world, x, y, z, l - 1);
            }
            if (collidingEntity instanceof EntityLivingBase ) {
                if (this.potionEssence == null)
                    return;
                for (PotionEffect effect : this.potionEssence.getEffects()) {
                    Potion potion = Potion.potionTypes[effect.getPotionID()];
                    if (potion.isInstant() && world.getWorldTime() % 20 != 0)
                        continue;
                    PotionEffect reducedEffect = new PotionEffect(effect.getPotionID(), potion.isInstant() ? 1 : effect.getDuration() / 20, Math.max(0, effect.getAmplifier() - 1));
                    ((EntityLivingBase) collidingEntity).addPotionEffect(reducedEffect);
                }
                if (this.cookTime > 0 && world.getWorldTime() % 20 != 0) {
                    collidingEntity.attackEntityFrom( DamageSource.inFire, 1.0F);
                }
            }


            if (collidingEntity instanceof EntityItem ) {
                ItemStack item = ((EntityItem) collidingEntity).getEntityItem();
                while (this.isItemValidForInput(item)) {

                    this.addItem(item);
                    if (--item.stackSize < 1)
                        collidingEntity.setDead();
                }
            }

        }
    }

    public int getColorMultiplier() {
        return PotionHelper.calcPotionLiquidColor(potionEssence == null ? new ArrayList<PotionEffect>() : potionEssence.getEffects());
    }

    public int getLiquidLevel()
    {
        return liquidLevel;
    }

    public void fillWithRain(World world)
    {
        if (getLiquidLevel() < 3  && !finishedCooking()) {
            setLiquidLevel(getLiquidLevel() + 1);
        }
    }

    public boolean handleBlockActivation(World world, EntityPlayer player){
        ItemStack itemStack = player.inventory.getCurrentItem();

        if (itemStack.getItem() == Items.water_bucket || (itemStack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) itemStack.getItem()).getFluid(itemStack).equals(new FluidStack(FluidRegistry.WATER, 1000)))) {
            if (getLiquidLevel() < 3 && !finishedCooking()) {
                if (!player.capabilities.isCreativeMode) {
                    if (itemStack.getItem() == Items.water_bucket) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                    } else {
                        ((IFluidContainerItem) itemStack.getItem()).drain(itemStack, 1000, true);
                    }
                }

                setLiquidLevel(3);
                cookTime = 0;
            }

            return true;
        } else {
            if (itemStack.getItem() == ModItems.potion && (itemStack.getTagCompound() == null || !itemStack.getTagCompound().getBoolean("hasPotion"))) {
                if (getLiquidLevel() > 0) {

                    if (finishedCooking()) {
                        ItemStack potion = new ItemStack(ModItems.potion, 1, 0);
                        potion.setTagCompound( removeContainedPotion( world ) );

                        --itemStack.stackSize;

                        if (itemStack.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, potion);
                        } else if (!player.inventory.addItemStackToInventory(potion)) {
                            world.spawnEntityInWorld(new EntityItem(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.5D, (double) pos.getZ() + 0.5D, potion));
                        }
                    }
                }
            } else if (isItemValidForInput(itemStack)) {
                addItem( itemStack );

                --itemStack.stackSize;
                if (itemStack.stackSize <= 0)
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
            return true;
        }
    }

    public void setLiquidLevel(int liquidLevel) {
        this.liquidLevel = liquidLevel;
        if (this.worldObj != null) {
            IBlockState blockState = this.worldObj.getBlockState(this.getPos());
            blockState = blockState.withProperty(ModBlocks.apothecaryCauldron.LEVEL, liquidLevel);
            this.worldObj.setBlockState(this.getPos(),blockState);
            this.worldObj.updateComparatorOutputLevel(pos, ModBlocks.apothecaryCauldron);
        }
    }
}
