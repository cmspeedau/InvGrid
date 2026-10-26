package dev.invgrid.network;

import dev.invgrid.menu.InventoryPanelMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * C2S: search box text changed; server re-filters the snapshot and
 * echoes back an updated SyncGridSnapshotPacket.
 */
public class UpdateFilterPacket {

    private final String query;

    public UpdateFilterPacket(String query) {
        this.query = query;
    }

    public static void encode(UpdateFilterPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.query, 64);
    }

    public static UpdateFilterPacket decode(FriendlyByteBuf buf) {
        return new UpdateFilterPacket(buf.readUtf(64));
    }

    public static void handle(UpdateFilterPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {
            if (player == null) return;
            if (player.containerMenu instanceof InventoryPanelMenu menu) {
                menu.setFilter(pkt.query); // triggers fresh snapshot push
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
