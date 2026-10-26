package dev.invgrid.menu;

import dev.invgrid.network.SyncGridSnapshotPacket;
import dev.invgrid.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryPanelMenu extends AbstractContainerMenu {

    public static final int GRID_COLS = 9;
    public static final int GRID_ROWS = 6;
    public static final int PLAYER_INV_X = 8;
    public static final int PLAYER_INV_Y = 148;

    /** Client-side mirror of the grid contents, filtered by the search box. */
    private final List<ItemStack> snapshot = new ArrayList<>();

    private String filter = "";

    public InventoryPanelMenu(int windowId, Inventory playerInv, FriendlyByteBuf data) {
        this(windowId, playerInv);
    }

    public InventoryPanelMenu(int windowId, Inventory playerInv) {
        super(ModMenus.INVENTORY_PANEL.get(), windowId);

        // 54 virtual, read-only grid slots (8px gap in bg between search and grid)
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int index = row * GRID_COLS + col;
                this.slots.add(new GridSnapshotSlot(this, index, 8 + col * 18, 30 + row * 18));
            }
        }

        // Player inventory + hotbar
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new net.minecraft.world.inventory.Slot(playerInv,
                        9 + row * 9 + col, PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18));
        for (int col = 0; col < 9; col++)
            this.addSlot(new net.minecraft.world.inventory.Slot(playerInv, col,
                    PLAYER_INV_X + col * 18, PLAYER_INV_Y + 58));
    }

    /** Server: push a fresh (filtered) snapshot to the viewing player. */
    public void pushSnapshot(ServerPlayer player, List<ItemStack> gridItems) {
        List<ItemStack> filtered = gridItems.stream()
                .filter(s -> filter.isEmpty()
                        || s.getHoverName().getString().toLowerCase().contains(filter.toLowerCase()))
                .toList();
        SyncGridSnapshotPacket.sendTo(player, filtered);
    }

    public void updateSnapshot(List<ItemStack> items) {
        snapshot.clear();
        snapshot.addAll(items);
    }

    public void setFilter(String query) {
        this.filter = query == null ? "" : query;
        // Server hook: in v0.2 this triggers GridManager.reindexAndPush(owner)
    }

    public String getFilter() {
        return filter;
    }

    public List<ItemStack> getSnapshot() {
        return snapshot;
    }

    /** Server-side extraction. Returns empty until grid indexing lands (v0.2). */
    public Optional<ItemStack> tryExtract(ServerPlayer player, int slotIndex, boolean shift) {
        if (slotIndex < 0 || slotIndex >= GRID_COLS * GRID_ROWS) return Optional.empty();
        // TODO v0.2: resolve grid via GridManager, pull stack, update snapshot.
        return Optional.empty();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // shift-click handled by extract packet, not slot logic
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(0, 0, 0) < 1e7 || !player.level().isClientSide; // grid range check lands in v0.2
    }
}
