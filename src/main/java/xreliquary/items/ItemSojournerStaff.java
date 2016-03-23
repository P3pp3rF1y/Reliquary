package xreliquary.items;


import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import java.util.ArrayList;
import java.util.List;


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
        return EnumRarity.EPIC;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        if (world.isRemote)
            return;

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
        if (entityLiving.worldObj.isRemote)
            return false;
        if (entityLiving.isSneaking()) {
            cycleTorchMode(ist);
            return true;
        }
        return false;
    }

    private void scanForMatchingTorchesToFillInternalStorage(ItemStack ist, EntityPlayer player) {
        List<String> torches = Settings.SojournerStaff.torches;
        List<Item> items = new ArrayList<Item>();

        //default to always work with vanilla torches
        ItemStack vanillaTorch = new ItemStack(Blocks.torch, 1, 0);
        items.add(vanillaTorch.getItem());

        for (String torch : torches) {
            if (!items.contains(RegistryHelper.getItemFromName(torch)))
                items.add(RegistryHelper.getItemFromName(torch));
        }

        for (Item item : items) {
            if (!isInternalStorageFullOfItem(ist, item) && InventoryHelper.consumeItem(item, player)) {
                addItemToInternalStorage(ist, item);
            }
        }
    }

    private void addItemToInternalStorage(ItemStack ist, Item item) {
        NBTTagCompound tagCompound = NBTHelper.getTag(ist);
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
            if (itemName.equals(RegistryHelper.getItemRegistryName(item))) {
                int quantity = tagItemData.getInteger("Quantity");
                tagItemData.setInteger("Quantity", quantity + 1);
                added = true;
            }
        }
        if (!added) {
            NBTTagCompound newTagData = new NBTTagCompound();
            newTagData.setString("Name", RegistryHelper.getItemRegistryName(item));
            newTagData.setInteger("Quantity", 1);
            tagList.appendTag(newTagData);
        }

        tagCompound.setTag("Items", tagList);

        NBTHelper.setTag(ist, tagCompound);
    }

    private boolean hasItemInInternalStorage(ItemStack ist, Item item, int cost) {
        NBTTagCompound tagCompound = NBTHelper.getTag(ist);
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
            if (itemName.equals(RegistryHelper.getItemRegistryName(item))) {
                int quantity = tagItemData.getInteger("Quantity");
                return quantity >= cost;
            }
        }

        return false;
    }

    private boolean isInternalStorageFullOfItem(ItemStack ist, Item item) {
        if (hasItemInInternalStorage(ist, item, 1)) {
            NBTTagCompound tagCompound = NBTHelper.getTag(ist);
            NBTTagList tagList = tagCompound.getTagList("Items", 10);

            for (int i = 0; i < tagList.tagCount(); ++i)
            {
                NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
                String itemName = tagItemData.getString("Name");
                if (itemName.equals(RegistryHelper.getItemRegistryName(item))) {
                    int quantity = tagItemData.getInteger("Quantity");
                    return quantity >= getTorchItemMaxCapacity();
                }
            }
        }
        return false;
    }

    public String getTorchPlacementMode(ItemStack ist) {
        if (NBTHelper.getTag(ist) == null) {
            return null;
        }

        NBTTagCompound tagCompound = NBTHelper.getTag(ist);
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

    public int getTorchCount(ItemStack ist) {
        if (NBTHelper.getTag(ist) == null) {
            return 0;
        }

        NBTTagCompound tagCompound = NBTHelper.getTag(ist);
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
                    else
                        return quantity;
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
                    return quantity;
                }
            }
        }
        return 0;
    }

    private void cycleTorchMode(ItemStack ist) {
        String mode = getTorchPlacementMode(ist);
        if (mode == null || mode.isEmpty())
            return;

        NBTTagCompound tagCompound = NBTHelper.getTag(ist);

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
        return Settings.SojournerStaff.maxCapacityPerItemType;
    }

    public boolean removeItemFromInternalStorage(ItemStack ist, Item item, int cost, EntityPlayer player) {
        if (player.capabilities.isCreativeMode)
            return true;
        if (hasItemInInternalStorage(ist, item, cost)) {
            NBTTagCompound tagCompound = NBTHelper.getTag(ist);

            NBTTagList tagList = tagCompound.getTagList("Items", 10);

            NBTTagList replacementTagList = new NBTTagList();

            for (int i = 0; i < tagList.tagCount(); ++i)
            {
                NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
                String itemName = tagItemData.getString("Name");
                if (itemName.equals(RegistryHelper.getItemRegistryName(item))) {
                    int quantity = tagItemData.getInteger("Quantity");
                    tagItemData.setInteger("Quantity", quantity - cost);
                }
                replacementTagList.appendTag(tagItemData);
            }
            tagCompound.setTag("Items", replacementTagList);
            NBTHelper.setTag(ist, tagCompound);
            return true;
        }
        return false;

    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean par4) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            return;
        //maps the contents of the Sojourner's staff to a tooltip, so the player can review the torches stored within.
        String phrase = "Nothing.";
        String placing = "Nothing.";
        NBTTagCompound tagCompound = NBTHelper.getTag(ist);
        if (tagCompound != null) {
            NBTTagList tagList = tagCompound.getTagList("Items", 10);
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
                String itemName = tagItemData.getString("Name");
                Item containedItem = RegistryHelper.getItemFromName(itemName);
                int quantity = tagItemData.getInteger("Quantity");
                phrase = String.format("%s%s", phrase.equals("Nothing.") ? "" : String.format("%s;", phrase), new ItemStack(containedItem, 1, 0).getDisplayName() + ": " + quantity);
            }

            //add "currently placing: blah blah blah" to the tooltip.
            Item placingItem = RegistryHelper.getItemFromName(getTorchPlacementMode(ist));

            if (placingItem != null) {
                placing = new ItemStack(placingItem, 1, 0).getDisplayName();
            }
        }
        this.formatTooltip(ImmutableMap.of("phrase", phrase, "placing", placing), ist, list);
        if(this.isEnabled(ist))
            LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + getItemStackDisplayName(new ItemStack(Blocks.torch))), ist, list);
        LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
    }


    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float xOff, float yOff, float zOff) {
        if (player.isSwingInProgress)
            return false;
        player.swingItem();
        if (world.isRemote)
            return false;
        if (!player.canPlayerEdit(pos, side, ist))
            return false;
        if (player.isSneaking())
            return false;
        if (getTorchPlacementMode(ist) == null)
            return false;
        Block blockAttemptingPlacement = Block.getBlockFromName(getTorchPlacementMode(ist));
        if (blockAttemptingPlacement == null)
            return false;

        Block blockTargetted = world.getBlockState(pos).getBlock();
        BlockPos placeBlockAt = pos;

        if (RegistryHelper.blocksEqual(blockTargetted, Blocks.snow)) {
            side = EnumFacing.UP;
        } else if (!RegistryHelper.blocksEqual(blockTargetted, Blocks.vine) && !RegistryHelper.blocksEqual(blockTargetted, Blocks.tallgrass) && !RegistryHelper.blocksEqual(blockTargetted, Blocks.deadbush) && (blockTargetted == null || !blockTargetted.isReplaceable(world, pos))) {
            placeBlockAt = pos.offset(side);
        }

        if (blockAttemptingPlacement.canPlaceBlockAt(world, placeBlockAt)) {
            if (world.canBlockBePlaced(blockAttemptingPlacement, placeBlockAt, false, side, player, ist)) {
                if (!player.capabilities.isCreativeMode) {
                    int cost = 1;
                    int distance = (int) player.getDistance(placeBlockAt.getX(), placeBlockAt.getY(), placeBlockAt.getZ());
                    for (; distance > Settings.SojournerStaff.tilePerCostMultiplier; distance -= Settings.SojournerStaff.tilePerCostMultiplier) {
                        cost++;
                    }
                    if (!removeItemFromInternalStorage(ist, Item.getItemFromBlock(blockAttemptingPlacement), cost, player))
                        return false;
                }
                IBlockState torchBlockState = attemptSide(world, placeBlockAt, side, blockAttemptingPlacement, player);
                if (placeBlockAt(ist, player, world, placeBlockAt, torchBlockState)) {
                    blockAttemptingPlacement.onBlockAdded(world, placeBlockAt, torchBlockState);
                    double gauss = 0.5D + world.rand.nextFloat() / 2;
                    world.spawnParticle(EnumParticleTypes.SPELL_MOB, placeBlockAt.getX() + 0.5D, placeBlockAt.getY() + 0.5D, placeBlockAt.getZ() + 0.5D, gauss, gauss, 0.0F);
                    world.playSoundEffect(placeBlockAt.getX() + 0.5F, placeBlockAt.getY() + 0.5F, placeBlockAt.getZ() + 0.5F, blockAttemptingPlacement.stepSound.getStepSound(), (blockAttemptingPlacement.stepSound.getVolume() + 1.0F) / 2.0F, blockAttemptingPlacement.stepSound.getFrequency() * 0.8F);
                }
            }
        }
        return true;
    }

    private IBlockState attemptSide(World world, BlockPos pos, EnumFacing side, Block block, EntityPlayer player) {
        return block.onBlockPlaced(world, pos, side, pos.getX(), pos.getY(), pos.getZ(), 0, player);
    }



    //a longer ranged version of "getMovingObjectPositionFromPlayer" basically
    public RayTraceResult getBlockTarget(World world, EntityPlayer player) {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + (double)(world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
        Vec3d vec3 = new Vec3d(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = Settings.SojournerStaff.maxRange;
        Vec3d vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return world.rayTraceBlocks(vec3, vec31, true, false, false);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World world, EntityPlayer player, EnumHand hand) {
        //calls onItemUse so all of the functionality we'd normally have to do preventative checks on gets handled there.
        if (!player.isSneaking()) {
            RayTraceResult mop = this.getBlockTarget(world, player);
            if (mop != null && mop.typeOfHit == RayTraceResult.MovingObjectType.BLOCK) {
                float xOff = (float) (mop.getBlockPos().getX() - player.posX);
                float yOff = (float) (mop.getBlockPos().getY() - player.posY);
                float zOff = (float) (mop.getBlockPos().getZ() - player.posZ);
                this.onItemUse(ist, player, world, mop.getBlockPos(), mop.sideHit, xOff, yOff, zOff);
            }
        }
        return super.onItemRightClick(ist, world, player);
    }

    //I named the vars in this method weird crap cos I have no idea what they do. This was stolen from the bucket code, I think.
    @Override
    protected RayTraceResult getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean weirdBucketBoolean) {
        float movementCoefficient = 1.0F;
        float pitchOff = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * movementCoefficient;
        float yawOff = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * movementCoefficient;
        double xOff = player.prevPosX + (player.posX - player.prevPosX) * movementCoefficient;
        double yOff = player.prevPosY + (player.posY - player.prevPosY) * movementCoefficient + player.getEyeHeight();
        double zOff = player.prevPosZ + (player.posZ - player.prevPosZ) * movementCoefficient;
        Vec3d playerVector = new Vec3d(xOff, yOff, zOff);
        float cosTraceYaw = MathHelper.cos(-yawOff * 0.017453292F - (float) Math.PI);
        float sinTraceYaw = MathHelper.sin(-yawOff * 0.017453292F - (float) Math.PI);
        float cosTracePitch = -MathHelper.cos(-pitchOff * 0.017453292F);
        float sinTracePitch = MathHelper.sin(-pitchOff * 0.017453292F);
        float pythagoraStuff = sinTraceYaw * cosTracePitch;
        float pythagoraStuff2 = cosTraceYaw * cosTracePitch;
        double distCoeff = 32.0D;
        Vec3d rayTraceVector = playerVector.addVector(pythagoraStuff * distCoeff, sinTracePitch * distCoeff, pythagoraStuff2 * distCoeff);
        return world.rayTraceBlocks(playerVector, rayTraceVector, weirdBucketBoolean);
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos,IBlockState torchBlockState) {
        if (!world.setBlockState(pos, torchBlockState, 3))
            return false;

        if (RegistryHelper.blocksEqual(torchBlockState.getBlock(), Blocks.torch)) {
            Blocks.torch.onNeighborBlockChange(world, pos, torchBlockState, torchBlockState.getBlock());
            Blocks.torch.onBlockPlacedBy(world, pos, torchBlockState, player, stack);
        }

        return true;
    }
}
