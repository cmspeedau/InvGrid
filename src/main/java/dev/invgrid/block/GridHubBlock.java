package dev.invgrid.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Heart of an InvGrid. On placement, forms a grid via GridManager. */
public class GridHubBlock extends Block {
    public GridHubBlock() {
        super(Properties.of().strength(3.5f).requiresCorrectToolForDrops());
    }

    @Override
    public void onPlace(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            dev.invgrid.grid.GridManager.formGrid(serverLevel, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            dev.invgrid.grid.GridManager.destroyGrid(pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
