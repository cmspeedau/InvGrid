package dev.invgrid.blockentity;

import dev.invgrid.InvGridRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Generator block entity: burns fuel items and converts them to Forge Energy.
 * Each tick it:
 *  1. If not burning, pulls the next burnable item from the input slot and
 *     starts a burn cycle (full furnace burn-time, FE_PER_TICK FE/tick).
 *  2. Pushes buffered energy to any adjacent IEnergyStorage that accepts FE
 *     (consumers or GridCable segments).
 */
public class BioBurnerBlockEntity extends BlockEntity {

    public static final int CAPACITY = 20_000;   // FE buffered
    public static final int MAX_OUTPUT = 512;    // FE/tick pushed out per side
    public static final int FE_PER_TICK = 40;    // generation rate while burning

    private final ItemStackHandler inventory = new ItemStackHandler(1);
    private final EnergyStorage energy = new EnergyStorage(CAPACITY, MAX_OUTPUT, 0);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energy);

    private int burnTimeRemaining;   // ticks of fuel left
    private int burnTimeTotal;       // total ticks of current fuel item

    public BioBurnerBlockEntity(BlockPos pos, BlockState state) {
        super(InvGridRegistry.BIO_BURNER_BLOCK_ENTITY.get(), pos, state);
    }

    /** Called from the block's ticker on the server each tick. */
    public static void serverTick(Level level, BlockPos pos, BlockState state, BioBurnerBlockEntity be) {
        be.tryConsumeFuel();
        be.generate();
        be.pushEnergy();
        be.setChanged();
    }

    private void tryConsumeFuel() {
        if (burnTimeRemaining > 0) return;
        ItemStack stack = inventory.getStackInSlot(0);
        int burnTicks = ForgeHooks.getBurnTime(stack, null);
        if (burnTicks <= 0) return;

        burnTimeTotal = burnTicks;
        burnTimeRemaining = burnTicks;
        inventory.extractItem(0, 1, false);
    }

    private void generate() {
        if (burnTimeRemaining <= 0) return;
        burnTimeRemaining--;
        // Overflow is dropped (EnergyStorage receive caps at capacity).
        energy.receiveEnergy(FE_PER_TICK, false);
    }

    private void pushEnergy() {
        if (energy.getEnergyStored() == 0 || level == null) return;

        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() == 0) break;

            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
            if (neighbor == null) continue;
            LazyOptional<IEnergyStorage> cap =
                    neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite());
            cap.ifPresent(target -> {
                int sent = energy.extractEnergy(MAX_OUTPUT, true);
                if (sent > 0) {
                    int accepted = target.receiveEnergy(sent, false);
                    energy.extractEnergy(accepted, false);
                }
            });
        }
    }

    /** Drops the fuel slot on break. */
    public void dropContents(Level level, BlockPos pos) {
        SimpleContainer box = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            box.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(level, pos, box);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("BurnRemaining", burnTimeRemaining);
        tag.putInt("BurnTotal", burnTimeTotal);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) inventory.deserializeNBT(tag.getCompound("Inventory"));
        int stored = tag.getInt("Energy");
        if (stored > 0) energy.receiveEnergy(stored, false);
        burnTimeRemaining = tag.getInt("BurnRemaining");
        burnTimeTotal = tag.getInt("BurnTotal");
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return energyOptional.cast();
        return super.getCapability(cap, side);
    }
}
