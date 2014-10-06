package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.util.NBTHelper;

import java.util.ArrayList;
import java.util.List;

@ContentInit
public class ItemSojournerStaff extends ItemToggleable {

    public ItemSojournerStaff() {
        super(Names.sojourner_staff);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        if (world.isRemote)
            return;

        if (this.isOnCooldown(ist)) {
            decrementCooldown(ist);
        }

        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        }
        if (player == null)
            return;

        if (this.isEnabled(ist)) {
            scanForMatchingTorchesToFillInternalStorage(ist, player);
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
        if (entityLiving.isSneaking() && !isOnCooldown(ist)) {
            cycleTorchMode(ist);
            setCooldown(ist);
            return true;
        }
        return false;
    }

    private void scanForMatchingTorchesToFillInternalStorage(ItemStack ist, EntityPlayer player) {
        List<String> torches = (List<String>) Reliquary.CONFIG.get(Names.sojourner_staff, "torches");
        List<Item> items = new ArrayList<Item>();

        for (String torch : torches) {
            items.add(ContentHandler.getItem(torch));
        }
        for (Item item : items) {
            if (!isInternalStorageFullOfItem(ist, item) && InventoryHelper.consumeItem(item, player)) {
                addItemToInternalStorage(ist, item);
            }
        }
    }

    private void addItemToInternalStorage(ItemStack ist, Item item) {
        NBTTagCompound tagCompound = ist.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }

        if (tagCompound.getTag("Items") == null)
            tagCompound.setTag("Items", new NBTTagList());
        NBTTagList tagList = tagCompound.getTagList("Items", 10);

