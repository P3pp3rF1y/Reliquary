package xreliquary.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.EntityHelper;
import xreliquary.util.InventoryHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public class ItemAngelheartVial extends ItemBase {

	public ItemAngelheartVial() {
		super(Names.Items.ANGELHEART_VIAL);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		canRepair = false;

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(EntityPlayer player, LivingAttackEvent event) {
				return player.getHealth() <= Math.round(event.getAmount())
					&& InventoryHelper.playerHasItem(player, ModItems.angelheartVial);
			}

			@Override
			public boolean apply(EntityPlayer player, LivingAttackEvent event) {
				decreaseAngelHeartByOne(player);

				// player should see a vial "shatter" effect and hear the glass break to
				// let them know they lost a vial.
				spawnAngelheartVialParticles(player);

				// play some glass breaking effects at the player location
				player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, player.world.rand.nextFloat() * 0.1F + 0.9F);

				// gives the player a few hearts, sparing them from death.
				float amountHealed = player.getMaxHealth() * (float) Settings.Items.AngelHeartVial.healPercentageOfMaxLife / 100F;
				player.setHealth(amountHealed);

				// if the player had any negative status effects [vanilla only for now], remove them:
				if(Settings.Items.AngelHeartVial.removeNegativeStatus)
					EntityHelper.removeNegativeStatusEffects(player);

				return true;
			}

			@Override
			public Priority getPriority() {
				return Priority.LOW;
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack getContainerItem(@Nonnull ItemStack stack) {
		return new ItemStack(ModItems.potion, 1, 0);
	}

	// returns an empty vial when used in crafting recipes.
	@Override
	public boolean hasContainerItem(ItemStack ist) {
		return true;
	}

	private static void spawnAngelheartVialParticles(EntityPlayer player) {
		double var8 = player.posX;
		double var10 = player.posY;
		double var12 = player.posZ;
		Random var7 = player.world.rand;
		for(int var15 = 0; var15 < 8; ++var15) {
			player.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D, Item.getIdFromItem(Items.POTIONITEM));
		}

		// purple, for reals.
		float red = 1.0F;
		float green = 0.0F;
		float blue = 1.0F;

		for(int var20 = 0; var20 < 100; ++var20) {
			double var39 = var7.nextDouble() * 4.0D;
			double var23 = var7.nextDouble() * Math.PI * 2.0D;
			double var25 = Math.cos(var23) * var39;
			double var27 = 0.01D + var7.nextDouble() * 0.5D;
			double var29 = Math.sin(var23) * var39;
			if(player.world.isRemote) {
				Particle var31 = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), var8 + var25 * 0.1D, var10 + 0.3D, var12 + var29 * 0.1D, var25, var27, var29);
				if(var31 != null) {
					float var32 = 0.75F + var7.nextFloat() * 0.25F;
					var31.setRBGColorF(red * var32, green * var32, blue * var32);
					var31.multiplyVelocity((float) var39);
				}
			}
		}

		player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, player.world.rand.nextFloat() * 0.1F + 0.9F);
	}

	private static void decreaseAngelHeartByOne(EntityPlayer player) {
		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if(player.inventory.mainInventory.get(slot).isEmpty())
				continue;
			if(player.inventory.mainInventory.get(slot).getItem() == ModItems.angelheartVial) {
				player.inventory.decrStackSize(slot, 1);
				return;
			}
		}
	}
}
