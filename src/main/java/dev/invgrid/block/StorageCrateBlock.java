package dev.invgrid.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Storage Crate — InvGrid's own storage block.
 * Visual fill: block model renders stacked "boxes" layers as fill % increases (0-4 layers).
 */
public class StorageCrateBlock extends Block implements EntityBlock {
    public StorageCrateBlock() {
        super(Properties.of().strength(2.0f));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new dev.invgrid.blockentity.StorageCrateBlockEntity(pos, state);
    }
}
