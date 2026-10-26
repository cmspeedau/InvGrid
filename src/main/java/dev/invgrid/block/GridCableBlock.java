package dev.invgrid.block;

import dev.invgrid.grid.IGridMember;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * InvGrid cable: the network's transport block. Joins panels, storage cells,
 * hub and burner into one grid (flood fill traverses it via IGridMember),
 * carries items as part of that grid, and moves Forge Energy between
 * adjacent machines through a small internal buffer.
 */
public class GridCableBlock extends Block implements IGridMember {

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    /** FE stored in one cable segment's buffer. */
    public static final int CABLE_BUFFER = 4096;
    /** Max FE moved per tick in each direction. */
    public static final int MAX_TRANSFER = 1024;

    private static final VoxelShape CORE = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape ARM_N = Block.box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape ARM_S = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape ARM_E = Block.box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape ARM_W = Block.box(0, 6, 6, 6, 10, 10);
    private static final VoxelShape ARM_U = Block.box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape ARM_D = Block.box(6, 0, 6, 10, 6, 10);

    private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new HashMap<>();

    public GridCableBlock() {
        super(Properties.copy(Blocks.IRON_BLOCK)
                .strength(0.8f, 6.0f)
                .noOcclusion()
                .isViewBlocking((s, l, p) -> false));
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    /** True if the neighbor should visually/join connect: any IGridMember or any energy handler. */
    private static boolean connects(LevelReader level, BlockPos pos, Direction dir) {
        BlockState state = level.getBlockState(pos.relative(dir));
        if (state.getBlock() instanceof IGridMember) return true;
        if (level instanceof Level lvl) {
            BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
            return be != null && be.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).isPresent();
        }
        return false;
    }

    private static BlockState withConnections(BlockState state, LevelReader level, BlockPos pos) {
        return state
                .setValue(NORTH, connects(level, pos, Direction.NORTH))
                .setValue(SOUTH, connects(level, pos, Direction.SOUTH))
                .setValue(EAST, connects(level, pos, Direction.EAST))
                .setValue(WEST, connects(level, pos, Direction.WEST))
                .setValue(UP, connects(level, pos, Direction.UP))
                .setValue(DOWN, connects(level, pos, Direction.DOWN));
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext ctx) {
        return withConnections(defaultBlockState(), ctx.getLevel(), ctx.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        boolean connected = neighborState.getBlock() instanceof IGridMember
                || (level instanceof Level lvl
                    && lvl.getBlockEntity(neighborPos) != null
                    && lvl.getBlockEntity(neighborPos).getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).isPresent());
        return state.setValue(propertyFor(dir), connected);
    }

    private static BooleanProperty propertyFor(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH; case SOUTH -> SOUTH; case EAST -> EAST;
            case WEST -> WEST; case UP -> UP; case DOWN -> DOWN;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE_CACHE.computeIfAbsent(state, s -> {
            VoxelShape shape = CORE;
            if (s.getValue(NORTH)) shape = Shapes.or(shape, ARM_N);
            if (s.getValue(SOUTH)) shape = Shapes.or(shape, ARM_S);
            if (s.getValue(EAST)) shape = Shapes.or(shape, ARM_E);
            if (s.getValue(WEST)) shape = Shapes.or(shape, ARM_W);
            if (s.getValue(UP)) shape = Shapes.or(shape, ARM_U);
            if (s.getValue(DOWN)) shape = Shapes.or(shape, ARM_D);
            return shape;
        });
    }

    // ---- Energy transport ----

    @Override
    public boolean hasBlockEntity(BlockState state) { return true; }

    @Override
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new dev.invgrid.blockentity.GridCableBlockEntity(pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void tick(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos,
                     net.minecraft.util.RandomSource random) {
        // Re-evaluate connections when neighbors change.
        BlockState updated = withConnections(state, level, pos);
        if (updated != state) level.setBlock(pos, updated, Block.UPDATE_CLIENTS);

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof dev.invgrid.blockentity.GridCableBlockEntity cable) {
            cable.distributeEnergy();
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // Drop buffered energy is lost with the cable; no item spill needed.
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
