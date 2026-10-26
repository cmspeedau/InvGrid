package dev.invgrid.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Server-side manager for formed InvGrids.
 * Each Grid Hub owns one grid; members are found via cable flood-fill.
 */
public class GridManager {
    private static final Map<BlockPos, Grid> GRIDS = new HashMap<>();

    public static Grid formGrid(ServerLevel level, BlockPos hubPos) {
        Grid grid = new Grid(hubPos);
        grid.rebuildMembers(level);
        GRIDS.put(hubPos.immutable(), grid);
        return grid;
    }

    public static Grid getGrid(BlockPos hubPos) {
        return GRIDS.get(hubPos.immutable());
    }

    public static void destroyGrid(BlockPos hubPos) {
        GRIDS.remove(hubPos.immutable());
    }

    private GridManager() {}
}
