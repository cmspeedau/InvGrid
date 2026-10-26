package dev.invgrid.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.invgrid.InvGrid;
import dev.invgrid.menu.GridSnapshotSlot;
import dev.invgrid.menu.InventoryPanelMenu;
import dev.invgrid.network.ExtractFromGridPacket;
import dev.invgrid.network.UpdateFilterPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InventoryPanelScreen extends AbstractContainerScreen<InventoryPanelMenu> {

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(InvGrid.MODID, "textures/gui/inventory_panel.png");

    private EditBox searchBox;

    public InventoryPanelScreen(InventoryPanelMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 194;
        this.imageHeight = 244;
    }

    @Override
    protected void init() {
        super.init();
        this.searchBox = new EditBox(this.font,
                this.leftPos + 10, this.topPos + 8, 140, 14, Component.translatable("gui.invgrid.search"));
        this.searchBox.setHint(Component.translatable("gui.invgrid.search.hint"));
        this.searchBox.setResponder(text -> {
            menu.setFilter(text);
            UpdateFilterPacket packet = new UpdateFilterPacket(text);
            dev.invgrid.network.ModNetworking.CHANNEL.sendToServer(packet);
        });
        addRenderableWidget(this.searchBox);
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Snapshot items render directly from the synced list
        var slots = this.menu.slots;
        for (int i = 0; i < InventoryPanelMenu.GRID_COLS * InventoryPanelMenu.GRID_ROWS; i++) {
            if (slots.get(i) instanceof GridSnapshotSlot slot) {
                ItemStack stack = slot.getSnapshotStack();
                if (!stack.isEmpty()) {
                    graphics.renderItem(stack, slot.x, slot.y);
                    graphics.renderItemDecorations(this.font, stack, slot.x, slot.y, null);
                }
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y) {
        if (this.hoveredSlot instanceof GridSnapshotSlot slot) {
            ItemStack stack = slot.getSnapshotStack();
            if (!stack.isEmpty()) {
                graphics.renderTooltip(this.font, stack, x, y);
            }
        }
        super.renderTooltip(graphics, x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.hoveredSlot instanceof GridSnapshotSlot slot) {
            ItemStack stack = slot.getSnapshotStack();
            if (!stack.isEmpty()) {
                boolean shift = net.minecraft.client.gui.GuiComponent.hasShiftDown();
                dev.invgrid.network.ModNetworking.CHANNEL.sendToServer(
                        new ExtractFromGridPacket(slot.gridIndex, shift));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Keep typing in the search box from triggering inventory/hotbar keys
        if (this.searchBox != null && this.searchBox.isFocused()
                && keyCode != 256 /* ESC */) {
            return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
