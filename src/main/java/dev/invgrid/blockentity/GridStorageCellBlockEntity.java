package dev.invgrid.blockentity;

import dev.invgrid.grid.GridExtraction;
import dev.invgrid.grid.GridManager;
import dev.invgrid.grid.IGridStorageCell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A storage cell in the grid. Holds 27 slots, exposes its contents to the
 * grid index, and supports server-side extraction through the panel.
 */
public class GridStorageCellBlockEntity extends BlockEntity implements IGridStorageCell {

    private final ItemStackHandler inventory = new ItemStackHandler(27) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            // grid contents changed -> panels refresh their snapshot
            if (level != null && !level.isClientSide()) {
                markGridDirty();
            }
        }
    };

    public GridStorageCellBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRID_STORAGE_BLOCK_ENTITY.get(), pos, state);
    }

    private void markGridDirty() {
        // walk neighbours to find an owning panel and flag its manager dirty
        for (var dir : net.minecraft.core.Direction.values()) {
            if (level.getBlockEntity(worldPosition.relative(dir)) instanceof InventoryPanelBlockEntity panel) {
                panel.getGridManager().markDirty();
            }
        }
    }

    @Override
    public List<ItemStack> getStoredItems() {
        List<ItemStack> out = new ArrayList<>();
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                out.add(inventory.getStackInSlot(i));
            }
        }
        return out;
    }

    @Override
    public int extractItem(Item item, int count, Consumer<ItemStack> sink) {
        int taken = 0;
        for (int i = 0; i < inventory.getSlots() && taken < count; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.getItem() != item || stack.isEmpty()) continue;

            int amount = Math.min(stack.getCount(), count - taken);
            ItemStack extracted = inventory.extractItem(i, amount, false);
            taken += extracted.getCount();
            sink.accept(extracted);
        }
        return taken;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
    }
}
