package dev.invgrid;

import net.minecraftforge.fml.common.Mod;

@Mod(InvGrid.MODID)
public class InvGrid {
    public static final String MODID = "invgrid";

    public InvGrid() {
        InvGridRegistry.BLOCKS.register(InvGridRegistry.MOD_BUS);
        InvGridRegistry.ITEMS.register(InvGridRegistry.MOD_BUS);
        InvGridRegistry.MENU_TYPES.register(InvGridRegistry.MOD_BUS);
    }
}
