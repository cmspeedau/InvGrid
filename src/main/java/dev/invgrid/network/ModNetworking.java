package dev.invgrid.network;

import dev.invgrid.InvGrid;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(InvGrid.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, SyncGridSnapshotPacket.class,
                SyncGridSnapshotPacket::encode, SyncGridSnapshotPacket::decode,
                SyncGridSnapshotPacket::handle);
        CHANNEL.registerMessage(packetId++, ExtractFromGridPacket.class,
                ExtractFromGridPacket::encode, ExtractFromGridPacket::decode,
                ExtractFromGridPacket::handle);
        CHANNEL.registerMessage(packetId++, UpdateFilterPacket.class,
                UpdateFilterPacket::encode, UpdateFilterPacket::decode,
                UpdateFilterPacket::handle);
    }
}
