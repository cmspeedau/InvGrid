package com.invgrid.client;

import com.invgrid.common.blockentity.InventoryPanelBlockEntity;
import com.invgrid.common.menu.InventoryPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client GUI for the Inventory Panel, deliberately mimicking Ender IO's
 * Inventory Panel: a compact wall-mounted screen with a single search box
 * on top and a plain scrollable grid of item icons with count labels.
 * No decorative chrome - just a dark bordered frame around the grid.
 */
public class InventoryPanelScreen extends AbstractContainerScreen<InventoryPanelMenu> {

    private static final int GUI_WIDTH = 195;
    private static final int GUI_HEIGHT = 222;

    /** Size of one item slot cell in the grid, in pixels. */
    private static final int CELL = 18;
    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 6;

    private static final int SEARCH_X = 8;
    private static final int SEARCH_Y = 6;
    private static final int SEARCH_W = GUI_WIDTH - 16;
    private static final int SEARCH_H = 12;

    private static final int GRID_X = 8;
    private static final int GRID_Y = 24;

    private EditBox searchBox;

    public InventoryPanelScreen(InventoryPanelMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        this.searchBox = new EditBox(this.font,
                this.leftPos + SEARCH_X, this.topPos + SEARCH_Y, SEARCH_W, SEARCH_H,
                Component.translatable("gui.invgrid.inventory_panel.search"));
        this.searchBox.setMaxLength(64);
        this.searchBox.setResponder(this::onSearchChanged);
        this.searchBox.setValue(this.menu.getSearchFilter());
        addRenderableWidget(this.searchBox);

        setInitialFocus(this.searchBox);
    }

    private void onSearchChanged(String text) {
        this.menu.setSearchFilter(text);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        // Keep the box in sync if the snapshot re-filters elsewhere.
        if (!this.searchBox.isFocused()
                && !this.searchBox.getValue().equals(this.menu.getSearchFilter())) {
            this.searchBox.setValue(this.menu.getSearchFilter());
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);
        renderTooltip(gfx, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Dark panel background with a light border, Ender IO panel style.
        gfx.fill(x, y, x + GUI_WIDTH, y + GUI_HEIGHT, 0xFF1A1A1E);
        gfx.renderOutline(x, y, GUI_WIDTH, GUI_HEIGHT, 0xFF55555A);
        // Inner subtle line under the search area.
        gfx.fill(x + 4, y + GRID_Y - 3, x + GUI_WIDTH - 4, y + GRID_Y - 2, 0xFF333338);

        // Empty state, matching Ender IO's bare look.
        if (this.menu.getVisibleSlots() == 0) {
            String msg = this.menu.getSnapshotCount() == 0 ? "Grid empty" : "No matching items";
            gfx.drawString(this.font, msg,
                    x + (GUI_WIDTH - this.font.width(msg)) / 2,
                    y + GRID_Y + 40, 0xFF888888, false);
        }
    }

    @Override
    protected void renderSlot(GuiGraphics gfx, net.minecraft.world.inventory.Slot slot) {
        super.renderSlot(gfx, slot);

        // Ender IO panel style: count label rendered in the corner of each cell.
        if (slot instanceof GridSnapshotSlot snapshotSlot && slot.hasItem()) {
            long total = snapshotSlot.getSnapshotTotal();
            String label = formatCount(total);
            int lx = slot.x + 17 - this.font.width(label);
            int ly = slot.y + 9;
            gfx.drawString(this.font, label, lx + 1, ly, 0xFF000000, false); // shadow
            gfx.drawString(this.font, label, lx, ly, 0xFFFFFFFF, false);
        }
    }

    private static String formatCount(long count) {
        if (count >= 1_000_000L) {
            return (count / 1_000_000L) + "M";
        } else if (count >= 10_000L) {
            return (count / 1_000L) + "k";
        }
        return Long.toString(count);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 || button == 1) {
            net.minecraft.world.inventory.Slot slot = this.hoveredSlot;
            if (slot instanceof GridSnapshotSlot snapshotSlot && slot.hasItem()) {
                boolean shift = hasShiftDown();
                // Left click: one stack's worth. Shift-click: everything of that type.
                this.menu.extractFromSlot(snapshotSlot, shift);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        // Intentionally minimal: no "Inventory Panel" title text inside the GUI,
        // matching the Ender IO panel's compact layout. Search box is the header.
    }
}
