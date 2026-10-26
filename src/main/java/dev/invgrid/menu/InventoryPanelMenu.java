package dev.invgrid.menu;

import dev.invgrid.blockentity.InventoryPanelBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Menu for the Basic Inventory Panel.
 *
 * Layout:
 *  - Slot 0..53   : grid snapshot slots (virtual, read-only until v0.2)
 *  - Slot 54..80  : player inventory
 *  - Slot 81..89  : player hotbar
 */
public class InventoryPanelMenu extends AbstractContainerMenu {

    public static final int GRID_SIZE = 54; // 9x6 grid snapshot
    public static final int PLAYER_INV_START = GRID_SIZE;
    public static final int HOTBAR_START = PLAYER_INV_START + 36;
    public static final int SLOT_COUNT = HOTBAR_START + 9;

    private final InventoryPanelBlockEntity panel;
    private final Player player;

    /** Client-side only: the current snapshot of grid contents. */
    private List<ItemStack> snapshot = List.of();

    /** Client-side only: current search filter text. */
    private String filter = "";

    public InventoryPanelMenu(int id, Inventory playerInv, InventoryPanelBlockEntity panel) {
        super(ModMenus.INVENTORY_PANEL.get(), id);
        this.panel = panel;
        this.player = playerInv.player;

        // Virtual read-only grid snapshot slots; interactions land in v0.2.
        for (int i = 0; i < GRID_SIZE; i++) {
            int x = 8 + (i % 9) * 18;
            int y = 18 + (i / 9) * 18;
            this.addSlot(new Slot(net.minecraft.world.SimpleContainer.EMPTY, i, x, y) {
                @Override
                public boolean mayPlace(ItemStack stack) { return false; }
                @Override
                public boolean mayPickup(Player p) { return false; }
            });
        }

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 198));
        }
    }

    /** Factory used on the client when the server sends the open packet. */
    public static InventoryPanelMenu clientCreate(int id, Inventory inv, FriendlyByteBuf buf) {
        return new InventoryPanelMenu(id, inv, null);
    }

    public void setSnapshot(List<ItemStack> snapshot) {
        this.snapshot = snapshot;
    }

    public List<ItemStack> getSnapshot() {
        return snapshot;
    }

    public void setFilter(String filter) {
        this.filter = filter == null ? "" : filter.toLowerCase();
    }

    public String getFilter() {
        return filter;
    }

    public InventoryPanelBlockEntity getPanel() {
        return panel;
    }

    @Override
    public boolean stillValid(Player p) {
        return panel == null || panel.isStillValid(p);
    }

    @Override
    public ItemStack quickMoveStack(Player p, int index) {
        // v0.1: read-only grid, shift-click does nothing yet.
        return ItemStack.EMPTY;
    }
}