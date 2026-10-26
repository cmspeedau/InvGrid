package dev.invgrid.client.screen;

import dev.invgrid.menu.InventoryPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Screen for the Basic Inventory Panel (v0.1).
 * Renders a 9x6 grid snapshot area, a search box, and the player inventory.
 * Extraction/insertion interactions land in v0.2.
 */
public class InventoryPanelScreen extends AbstractContainerScreen<InventoryPanelMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("invgrid", "textures/gui/inventory_panel.png");

    private static final int IMAGE_WIDTH = 176;
    private static final int IMAGE_HEIGHT = 232;

    private EditBox searchBox;

    public InventoryPanelScreen(InventoryPanelMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = IMAGE_WIDTH;
        this.imageHeight = IMAGE_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.searchBox = new EditBox(this.font,
                this.leftPos + 8, this.topPos + 4, 120, 10,
                Component.translatable("gui.invgrid.inventory_panel.search"));
        this.searchBox.setHint(Component.translatable("gui.invgrid.inventory_panel.search_hint"));
        this.searchBox.setResponder(this::onSearchChanged);
        this.addWidget(this.searchBox);
    }

    private void onSearchChanged(String text) {
        this.menu.setFilter(text);
        // TODO v0.2: send filter packet to server, request refreshed snapshot
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.searchBox.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Keep typing in the search box from triggering hotbar/inventory keys.
        if (this.searchBox.isFocused() && keyCode != 256) {
            return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}