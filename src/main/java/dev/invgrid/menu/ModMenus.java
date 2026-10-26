package dev.invgrid.menu;

import dev.invgrid.InvGrid;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, InvGrid.MODID);

    public static final RegistryObject<MenuType<InventoryPanelMenu>> INVENTORY_PANEL =
            MENUS.register("inventory_panel",
                    () -> IForgeMenuType.create(InventoryPanelMenu::clientCreate));
}