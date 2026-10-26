package dev.invgrid.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Flood-fills from a panel through adjacent grid blocks (panels, grid storage
 * cells, hub) to discover all connected members of one grid network.
 * Bounded by MAX_MEMBERS so a runaway loop can't hang the server.
 */
public final class GridFloodFill {

    /** Hard cap on network size; matches the planned Elite-tier wireless range story. */
    public static final int MAX_MEMBERS = 4096;

    private GridFloodFill() {}

    public static List<BlockPos> discover(ServerLevel level, BlockPos origin) {
        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> members = new ArrayList<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();

        if (!isGridMember(level, origin)) return members;

        queue.add(origin.immutable());
        visited.add(origin.immutable());

        while (!queue.isEmpty() && members.size() < MAX_MEMBERS) {
            BlockPos pos = queue.poll();
            members.add(pos);

            for (Direction dir : Direction.values()) {
                BlockPos next = pos.relative(dir);
                if (visited.add(next) && isGridMember(level, next)) {
                    queue.add(next.immutable());
                }
            }
        }
        return members;
    }

    /** Grid members: panels, storage cells, and the hub. Extend as block types land. */
    public static boolean isGridMember(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof IGridMember;
    }
}
