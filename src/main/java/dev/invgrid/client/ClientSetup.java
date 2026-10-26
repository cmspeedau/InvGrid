package dev.invgrid.client;

import dev.invgrid.InvGrid;
import dev.invgrid.client.screen.InventoryPanelScreen;
import dev.invgrid.menu.ModMenus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterMenuScreensEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = InvGrid.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.INVENTORY_PANEL.get(), InventoryPanelScreen::new);
    }
}