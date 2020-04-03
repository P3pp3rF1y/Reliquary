package xreliquary.compat.curios;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;
import xreliquary.client.render.MobCharmBeltLayerRenderer;
import xreliquary.compat.ICompat;
import xreliquary.init.ModItems;
import xreliquary.items.util.IBaubleItem;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Reference;
import xreliquary.util.InventoryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CuriosCompat implements ICompat {
	public CuriosCompat() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::sendImc);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::registerModels));
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SuppressWarnings("squid:S1172") //used in reflection to resolve when this listener should be called
	private void sendImc(InterModEnqueueEvent evt) {
		InterModComms.sendTo(Compatibility.MOD_ID.CURIOS, CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage(IBaubleItem.Type.NECKLACE.getIdentifier()));
		InterModComms.sendTo(Compatibility.MOD_ID.CURIOS, CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage(IBaubleItem.Type.BODY.getIdentifier()));
		InterModComms.sendTo(Compatibility.MOD_ID.CURIOS, CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage(IBaubleItem.Type.BELT.getIdentifier()));
	}

	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> evt) {
		ItemStack stack = evt.getObject();
		Item item = stack.getItem();
		//noinspection ConstantConditions
		if (item.getRegistryName().getNamespace().equals(Reference.MOD_ID) && item instanceof IBaubleItem) {
			evt.addCapability(new ResourceLocation(Reference.MOD_ID, item.getRegistryName().getPath() + "_curios"), new ICapabilityProvider() {
				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
					return CuriosCapability.ITEM.orEmpty(cap, LazyOptional.of(() -> new CuriosBaubleItemWrapper((IBaubleItem) item)));
				}
			});

		}
	}

	@Override
	public void setup() {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> new CuriosFortuneCoinToggler().registerSelf());
		ModItems.MOB_CHARM.setCharmInventoryHandler(new CuriosCharmInventoryHandler());
		//noinspection ConstantConditions
		InventoryHelper.addBaublesItemHandlerFactory((player, type) -> (CuriosAPI.getCuriosHandler(player)
				.map(handler -> handler.getStackHandler(type.getIdentifier())).filter(Objects::nonNull).orElse(new CurioStackHandler())));
	}

	public static Optional<ItemStack> getStackInSlot(LivingEntity entity, String slotName, int slot) {
		return CuriosAPI.getCuriosHandler(entity).map(handler -> Optional.of(handler.getStackHandler(slotName).getStackInSlot(slot)))
				.orElse(Optional.empty());
	}

	public static void setStackInSlot(LivingEntity entity, String slotName, int slot, ItemStack stack) {
		CuriosAPI.getCuriosHandler(entity).ifPresent(handler -> handler.getStackHandler(slotName).setStackInSlot(slot, stack));
	}

	@OnlyIn(Dist.CLIENT)
	private void registerModels(ModelRegistryEvent event) {
		Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
		PlayerRenderer render = skinMap.get("default");
		render.addLayer(new MobCharmBeltLayerRenderer(render) {
			@Override
			protected IItemHandler getBaublesHandler(PlayerEntity player) {
				return CuriosAPI.getCuriosHandler(player).map(h -> h.getStackHandler(IBaubleItem.Type.BELT.getIdentifier())).orElse(new CurioStackHandler());
			}
		});
	}
}
