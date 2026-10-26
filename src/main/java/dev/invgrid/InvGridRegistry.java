package dev.invgrid;

import dev.invgrid.block.BioBurnerBlock;
import dev.invgrid.block.GridHubBlock;
import dev.invgrid.block.GridCableBlock;
import dev.invgrid.block.InventoryPanelBlock;
import dev.invgrid.block.StorageCrateBlock;
import dev.invgrid.blockentity.BioBurnerBlockEntity;
import dev.invgrid.item.GridCardItem;
import dev.invgrid.menu.InventoryPanelMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class InvGridRegistry {
    public static final IEventBus MOD_BUS; // set via DistExecutor in main class container

    static {
        MOD_BUS = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, InvGrid.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InvGrid.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, InvGrid.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, InvGrid.MODID);

    public static final RegistryObject<Block> GRID_HUB = BLOCKS.register("grid_hub", GridHubBlock::new);
    public static final RegistryObject<Block> INVGRID_CABLE = BLOCKS.register("invgrid_cable", GridCableBlock::new);
    public static final RegistryObject<Block> INVENTORY_PANEL = BLOCKS.register("inventory_panel", InventoryPanelBlock::new);
    public static final RegistryObject<Block> STORAGE_CRATE = BLOCKS.register("storage_crate", StorageCrateBlock::new);
    public static final RegistryObject<Block> BIO_BURNER = BLOCKS.register("bio_burner", BioBurnerBlock::new);

    public static final RegistryObject<BlockEntityType<BioBurnerBlockEntity>> BIO_BURNER_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("bio_burner",
                    () -> BlockEntityType.Builder.of(BioBurnerBlockEntity::new, BIO_BURNER.get()).build(null));

    public static final RegistryObject<Item> GRID_CARD = ITEMS.register("grid_card",
            () -> new GridCardItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<MenuType<InventoryPanelMenu>> PANEL_MENU =
            MENU_TYPES.register("inventory_panel", () -> IForgeMenuType.create(InventoryPanelMenu::fromNetwork));

    // Register BlockItems
    static {
        ITEMS.register("grid_hub", () -> new BlockItem(GRID_HUB.get(), new Item.Properties()));
        ITEMS.register("invgrid_cable", () -> new BlockItem(INVGRID_CABLE.get(), new Item.Properties()));
        ITEMS.register("inventory_panel", () -> new BlockItem(INVENTORY_PANEL.get(), new Item.Properties()));
        ITEMS.register("storage_crate", () -> new BlockItem(STORAGE_CRATE.get(), new Item.Properties()));
        ITEMS.register("bio_burner", () -> new BlockItem(BIO_BURNER.get(), new Item.Properties()));
    }

    private InvGridRegistry() {}
}
