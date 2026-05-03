package com.dd3ok.fpsxyzs.gui;

import com.dd3ok.fpsxyzs.ModConfig;
import com.dd3ok.fpsxyzs.FPSXYZsMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMainMenuScreen extends Screen {
    private static final int PANEL_WIDTH = 620;
    private static final int LABEL_WIDTH = 330;
    private static final int CONTROL_WIDTH = 220;
    private static final int BUTTON_WIDTH = 190;
    private static final int CONTROL_HEIGHT = 20;
    private static final int ROW_HEIGHT = 26;
    private static final int SECTION_GAP = 8;
    private static final int LABEL_COLOR = 0xFFFFFFFF;
    private static final int HELP_COLOR = 0xFFA0A0A0;
    private static final int SECTION_COLOR = 0xFFFFFFA0;

    private final Screen parent;
    private final ModConfig config;

    private boolean enabled;
    private boolean showFps;
    private boolean showCoords;
    private boolean showBiome;
    private boolean showGameTime;
    private boolean showRealTime;
    private boolean showCoordinateFacingAxis;
    private ModConfig.Position position;
    private boolean coordinateCommaSeparator;
    private float textScale;
    private int lineSpacing;

    private EditBox textScaleField;
    private EditBox lineSpacingField;

    public ModMainMenuScreen(Screen parent, ModConfig config) {
        super(Component.literal("FPS XYZ Settings v" + FPSXYZsMod.VERSION));
        this.parent = parent;
        this.config = config;
        loadDraft();
    }

    private void loadDraft() {
        enabled = config.isEnabled();
        showFps = config.isShowFps();
        showCoords = config.isShowCoords();
        showBiome = config.isShowBiome();
        showGameTime = config.isShowGameTime();
        showRealTime = config.isShowRealTime();
        showCoordinateFacingAxis = config.isShowCoordinateFacingAxis();
        position = config.getPosition();
        coordinateCommaSeparator = config.isCoordinateCommaSeparator();
        textScale = config.getTextScale();
        lineSpacing = config.getLineSpacing();
    }

    @Override
    protected void init() {
        int panelX = Math.max(8, (width - PANEL_WIDTH) / 2);
        int labelX = panelX;
        int controlX = panelX + LABEL_WIDTH + 24;
        int y = 34;

        y = addSectionTitle(labelX, y, "Display");
        y = addToggleRow(labelX, controlX, y, "HUD Enabled", "Master switch for the entire overlay.", enabled, value -> enabled = value);
        y = addToggleRow(labelX, controlX, y, "FPS", "Shows the current client frame rate.", showFps, value -> showFps = value);
        y = addToggleRow(labelX, controlX, y, "XYZ", "Shows your current block position.", showCoords, value -> showCoords = value);
        y = addToggleRow(labelX, controlX, y, "Biome", "Shows the biome at your current position.", showBiome, value -> showBiome = value);
        y = addToggleRow(labelX, controlX, y, "Game Time", "Shows Minecraft world time as HH:mm.", showGameTime, value -> showGameTime = value);
        y = addToggleRow(labelX, controlX, y, "Real Time", "Shows your computer clock as HH:mm.", showRealTime, value -> showRealTime = value);

        y += SECTION_GAP;
        y = addSectionTitle(labelX, y, "XYZ Format");
        y = addToggleRow(labelX, controlX, y, "Facing Axis", "Adds X+/X-/Z+/Z- to the axis you are facing.", showCoordinateFacingAxis,
                value -> showCoordinateFacingAxis = value);
        y = addToggleRow(labelX, controlX, y, "XYZ Commas", "Uses commas between coordinates: X, Y, Z.", coordinateCommaSeparator,
                value -> coordinateCommaSeparator = value);

        y += SECTION_GAP;
        y = addSectionTitle(labelX, y, "Layout");
        addLabel(labelX, y, "Position", "Chooses the HUD corner on screen.");
        addRenderableWidget(CycleButton.<ModConfig.Position>builder(
                        pos -> Component.literal(pos.getDisplayName()),
                        position)
                .withValues(ModConfig.Position.values())
                .create(controlX, y, CONTROL_WIDTH, CONTROL_HEIGHT, Component.literal("Position"),
                        (button, value) -> position = value));
        y += ROW_HEIGHT;
        textScaleField = addTextRow(labelX, controlX, y, "Text Scale", "HUD text size. Allowed range: 1.0 to 2.0.", Float.toString(textScale));
        y += ROW_HEIGHT;
        lineSpacingField = addTextRow(labelX, controlX, y, "Line Spacing", "Vertical distance between HUD rows. Allowed range: 10 to 20.", Integer.toString(lineSpacing));
        y += ROW_HEIGHT;

        int buttonY = Math.max(y + SECTION_GAP, height - CONTROL_HEIGHT - 12);
        int resetX = panelX;
        int cancelX = panelX + PANEL_WIDTH - BUTTON_WIDTH * 2 - 12;
        int doneX = panelX + PANEL_WIDTH - BUTTON_WIDTH;

        addRenderableWidget(Button.builder(Component.literal("Reset Visible Defaults"), button -> {
                    config.resetDisplayDefaults();
                    FPSXYZsMod.resetInfoDisplay();
                    FPSXYZsMod.showStatusMessage("FPSXYZs v" + FPSXYZsMod.VERSION + ": defaults restored");
                    loadDraft();
                    rebuildWidgets();
                })
                .bounds(resetX, buttonY, BUTTON_WIDTH, CONTROL_HEIGHT)
                .build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> minecraft.setScreen(parent))
                .bounds(cancelX, buttonY, BUTTON_WIDTH, CONTROL_HEIGHT)
                .build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
                    applyDraft();
                    FPSXYZsMod.resetInfoDisplay();
                    FPSXYZsMod.showStatusMessage("FPSXYZs v" + FPSXYZsMod.VERSION + ": settings applied");
                    minecraft.setScreen(parent);
                })
                .bounds(doneX, buttonY, BUTTON_WIDTH, CONTROL_HEIGHT)
                .build());
    }

    private int addSectionTitle(int x, int y, String label) {
        Component title = Component.literal(label);
        addRenderableOnly((graphics, mouseX, mouseY, delta) ->
                graphics.text(font, title, x, y, SECTION_COLOR, false));
        return y + 18;
    }

    private int addToggleRow(int labelX, int controlX, int y, String label, String help, boolean initialValue, ToggleSetter setter) {
        addLabel(labelX, y, label, help);
        addRenderableWidget(CycleButton.onOffBuilder(initialValue)
                .create(controlX, y, CONTROL_WIDTH, CONTROL_HEIGHT, Component.literal(label),
                        (button, value) -> setter.set(value)));
        return y + ROW_HEIGHT;
    }

    private EditBox addTextRow(int labelX, int controlX, int y, String label, String help, String initialValue) {
        addLabel(labelX, y, label, help);
        EditBox field = new EditBox(font, controlX, y, CONTROL_WIDTH, CONTROL_HEIGHT, Component.literal(label));
        field.setValue(initialValue);
        addRenderableWidget(field);
        return field;
    }

    private void addLabel(int x, int y, String label, String help) {
        Component title = Component.literal(label);
        Component description = Component.literal(help);
        addRenderableOnly((graphics, mouseX, mouseY, delta) -> {
            graphics.text(font, title, x, y + 1, LABEL_COLOR, false);
            graphics.text(font, description, x, y + 12, HELP_COLOR, false);
        });
    }

    private void applyDraft() {
        config.applySettings(
                enabled,
                showFps,
                showCoords,
                showBiome,
                showGameTime,
                showRealTime,
                showCoordinateFacingAxis,
                position,
                coordinateCommaSeparator,
                parseFloat(textScaleField.getValue(), 1.0f, 2.0f, textScale),
                parseInt(lineSpacingField.getValue(), 10, 20, lineSpacing)
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
        graphics.centeredText(font, title, width / 2, 14, LABEL_COLOR);
    }

    @FunctionalInterface
    private interface ToggleSetter {
        void set(boolean value);
    }
}
