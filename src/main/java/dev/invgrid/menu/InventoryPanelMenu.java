package dev.invgrid.menu;

import dev.invgrid.grid.GridExtraction;
import dev.invgrid.grid.GridManager;
import dev.invgrid.item.ModItems;
import dev.invgrid.network.ModNetworking;
import dev.invgrid.network.packet.UpdateFilterPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;\n
public class InventoryPanelMenu extends AbstractContainerMenu {
    public static final String MODID = "invgrid";
    public static final int GRID_SLOT_START = 0;
    public static final int GRID_SLOT_COUNT = 36;
    public static final int INVENTORY_SLOT_START = GRID_SLOT_COUNT;
    public static final int VANILLA_SLOT_COUNT = 36;

    private final Player player;
    private final InventoryPanelBlockEntity panel;

    public InventoryPanelMenu(int containerId, Inventory playerInv, BlockPos pos) {
        super(ModMenus.INVENTORY_PANEL_MENU.get(), containerId);
        this.player = playerInv.player;
        this.panel = (InventoryPanelBlockEntity) playerInv.player.level().getBlockEntity(pos);

        // grid slots (read-only, driven by server snapshot)
        for (int i = 0; i < GRID_SLOT_COUNT; i++) {
            int col = i % 9, row = i / 9;
            addSlot(new GridSnapshotSlot(this, i, 8 + col * 18, 18 + row * 18));
        }
        // player inventory 3x9 + hotbar
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlot(new net.minecraft.world.inventory.Slot(playerInv, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
        for (int c = 0; c < 9; c++)
            addSlot(new net.minecraft.world.inventory.Slot(playerInv, c, 8 + c * 18, 142));
    }

    /** Client-side constructor mirror (registry-driven); filter updates flow through packets. */
    public void sendFilterToServer(String query) {
        if (player.level().isClientSide()) {
            ModNetworking.sendToServer(new UpdateFilterPacket(panelPosition(), query));
        }
    }

    private BlockPos panelPosition() {
        return panel != null ? panel.getBlockPos() : BlockPos.ZERO;
    }

    public void requestExtract(int snapshotIndex, boolean all) {
        if (player.level().isClientSide()) {
            ModNetworking.sendToServer(new dev.invgrid.network.packet.ExtractFromGridPacket(panelPosition(), snapshotIndex, all));
        }
    }

    /** Server: perform extraction for the item at the given snapshot index. */
    public void serverExtract(ServerPlayer serverPlayer, int snapshotIndex, boolean all) {
        if (panel == null || serverPlayer.level().isClientSide()) return;
        if (!stillValid(serverPlayer)) return;

        GridManager mgr = panel.getGridManager();
        ItemStack display = mgr.getItemAt(snapshotIndex); // aggregated display stack
        if (display == null || display.isEmpty()) return;

        int amount = all ? display.getCount() : Math.min(display.getMaxStackSize(), display.getCount());
        var extracted = GridExtraction.extract(
                serverPlayer.level(), mgr.getMemberPositions(), display.getItem(), amount);

        for (ItemStack stack : extracted) {
            if (!serverPlayer.getInventory().add(stack)) {
                serverPlayer.drop(stack, false); // no inventory space: drop at feet
            }
        }

        if (!extracted.isEmpty()) {
            mgr.markDirty(); // contents changed -> re-flood + re-push snapshot
        }
    }

    @Override
    public boolean stillValid(Player p) {
        return panel != null && panel.isRemoved() == false && p.distanceToSqr(panel.getBlockPos().getX(), panel.getBlockPos().getY(), panel.getBlockPos().getZ()) < 64;
    }

    @Override
    public ItemStack quickMoveStack(Player p, int index) {
        // Grid slots are read-only from the client; shift-click on a grid slot
        // is routed as an extract-all request instead of vanilla quick-move.
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canTakeItemForPlaceAll(net.minecraft.world.item.ItemStack stack, net.minecraft.world.inventory.Slot slot) {
        return !(slot instanceof GridSnapshotSlot) && super.canTakeItemForPlaceAll(stack, slot);
    }

    public Player getPlayer() { return player; }
}
