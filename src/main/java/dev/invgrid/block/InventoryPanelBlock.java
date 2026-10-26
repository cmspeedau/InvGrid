package dev.invgrid.block;

import dev.invgrid.menu.InventoryPanelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Inventory Panel — opens the grid view GUI. */
public class InventoryPanelBlock extends Block {
    public InventoryPanelBlock() {
        super(Properties.of().strength(2.5f));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new dev.invgrid.menu.InventoryPanelMenuProvider(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
