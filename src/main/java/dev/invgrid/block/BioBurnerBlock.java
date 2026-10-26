package dev.invgrid.block;

import dev.invgrid.blockentity.BioBurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Bio Burner — InvGrid's cheap early-game FE generator.
 * Burns organic items (saplings, seeds, crops, leaves) for small amounts of FE.
 * Crafted from planks + a log + gold plates — deliberately near-free so the grid
 * is usable from minute one.
 *
 * TODO (v0.2): FE capability exposure, organic fuel table, burning tick logic,
 * lit state particle/light, GUI with fuel progress.
 */
public class BioBurnerBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 14, 14);

    public BioBurnerBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.LIT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BioBurnerBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(BlockStateProperties.LIT)) {
            // TODO: smoke/flame particles above the burner
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true; // comparator reads remaining burn time — nice for automation
    }
}
