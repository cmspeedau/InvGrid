package dev.invgrid.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * A block entity that stores items and is part of an inventory grid.
 * Implement this to join the grid: the index reads stored items,
 * extraction pulls from you.
 */
public interface IGridStorageCell {

    /** All stored stacks, for index aggregation. Do not modify. */
    List<ItemStack> getStoredItems();

    /**
     * Remove up to {@code count} of the given item from this cell.
     *
     * @param item type to remove
     * @param count max amount
     * @param sink callback receiving each extracted stack (call per stack removed)
     * @return total number of items actually removed
     */
    int extractItem(Item item, int count, Consumer<ItemStack> sink);
}
