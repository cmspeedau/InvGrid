package dev.invgrid.block;

import dev.invgrid.InvGridRegistry;
import dev.invgrid.blockentity.BioBurnerBlockEntity;
import dev.invgrid.grid.IGridMember;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Bio Burner: the grid's generator. Burns furnace fuels from its single input
 * slot and produces Forge Energy, pushing it into adjacent energy consumers
 * and GridCable segments each tick. Implements IGridMember so the flood fill
 * treats it as part of the network.
 */
public class BioBurnerBlock extends BaseEntityBlock implements IGridMember {

    public BioBurnerBlock(Properties props) {
        super(props);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BioBurnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        if (type != InvGridRegistry.BIO_BURNER_BLOCK_ENTITY.get()) return null;
        return (BlockEntityTicker<T>) (BlockEntityTicker<BioBurnerBlockEntity>) BioBurnerBlockEntity::serverTick;
    }

    /** Drops the fuel contents on break. */
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BioBurnerBlockEntity burner) {
                burner.dropContents(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
