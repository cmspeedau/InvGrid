package dev.invgrid.menu;

import dev.invgrid.InvGridRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

/**
 * Inventory Panel menu. v0.1: player inventory + a grid-list display slot row.
 * Server-side index feeds item ghosts via screen sync in v0.2.
 */
public class InventoryPanelMenu extends AbstractContainerMenu {
    public final BlockPos panelPos;

    public InventoryPanelMenu(int id, Inventory playerInv, BlockPos panelPos) {
        super(InvGridRegistry.PANEL_MENU.get(), id);
        this.panelPos = panelPos.immutable();

        PlayerInvWrapper playerWrapper = new PlayerInvWrapper(playerInv);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new SlotItemHandler(playerWrapper, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new SlotItemHandler(playerWrapper, col, 8 + col * 18, 142));
        }
    }

    public static InventoryPanelMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf buf) {
        return new InventoryPanelMenu(id, inv, buf.readBlockPos());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // TODO v0.2: shift-click transfer
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
