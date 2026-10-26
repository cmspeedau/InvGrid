package dev.invgrid.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

/** Provider that opens the Inventory Panel menu for a panel at pos. */
public class InventoryPanelMenuProvider implements MenuProvider {
    private final net.minecraft.core.BlockPos pos;

    public InventoryPanelMenuProvider(net.minecraft.core.BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.invgrid.inventory_panel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
        return new InventoryPanelMenu(windowId, inv, pos);
    }
}
