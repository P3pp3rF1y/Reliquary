package reliquary.compat.curios;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemStackHandler;
import reliquary.client.model.MobCharmBeltModel;
import reliquary.init.ModItems;
import reliquary.items.util.ICuriosItem;
import reliquary.reference.Compatibility;
import reliquary.reference.Reference;
import reliquary.util.InventoryHelper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class CuriosCompat {

	private static final EmptyCuriosHandler EMPTY_HANDLER = new EmptyCuriosHandler();

	public CuriosCompat() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::sendImc);
		modEventBus.addListener(this::setup);
		IEventBus eventBus = MinecraftForge.EVENT_BUS;
		eventBus.addGenericListener(ItemStack.class, this::onAttachCapabilities);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::registerLayerDefinitions));
	}

	private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(MobCharmBeltRenderer.MOB_CHARM_BELT_LAYER, MobCharmBeltModel::createBodyLayer);
		CuriosRendererRegistry.register(ModItems.MOB_CHARM_BELT.get(), MobCharmBeltRenderer::new);
	}

	private void sendImc(InterModEnqueueEvent evt) {
		InterModComms.sendTo(Compatibility.ModIds.CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
		InterModComms.sendTo(Compatibility.ModIds.CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BODY.getMessageBuilder().build());
		InterModComms.sendTo(Compatibility.ModIds.CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
	}

	public void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> evt) {
		ItemStack stack = evt.getObject();
		Item item = stack.getItem();
		if (isCuriosItem(item)) {
			addCuriosCapability(evt, stack);
		}
	}

	private boolean isCuriosItem(Item item) {
		return item.getRegistryName() != null && item.getRegistryName().getNamespace().equals(Reference.MOD_ID) && item instanceof ICuriosItem;
	}

	private void addCuriosCapability(AttachCapabilitiesEvent<ItemStack> evt, ItemStack stack) {
		//noinspection ConstantConditions - item.getRegistryName() isn't null at this point - that is checked in the other method
		evt.addCapability(new ResourceLocation(Reference.MOD_ID, stack.getItem().getRegistryName().getPath() + "_curios"), new ICapabilityProvider() {
			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
				return CuriosCapability.ITEM.orEmpty(cap, LazyOptional.of(() -> new CuriosBaubleItemWrapper(stack)));
			}
		});
	}

	@SuppressWarnings("unused") //event type parameter needed for addListener to know when to call this method
	private void setup(FMLCommonSetupEvent event) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new CuriosFortuneCoinToggler().registerSelf());
		ModItems.MOB_CHARM.get().setCharmInventoryHandler(new CuriosCharmInventoryHandler());
		InventoryHelper.addBaublesItemHandlerFactory((player, type) -> (CuriosApi.getCuriosHelper().getCuriosHandler(player)
				.map(handler -> handler.getStacksHandler(type.getIdentifier()).map(ICurioStacksHandler::getStacks).orElse(EMPTY_HANDLER)).orElse(EMPTY_HANDLER)));
	}

	public static Optional<ItemStack> getStackInSlot(LivingEntity entity, String slotName, int slot) {
		return CuriosApi.getCuriosHelper().getCuriosHandler(entity).map(handler -> handler.getStacksHandler(slotName)
				.map(sh -> sh.getStacks().getStackInSlot(slot))).orElse(Optional.empty());
	}

	public static void setStackInSlot(LivingEntity entity, String slotName, int slot, ItemStack stack) {
		CuriosApi.getCuriosHelper().getCuriosHandler(entity).ifPresent(handler -> handler.getStacksHandler(slotName).ifPresent(sh -> sh.getStacks().setStackInSlot(slot, stack)));
	}

	private static class EmptyCuriosHandler extends ItemStackHandler implements IDynamicStackHandler {
		@Override
		public void setPreviousStackInSlot(int i, @Nonnull ItemStack itemStack) {
			//noop
		}

		@Override
		public ItemStack getPreviousStackInSlot(int i) {
			return ItemStack.EMPTY;
		}

		@Override
		public void grow(int i) {
			//noop
		}

		@Override
		public void shrink(int i) {
			//noop
		}
	}
}
