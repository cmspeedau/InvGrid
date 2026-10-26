package dev.invgrid.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * An InvGrid: a hub plus its connected panels and inventories.
 * v0.1: read-only item index over connected inventories.
 */
public class Grid {
    private final BlockPos hubPos;
    private final List<BlockPos> members = new ArrayList<>();

    public Grid(BlockPos hubPos) {
        this.hubPos = hubPos.immutable();
    }

    /** Flood-fill from the hub through cables/panels/inventories to find members. */
    public void rebuildMembers(ServerLevel level) {
        members.clear();
        // TODO v0.1: BFS over block graph (cable connects to inventories/panels within range limit of 64)
    }

    /** Snapshot all items visible to the grid (read-only). */
    public List<ItemStack> indexItems(ServerLevel level) {
        List<ItemStack> out = new ArrayList<>();
        // TODO v0.1: iterate member inventories via IItemHandler capabilities
        return out;
    }

    public BlockPos getHubPos() {
        return hubPos;
    }
}
