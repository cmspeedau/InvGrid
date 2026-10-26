package dev.invgrid.network;

import dev.invgrid.block.entity.InventoryPanelBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Server helper: sends the current filtered snapshot to every player whose
 * open container menu is bound to this panel. Called from the panel BE tick.
 */
public final class SyncGridSnapshotPacket {

    private SyncGridSnapshotPacket() {}

    public static void sendToOpenMenus(InventoryPanelBlockEntity panel, List<ItemStack> snapshot) {
        for (ServerPlayer player : panel.getLevel().players()) {
            if (player.containerMenu instanceof dev.invgrid.menu.InventoryPanelMenu menu
                    && menu.matchesPanel(panel)) {
                ModNetworking.sendToPlayer(new SyncGridSnapshotPayload(snapshot), player);
            }
        }
    }
}
