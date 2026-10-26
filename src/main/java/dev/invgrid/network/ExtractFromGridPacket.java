package dev.invgrid.network;

import dev.invgrid.menu.InventoryPanelMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * C2S: player requested extraction of the item in the given grid slot.
 * shift = true means extract a full stack (or as much as possible).
 */
public class ExtractFromGridPacket {

    private final int slotIndex;
    private final boolean shift;

    public ExtractFromGridPacket(int slotIndex, boolean shift) {
        this.slotIndex = slotIndex;
        this.shift = shift;
    }

    public static void encode(ExtractFromGridPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.slotIndex);
        buf.writeBoolean(pkt.shift);
    }

    public static ExtractFromGridPacket decode(FriendlyByteBuf buf) {
        return new ExtractFromGridPacket(buf.readVarInt(), buf.readBoolean());
    }

    public static void handle(ExtractFromGridPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {
            if (player == null) return;
            if (player.containerMenu instanceof InventoryPanelMenu menu) {
                // TODO v0.2: hook into GridManager index. Stub for now —
                // basic panels are read-only until grid indexing lands.
                menu.tryExtract(player, pkt.slotIndex, pkt.shift)
                        .ifPresent(stack -> player.getInventory().add(stack));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
