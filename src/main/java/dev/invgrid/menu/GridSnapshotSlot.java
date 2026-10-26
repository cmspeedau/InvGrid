package dev.invgrid.menu;

import net.minecraft.world.item.ItemStack;

/**
 * A read-only slot backed by the client-side snapshot list.
 * Items render normally but cannot be taken with vanilla drag/pickup;
 * extraction goes through the ExtractFromGridPacket instead.
 */
public class GridSnapshotSlot extends net.minecraft.world.inventory.Slot {

    private final InventoryPanelMenu menu;
    private final int gridIndex;

    public GridSnapshotSlot(InventoryPanelMenu menu, int gridIndex, int x, int y) {
        super(new net.minecraft.world.inventory.TransientContainer(null), gridIndex, x, y);
        this.menu = menu;
        this.gridIndex = gridIndex;
    }

    public ItemStack getSnapshotStack() {
        var snap = menu.getSnapshot();
        if (gridIndex < snap.size()) return snap.get(gridIndex);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem() {
        return getSnapshotStack();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(net.minecraft.world.entity.player.Player player) {
        return false;
    }

    @Override
    public void set(ItemStack stack) {
        // no-op: snapshot is server-driven
    }
}
