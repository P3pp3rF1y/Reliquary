package xreliquary.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import xreliquary.Reliquary;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.EntityHelper;
import xreliquary.util.InventoryHelper;

public class ItemPhoenixDown extends ItemAngelicFeather {

	public ItemPhoenixDown() {
		super(Names.Items.PHOENIX_DOWN);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(EntityPlayer player, LivingAttackEvent event) {
				return event.getSource() == DamageSource.FALL
						&& player.getHealth() > Math.round(event.getAmount())
						&& player.getFoodStats().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.phoenixDown);
			}

			@Override
			public boolean apply(EntityPlayer player, LivingAttackEvent event) {
				float hungerDamage = event.getAmount() * ((float) Settings.Items.PhoenixDown.hungerCostPercent / 100F);
				player.addExhaustion(hungerDamage);
				return true;
			}

			@Override
			public Priority getPriority() {
				return Priority.HIGH;
			}
		});

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(EntityPlayer player, LivingAttackEvent event) {
				return player.getHealth() <= Math.round(event.getAmount())
						&& InventoryHelper.playerHasItem(player, ModItems.phoenixDown);
			}

			@Override
			public boolean apply(EntityPlayer player, LivingAttackEvent event) {
				// item reverts to a normal feather.
				revertPhoenixDownToAngelicFeather(player);

				// gives the player a few hearts, sparing them from death.
				float amountHealed = player.getMaxHealth() * (float) Settings.Items.PhoenixDown.healPercentageOfMaxLife / 100F;
				player.setHealth(amountHealed);

				// if the player had any negative status effects [vanilla only for now], remove them:
				if(Settings.Items.PhoenixDown.removeNegativeStatus)
					EntityHelper.removeNegativeStatusEffects(player);

				// added bonus, has some extra effects when drowning or dying to lava
				if(event.getSource() == DamageSource.LAVA && Settings.Items.PhoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou)
					player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 0));
				if(event.getSource() == DamageSource.DROWN && Settings.Items.PhoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou) {
					player.setAir(10);
					player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 0));
				}

				// give the player temporary resistance to other damages.
				if(Settings.Items.PhoenixDown.giveTemporaryDamageResistance)
					player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 1));

				// give the player temporary regeneration.
				if(Settings.Items.PhoenixDown.giveTemporaryRegeneration)
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 1));

				// particles, lots of them
				spawnPhoenixResurrectionParticles(player);

				return true;
			}

			@Override
			public Priority getPriority() {
				return Priority.LOW;
			}
		});
	}

	private static void revertPhoenixDownToAngelicFeather(EntityPlayer player) {
		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if(player.inventory.mainInventory.get(slot).isEmpty())
				continue;
			if(player.inventory.mainInventory.get(slot).getItem() == ModItems.phoenixDown) {
				player.inventory.mainInventory.set(slot, new ItemStack(ModItems.angelicFeather));
				return;
			}
		}
	}

	private static void spawnPhoenixResurrectionParticles(EntityPlayer player) {
		for(int particles = 0; particles <= 400; particles++) {
			player.world.spawnParticle(EnumParticleTypes.FLAME, player.posX, player.posY, player.posZ, player.world.rand.nextGaussian() * 8, player.world.rand.nextGaussian() * 8, player.world.rand.nextGaussian() * 8);
		}
	}
}
