package dev.invgrid.network.packet;

import dev.invgrid.blockentity.InventoryPanelBlockEntity;
import dev.invgrid.menu.InventoryPanelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * C2S packet: the player clicked a grid item in the panel screen and wants
 * to extract it into their inventory.
 *
 * Layout:
 *   pos   - panel block position (authoritative server-side lookup)
 *   slot  - slot index in the OPEN MENU's grid snapshot (client-side index,
 *           so it maps 1:1 to what the player actually clicked)
 *   all   - true if shift-clicked: extract everything of that item type
 */
public record ExtractFromGridPacket(BlockPos pos, int slot, boolean all)
        implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(InventoryPanelMenu.MODID, "extract_from_grid");

    public static final Type<ExtractFromGridPacket> TYPE =
            new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtractFromGridPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ExtractFromGridPacket::pos,
                    ByteBufCodecs.VAR_INT, ExtractFromGridPacket::slot,
                    ByteBufCodecs.BOOL, ExtractFromGridPacket::all,
                    ExtractFromGridPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
