package dev.invgrid.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Server-side grid manager for one panel BE: discovers the connected network
 * via flood-fill, aggregates contents into a GridIndex, and applies the
 * panel's search filter to produce the snapshot the client sees.
 */
public class GridManager {

    private final GridIndex index = new GridIndex();
    private long lastBuild;
    private static final long REBUILD_INTERVAL_TICKS = 10;

    /** Rebuilds the index if stale, then returns the (unfiltered) grid contents. */
    public List<ItemStack> getContents(ServerLevel level, BlockPos panelPos) {
        rebuildIfStale(level, panelPos);
        return index.asList();
    }

    public void rebuildIfStale(ServerLevel level, BlockPos panelPos) {
        long now = level.getGameTime();
        if (now - lastBuild < REBUILD_INTERVAL_TICKS) return;
        lastBuild = now;
        rebuild(level, panelPos);
    }

    public void rebuild(ServerLevel level, BlockPos panelPos) {
        index.clear();
        List<BlockPos> members = GridFloodFill.discover(level, panelPos);
        for (BlockPos pos : members) {
            if (level.getBlockEntity(pos) instanceof IGridStorageCell cell) {
                index.addAll(cell.getStoredItems());
            }
        }
    }

    /** Applies a lowercase-contains filter to the current index contents. */
    public List<ItemStack> filter(String query) {
        List<ItemStack> all = index.asList();
        if (query == null || query.isBlank()) return all;
        String q = query.toLowerCase();
        return all.stream()
                .filter(s -> s.getHoverName().getString().toLowerCase().contains(q)
                        || s.getItem().toString().contains(q))
                .toList();
    }

    public int uniqueItems() {
        return index.uniqueItems();
    }
}
