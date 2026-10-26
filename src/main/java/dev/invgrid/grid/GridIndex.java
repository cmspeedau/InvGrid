package dev.invgrid.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregated view of every item stored across all storage cells in a grid.
 * Rebuilt from cell BEs on demand; the panel snapshot is filtered from this.
 */
public class GridIndex {

    /** item registry name -> aggregated stack (count summed across cells). */
    private final Map<String, ItemStack> byItem = new HashMap<>();

    public void clear() {
        byItem.clear();
    }

    public void addAll(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            String key = stack.getItem().toString();
            ItemStack existing = byItem.get(key);
            if (existing == null) {
                ItemStack copy = stack.copy();
                byItem.put(key, copy);
            } else {
                existing.grow(stack.getCount());
            }
        }
    }

    public List<ItemStack> asList() {
        return new ArrayList<>(byItem.values());
    }

    public int uniqueItems() {
        return byItem.size();
    }
}
