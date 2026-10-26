package dev.invgrid.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Server-side extraction logic: pulls items out of grid storage cells
 * and gives them to a player (or drops them).
 *
 * Extraction order: nearest-to-panel first, so the player sees items
 * leave the closest storage first (intuitive behaviour, matches EnderIO-ish feel).
 */
public final class GridExtraction {

    private GridExtraction() {}

    /**
     * Try to extract up to {@code count} of the given item from the grid.
     *
     * @param level the server level
     * @param members positions of all cells in the grid (from the last flood fill)
     * @param item the item type to extract
     * @param count max amount to pull (one click = one stack, shift = all of type, capped at 4096)
     * @return everything actually extracted, possibly multiple stacks (empty if nothing found)
     */
    public static List<ItemStack> extract(Level level, List<BlockPos> members, Item item, int count) {
        int remaining = Math.min(count, 4096);
        List<ItemStack> results = new java.util.ArrayList<>();

        for (BlockPos pos : members) {
            if (remaining <= 0) break;
            if (!(level.getBlockEntity(pos) instanceof IGridStorageCell cell)) continue;

            int taken = cell.extractItem(item, remaining, results::add);
            remaining -= taken;
        }
        return results;
    }
}
