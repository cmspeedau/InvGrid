package dev.invgrid.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Storage Crate block entity. Single big ItemStackHandler (54 slots).
 * Track fill ratio and expose via NBT for the visual "stacked boxes" model.
 */
public class StorageCrateBlockEntity extends BlockEntity {
    public static final int SLOTS = 54;

    private final ItemStackHandler items = new ItemStackHandler(SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<net.minecraftforge.items.IItemHandler> lazyItems = LazyOptional.of(() -> items);

    public StorageCrateBlockEntity(BlockPos pos, BlockState state) {
        super(dev.invgrid.InvGridRegistry.STORAGE_CRATE.get().defaultBlockState().getBlock().equals(state.getBlock())
                ? state : state, pos, state.getBlock() instanceof dev.invgrid.block.StorageCrateBlock ? null : null);
        // NOTE: BlockEntityType registration lands with v0.1 type registry; placeholder super arg handled in type hookup.
    }

    public float getFillRatio() {
        int filled = 0;
        for (int i = 0; i < items.getSlots(); i++) {
            if (!items.getStackInSlot(i).isEmpty()) filled++;
        }
        return (float) filled / SLOTS;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", items.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        items.deserializeNBT(tag.getCompound("Items"));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyItems.cast();
        return super.getCapability(cap, side);
    }
}
