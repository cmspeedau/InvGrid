package dev.invgrid.block.entity;

import dev.invgrid.grid.GridManager;
import dev.invgrid.network.ModNetworking;
import dev.invgrid.network.SyncGridSnapshotPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Inventory Panel block entity. Owns a GridManager; rebuilds the index on
 * load, on neighbour changes, and on a 10-tick cadence, then pushes a
 * filtered snapshot to the open client menu.
 */
public class InventoryPanelBlockEntity extends BlockEntity {

    private final GridManager gridManager = new GridManager();
    private String filter = "";
    private boolean dirty = true;

    public InventoryPanelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (dirty) {
            gridManager.rebuild(serverLevel, worldPosition);
            dirty = false;
        } else {
            gridManager.rebuildIfStale(serverLevel, worldPosition);
        }
        // Push fresh (filtered) snapshot to any open menu each rebuild cadence.
        SyncGridSnapshotPacket.sendToOpenMenus(this, gridManager.filter(filter));
    }

    public void setFilter(String filter) {
        this.filter = filter == null ? "" : filter.substring(0, Math.min(filter.length(), 64));
    }

    public String getFilter() {
        return filter;
    }

    /** Called on neighbour changed so network changes reflect immediately. */
    public void markDirty() {
        this.dirty = true;
    }

    public GridManager getGridManager() {
        return gridManager;
    }
}
