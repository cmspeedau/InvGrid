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
import dev.invgrid.block.BioBurnerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.Nonnull;

/**
 * Bio Burner BE.
 *
 * Design:
 * - Single fuel slot, accepts ORGANIC items only (custom predicate, NOT vanilla
 *   burn time — so logs/coal don't trivialize it and it stays a "bio" niche).
 * - Each organic item yields a small FE amount at a modest FE/t rate.
 * - Tiny internal buffer, pushes FE to adjacent capability holders (panels, hub).
 *
 * TODO (v0.2): register ORGANIC_FUELS map (sapling=1000FE, seeds=500,
 * kelp=800, crops=1200...), energy storage handler, push loop in tick().
 */
public class BioBurnerBlockEntity extends BlockEntity {

    public static final int MAX_ENERGY = 8_000; // small buffer on purpose
    public static final int FE_PER_TICK = 20;   // 20 FE/t while burning — modest

    private final ItemStackHandler fuelSlot = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return isOrganicFuel(stack);
        }
    };

    private int burnTicksRemaining = 0;
    private int totalBurnTicksCurrentItem = 0;

    public BioBurnerBlockEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType<?>) null, pos, state); // TODO: real type in Registration
    }

    public static boolean isOrganicFuel(ItemStack stack) {
        // v0.2: lookup in ORGANIC_FUELS. Placeholder check:
        return stack.is(net.minecraft.tags.ItemTags.SAPLINGS);
    }

    public void tickServer() {
        // TODO: consume fuel slot item -> add burn ticks; burn down; set LIT state;
        // push energy to neighbors via ForgeCapabilities.ENERGY.
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Fuel", fuelSlot.serializeNBT());
        tag.putInt("Burn", burnTicksRemaining);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fuelSlot.deserializeNBT(tag.getCompound("Fuel"));
        burnTicksRemaining = tag.getInt("Burn");
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.empty(); // v0.2: expose fuel slot for hoppers
        }
        return super.getCapability(cap, side);
    }
}
