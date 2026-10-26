package dev.invgrid.item;

import net.minecraft.world.item.Item;

/**
 * Grid Card — right-click a Grid Hub to bind, then right-click a panel/remote to link it.
 * Links are stored in the item's NBT ("Hub" -> BlockPos).
 */
public class GridCardItem extends Item {
    public GridCardItem(Properties properties) {
        super(properties);
    }
    // TODO v0.3: hub binding via useOn() + card NBT
}
