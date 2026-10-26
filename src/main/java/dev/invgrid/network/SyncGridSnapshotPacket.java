package dev.invgrid.network;

import dev.invgrid.menu.InventoryPanelMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * S2C: syncs a snapshot of the grid's item index into the open
 * InventoryPanelMenu so the client can render grid contents.
 */
public class SyncGridSnapshotPacket {

    private final List<ItemStack> items;

    public SyncGridSnapshotPacket(List<ItemStack> items) {
        this.items = items;
    }

    public static void encode(SyncGridSnapshotPacket pkt, FriendlyByteBuf buf) {
        buf.writeCollection(pkt.items, FriendlyByteBuf::writeItem);
    }

    public static SyncGridSnapshotPacket decode(FriendlyByteBuf buf) {
        List<ItemStack> items = buf.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
        return new SyncGridSnapshotPacket(items);
    }

    public static void handle(SyncGridSnapshotPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Client side only
            if (ctx.get().getSender() != null) return;
            if (ctx.get().get().get().menu instanceof InventoryPanelMenu menu) {
                menu.updateSnapshot(pkt.items);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    /** Helper for the server to push a snapshot to a player with a panel open. */
    public static void sendTo(ServerPlayer player, List<ItemStack> snapshot) {
        ModNetworking.CHANNEL.sendTo(new SyncGridSnapshotPacket(snapshot),
                player.connection.connection, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
    }
}
