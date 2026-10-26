package dev.invgrid.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Cable block entity: a tiny energy buffer that pulls FE from
 * adjacent producers and pushes it into adjacent consumers each tick.
 * Item transport needs no BE — cables join the item grid via
 * {@code IGridMember}, so the flood fill carries item flow through them.
 */
public class GridCableBlockEntity extends BlockEntity {

    private final EnergyStorage buffer = new EnergyStorage(
            dev.invgrid.block.GridCableBlock.CABLE_BUFFER,
            dev.invgrid.block.GridCableBlock.MAX_TRANSFER);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> buffer);

    public GridCableBlockEntity(BlockPos pos, BlockState state) {
        super(type(), pos, state);
    }

    private static BlockEntityType<GridCableBlockEntity> type() {
        return dev.invgrid.InvGridRegistry.CABLE_BLOCK_ENTITY.get();
    }

    /** Pull from producers, push to consumers, once per scheduled tick. */
    public void distributeEnergy() {
        if (level == null || level.isClientSide) return;

        Map<Direction, IEnergyStorage> consumers = new HashMap<>();
        Map<Direction, IEnergyStorage> producers = new HashMap<>();

        for (Direction dir : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
            if (neighbor == null) continue;
            neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite())
                    .ifPresent(storage -> {
                        if (storage instanceof GridCableBlockEntity == false) {
                            if (storage.canExtract()) producers.put(dir, storage);
                            if (storage.canReceive()) consumers.put(dir, storage);
                        }
                    });
        }

        // Pull from producers into the buffer.
        for (IEnergyStorage source : producers.values()) {
            int space = dev.invgrid.block.GridCableBlock.CABLE_BUFFER - buffer.getEnergyStored();
            if (space <= 0) break;
            int pulled = source.extractEnergy(
                    Math.min(dev.invgrid.block.GridCableBlock.MAX_TRANSFER, space), false);
            if (pulled > 0) buffer.receiveEnergy(pulled, false);
        }

        // Push from the buffer to consumers.
        if (buffer.getEnergyStored() > 0) {
            for (IEnergyStorage sink : consumers.values()) {
                int sent = sink.receiveEnergy(
                        Math.min(dev.invgrid.block.GridCableBlock.MAX_TRANSFER, buffer.getEnergyStored()), false);
                if (sent > 0) buffer.extractEnergy(sent, false);
                if (buffer.getEnergyStored() == 0) break;
            }
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return energyOptional.cast();
        return super.getCapability(cap, side);
    }
}
