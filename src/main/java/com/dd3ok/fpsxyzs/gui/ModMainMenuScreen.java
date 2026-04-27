package com.dd3ok.fpsxyzs.gui;

import com.dd3ok.fpsxyzs.ModConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMainMenuScreen extends Screen {
    private static final int CONTROL_WIDTH = 170;
    private static final int CONTROL_HEIGHT = 20;
    private static final int ROW_HEIGHT = 24;
    private static final int SECTION_GAP = 12;
    private static final int COLUMN_GAP = 32;

    private final Screen parent;
    private final ModConfig config;

    private boolean showFps;
    private boolean showCoords;
    private boolean showBiome;
    private boolean showGameTime;
    private boolean showRealTime;
    private boolean showCoordinateFacingAxis;
    private ModConfig.Position position;
    private String coordinateSeparator;
    private float textScale;
    private int lineSpacing;

    private EditBox coordinateSeparatorField;
    private EditBox textScaleField;
    private EditBox lineSpacingField;

    public ModMainMenuScreen(Screen parent, ModConfig config) {
        super(Component.literal("FPS XYZ Settings"));
        this.parent = parent;
        this.config = config;
        loadDraft();
    }

    private void loadDraft() {
        showFps = config.isShowFps();
        showCoords = config.isShowCoords();
        showBiome = config.isShowBiome();
        showGameTime = config.isShowGameTime();
        showRealTime = config.isShowRealTime();
        showCoordinateFacingAxis = config.isShowCoordinateFacingAxis();
        position = config.getPosition();
        coordinateSeparator = config.getCoordinateSeparator();
        textScale = config.getTextScale();
        lineSpacing = config.getLineSpacing();
    }

    @Override
    protected void init() {
        int totalWidth = CONTROL_WIDTH * 2 + COLUMN_GAP;
        int leftX = Math.max(8, (width - totalWidth) / 2);
        int rightX = leftX + CONTROL_WIDTH + COLUMN_GAP;
        int startY = 38;

        int leftY = startY;
        leftY = addSectionTitle(leftX, leftY, "Display");
        leftY = addToggle(leftX, leftY, "FPS", showFps, value -> showFps = value);
        leftY = addToggle(leftX, leftY, "XYZ", showCoords, value -> showCoords = value);
        leftY = addToggle(leftX, leftY, "Biome", showBiome, value -> showBiome = value);
        leftY = addToggle(leftX, leftY, "Game Time", showGameTime, value -> showGameTime = value);
        leftY = addToggle(leftX, leftY, "Real Time", showRealTime, value -> showRealTime = value);

        int rightY = startY;
        rightY = addSectionTitle(rightX, rightY, "XYZ");
        rightY = addToggle(rightX, rightY, "Facing Axis", showCoordinateFacingAxis,
                value -> showCoordinateFacingAxis = value);
        coordinateSeparatorField = addTextField(rightX, rightY, "Separator", coordinateSeparator);
        rightY += ROW_HEIGHT + SECTION_GAP;

        rightY = addSectionTitle(rightX, rightY, "General");
        addRenderableWidget(CycleButton.<ModConfig.Position>builder(
                        pos -> Component.literal("Position: " + pos.getDisplayName()),
                        position)
                .withValues(ModConfig.Position.values())
                .create(rightX, rightY, CONTROL_WIDTH, CONTROL_HEIGHT, Component.literal("Position"),
                        (button, value) -> position = value));
        rightY += ROW_HEIGHT + SECTION_GAP;

        textScaleField = addTextField(rightX, rightY, "Text Scale", Float.toString(textScale));
        rightY += ROW_HEIGHT + SECTION_GAP;
        lineSpacingField = addTextField(rightX, rightY, "Line Spacing", Integer.toString(lineSpacing));

        int preferredButtonY = Math.max(leftY, rightY) + SECTION_GAP;
        int lowestVisibleButtonY = height - CONTROL_HEIGHT - 8;
        int buttonY = Math.min(preferredButtonY, lowestVisibleButtonY);
        int cancelX = width / 2 - CONTROL_WIDTH - 5;
        int doneX = width / 2 + 5;

        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> minecraft.setScreen(parent))
                .bounds(cancelX, buttonY, CONTROL_WIDTH, CONTROL_HEIGHT)
                .build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
                    applyDraft();
                    minecraft.setScreen(parent);
                })
                .bounds(doneX, buttonY, CONTROL_WIDTH, CONTROL_HEIGHT)
                .build());
    }

    private int addSectionTitle(int x, int y, String label) {
        Component title = Component.literal(label);
        addRenderableOnly((graphics, mouseX, mouseY, delta) ->
                graphics.text(font, title, x, y, 0xFFFFFF, false));
        return y + ROW_HEIGHT;
    }

    private int addToggle(int x, int y, String label, boolean initialValue, ToggleSetter setter) {
        addRenderableWidget(CycleButton.onOffBuilder(initialValue)
                .create(x, y, CONTROL_WIDTH, CONTROL_HEIGHT, Component.literal(label),
                        (button, value) -> setter.set(value)));
        return y + ROW_HEIGHT;
    }

    private EditBox addTextField(int x, int y, String label, String initialValue) {
        Component title = Component.literal(label);
        addRenderableOnly((graphics, mouseX, mouseY, delta) ->
                graphics.text(font, title, x, y - 11, 0xA0A0A0, false));

        EditBox field = new EditBox(font, x, y, CONTROL_WIDTH, CONTROL_HEIGHT, title);
        field.setValue(initialValue);
        addRenderableWidget(field);
        return field;
    }

    private void applyDraft() {
        config.applySettings(
                showFps,
                showCoords,
                showBiome,
                showGameTime,
                showRealTime,
                showCoordinateFacingAxis,
                position,
                coordinateSeparatorField.getValue(),
                parseFloat(textScaleField.getValue(), 0.5f, 2.0f, textScale),
                parseInt(lineSpacingField.getValue(), 5, 20, lineSpacing)
        );
    }

    private float parseFloat(String value, float min, float max, float fallback) {
        try {
            float parsed = Float.parseFloat(value);
            return Math.max(min, Math.min(max, parsed));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private int parseInt(String value, int min, int max, int fallback) {
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(min, Math.min(max, parsed));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(font, title, width / 2, 14, 0xFFFFFF);
    }

    @FunctionalInterface
    private interface ToggleSetter {
        void set(boolean value);
    }
}