        boolean added = false;
        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
            String itemName = tagItemData.getString("Name");
            if (itemName.equals(ContentHelper.getIdent(item))) {
                int quantity = tagItemData.getInteger("Quantity");
                tagItemData.setInteger("Quantity", quantity + 1);
                added = true;
            }
        }
        if (!added) {
            NBTTagCompound newTagData = new NBTTagCompound();
            newTagData.setString("Name", ContentHelper.getIdent(item));
            newTagData.setInteger("Quantity", 1);
            tagList.appendTag(newTagData);
        }

        tagCompound.setTag("Items", tagList);

        ist.setTagCompound(tagCompound);
    }

    private boolean hasItemInInternalStorage(ItemStack ist, Item item, int cost) {
        NBTTagCompound tagCompound = ist.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        if (tagCompound.hasNoTags()) {
            tagCompound.setTag("Items", new NBTTagList());
            return false;
        }

        NBTTagList tagList = tagCompound.getTagList("Items", 10);
        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
            String itemName = tagItemData.getString("Name");
            if (itemName.equals(ContentHelper.getIdent(item))) {
                int quantity = tagItemData.getInteger("Quantity");
                return quantity >= cost;
            }
        }

        return false;
    }

    private boolean isInternalStorageFullOfItem(ItemStack ist, Item item) {
        if (hasItemInInternalStorage(ist, item, 1)) {
            NBTTagCompound tagCompound = ist.getTagCompound();
            NBTTagList tagList = tagCompound.getTagList("Items", 10);

            for (int i = 0; i < tagList.tagCount(); ++i)
            {
                NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
                String itemName = tagItemData.getString("Name");
                if (itemName.equals(ContentHelper.getIdent(item))) {
                    int quantity = tagItemData.getInteger("Quantity");
                    return quantity >= getTorchItemMaxCapacity();
                }
            }
        }
        return false;
    }

    public String getTorchPlacementMode(ItemStack ist) {
        if (ist.getTagCompound() == null) {
            return null;
        }

        NBTTagCompound tagCompound = ist.getTagCompound();
        String torchToPlace = tagCompound.getString("Torch");

        NBTTagList tagList = tagCompound.getTagList("Items", 10);

        if (torchToPlace != null) {
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
                String itemName = tagItemData.getString("Name");
                if (itemName.equals(torchToPlace)) {
                    int quantity = tagItemData.getInteger("Quantity");
                    if (quantity <= 0)
                        torchToPlace = null;
                }
            }
        }
        if (torchToPlace == null || torchToPlace.isEmpty()) {
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
                String itemName = tagItemData.getString("Name");

                int quantity = tagItemData.getInteger("Quantity");
                if (quantity > 0) {
                    tagCompound.setString("Torch", itemName);
                    return itemName;
                }
            }
        }
        return torchToPlace;
    }

    private void cycleTorchMode(ItemStack ist) {
        String mode = getTorchPlacementMode(ist);
        if (mode == null || mode.isEmpty())
            return;

        NBTTagCompound tagCompound = ist.getTagCompound();

        NBTTagList tagList = tagCompound.getTagList("Items", 10);

        boolean itemFound = false;
        String firstItem = null;
        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
            String itemName = tagItemData.getString("Name");
            int quantity = tagItemData.getInteger("Quantity");
            if (quantity <= 0) continue;
            if (firstItem == null) {
                firstItem = itemName;
            }
            if (itemFound) {
                tagCompound.setString("Torch", itemName);
                return;
            }
            if (itemName.equals(mode))
                itemFound = true;
            if (i == tagList.tagCount() - 1) {
                tagCompound.setString("Torch", firstItem);
            }
        }
    }

    private int getTorchItemMaxCapacity() {
        return Reliquary.CONFIG.getInt(Names.sojourner_staff, "max_capacity_per_item_type");
    }

    public boolean removeItemFromInternalStorage(ItemStack ist, Item item, int cost) {
        if (hasItemInInternalStorage(ist, item, cost)) {
            NBTTagCompound tagCompound = ist.getTagCompound();

            NBTTagList tagList = tagCompound.getTagList("Items", 10);

            NBTTagList replacementTagList = new NBTTagList();

            for (int i = 0; i < tagList.tagCount(); ++i)
            {
                NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
                String itemName = tagItemData.getString("Name");
                if (itemName.equals(ContentHelper.getIdent(item))) {
                    int quantity = tagItemData.getInteger("Quantity");
                    tagItemData.setInteger("Quantity", quantity - cost);
                }
                replacementTagList.appendTag(tagItemData);
            }
            tagCompound.setTag("Items", replacementTagList);
            ist.setTagCompound(tagCompound);
            return true;
        }
        return false;

    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean par4) {
        //maps the contents of the Sojourner's staff to a tooltip, so the player can review the torches stored within.
        String phrase = "Nothing.";
        String placing = "Nothing.";
        NBTTagCompound tagCompound = ist.getTagCompound();
        if (tagCompound != null) {
            NBTTagList tagList = tagCompound.getTagList("Items", 10);
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
                String itemName = tagItemData.getString("Name");
                Item containedItem = ContentHandler.getItem(itemName);
                int quantity = tagItemData.getInteger("Quantity");
                phrase = String.format("%s%s", phrase.equals("Nothing.") ? "" : String.format("%s;", phrase), new ItemStack(containedItem, 1, 0).getDisplayName() + ": " + quantity);
            }

            //add "currently placing: blah blah blah" to the tooltip.

            Item placingItem = ContentHandler.getItem(getTorchPlacementMode(ist));

            if (placingItem != null) {
                placing = new ItemStack(placingItem, 1, 0).getDisplayName();
            }
        }
        this.formatTooltip(ImmutableMap.of("phrase", phrase, "placing", placing), ist, list);
    }


    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xOff, float yOff, float zOff) {
        if (this.isOnCooldown(ist))
            return false;
        if (!player.canPlayerEdit(x, y, z, side, ist))
            return false;
        if (player.isSneaking())
            return false;
        if (getTorchPlacementMode(ist) == null)
            return false;
        Block blockAttemptingPlacement = Block.getBlockFromName(getTorchPlacementMode(ist));
        if (blockAttemptingPlacement == null)
            return false;

        Block blockTargetted = world.getBlock(x, y, z);

        if (ContentHelper.areBlocksEqual(blockTargetted, Blocks.snow)) {
            side = 1;
        } else if (!ContentHelper.areBlocksEqual(blockTargetted, Blocks.vine) && !ContentHelper.areBlocksEqual(blockTargetted, Blocks.tallgrass) && !ContentHelper.areBlocksEqual(blockTargetted, Blocks.deadbush) && (blockTargetted == null || !blockTargetted.isReplaceable(world, x, y, z))) {
            x += side == 4 ? -1 : side == 5 ? 1 : 0;
            y += side == 0 ? -1 : side == 1 ? 1 : 0;
            z += side == 2 ? -1 : side == 3 ? 1 : 0;
        }

        if (blockAttemptingPlacement.canPlaceBlockAt(world, x, y, z)) {
            if (world.canPlaceEntityOnSide(blockAttemptingPlacement, x, y, z, false, side, player, ist)) {

                if (!player.capabilities.isCreativeMode) {
                    int cost = 1;
                    int distance = (int) player.getDistance(x, y, z);
                    for (; distance > 6; distance -= 6) {
                        cost++;
                    }
                    if (!removeItemFromInternalStorage(ist, Item.getItemFromBlock(blockAttemptingPlacement), cost))
                        return false;
                }
                if (placeBlockAt(ist, player, world, x, y, z, side, xOff, yOff, zOff, attemptSide(world, x, y, z, side, blockAttemptingPlacement), blockAttemptingPlacement)) {
                    blockAttemptingPlacement.onBlockAdded(world, x, y, z);
                    double gauss = 0.5D + world.rand.nextFloat() / 2;
                    player.swingItem();
                    world.spawnParticle("mobSpell", x + 0.5D, y + 0.5D, z + 0.5D, gauss, gauss, 0.0F);
                    world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, blockAttemptingPlacement.stepSound.getStepResourcePath(), (blockAttemptingPlacement.stepSound.getVolume() + 1.0F) / 2.0F, blockAttemptingPlacement.stepSound.getPitch() * 0.8F);
                    this.setCooldown(ist);
                }
            }
        }
        return true;
    }

    private int attemptSide(World world, int x, int y, int z, int side, Block block) {

        return block.onBlockPlaced(world, x, y, z, side, x, y, z, 0);
    }

    private void decrementCooldown(ItemStack ist) {
        NBTHelper.setShort("cooldown", ist, NBTHelper.getShort("cooldown", ist) - 1);
    }

    private boolean isOnCooldown(ItemStack ist) {
        return NBTHelper.getShort("cooldown", ist) > 0;
    }

    private void setCooldown(ItemStack ist) {
        NBTHelper.setShort("cooldown", ist, 10);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (this.isOnCooldown(ist))
            return ist;
        if (!player.isSneaking()) {
            MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                float xOff = (float) (mop.blockX - player.posX);
                float yOff = (float) (mop.blockY - player.posY);
                float zOff = (float) (mop.blockZ - player.posZ);
                this.onItemUse(ist, player, world, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, xOff, yOff, zOff);
            }
        }
        return super.onItemRightClick(ist, world, player);
    }

    @Override
    protected MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean par3) {
        float var4 = 1.0F;
        float var5 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * var4;
        float var6 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * var4;
        double var7 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * var4;
        double var9 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * var4 + 1.62D - par2EntityPlayer.yOffset;
        double var11 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * var4;
        Vec3 var13 = Vec3.createVectorHelper(var7, var9, var11);
        float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
        float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
        float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        float var17 = MathHelper.sin(-var5 * 0.017453292F);
        float var18 = var15 * var16;
        float var20 = var14 * var16;
        double var21 = 32.0D;
        Vec3 var23 = var13.addVector(var18 * var21, var17 * var21, var20 * var21);
        return par1World.rayTraceBlocks(var13, var23, par3);
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata, Block block) {
        if (!world.setBlock(x, y, z, block, metadata, 3))
            return false;
        if (ContentHelper.areBlocksEqual(world.getBlock(x, y, z), block)) {
            block.onNeighborBlockChange(world, x, y, z, world.getBlock(x, y, z));
            block.onBlockPlacedBy(world, x, y, z, player, stack);
        }


        return true;
    }



}
