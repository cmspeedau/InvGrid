package dev.invgrid.grid;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Implemented by grid storage cell block entities so GridIndex can read them. */
public interface IGridStorageCell {
    List<ItemStack> getStoredItems();
}
