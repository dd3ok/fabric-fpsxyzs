package com.dd3ok.fpsxyzs.gui;

import com.dd3ok.fpsxyzs.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModMainMenuScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;
    private final List<TextFieldWidget> textFields = new ArrayList<>();

    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING = 24;
    private static final int SECTION_SPACING = 15;

    public ModMainMenuScreen(Screen parent, ModConfig config) {
        super(Text.literal("FPS XYZ Settings"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        int leftColumnX = width / 3;
        int rightColumnX = (width * 2) / 3;
        int startY = 40;

        int leftY = startY;
        addDisplayTogglesSection(leftColumnX, leftY);

        int rightY = startY;
        addGeneralSettingsSection(rightColumnX, rightY);

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
                    config.save();
                    client.setScreen(parent);
                })
                .dimensions(width/2 - BUTTON_WIDTH/2, height - 30, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
    }

    private int addDisplayTogglesSection(int centerX, int startY) {
        int currentY = startY;

        drawSectionTitle("Display Options", centerX, currentY);
        currentY += 25;

        addToggleButton("Show FPS", config.isShowFps(), config::setShowFps, centerX, currentY);
        currentY += SPACING;
        addToggleButton("Show XYZ", config.isShowCoords(), config::setShowCoords, centerX, currentY);
        currentY += SPACING;
        addToggleButton("Show Biome", config.isShowBiome(), config::setShowBiome, centerX, currentY);
        currentY += SPACING;
        addToggleButton("Show Game Time", config.isShowGameTime(), config::setShowGameTime, centerX, currentY);
        currentY += SPACING;
        addToggleButton("Show Real Time", config.isShowRealTime(), config::setShowRealTime, centerX, currentY);
        currentY += SPACING;

        return currentY;
    }

    private int addGeneralSettingsSection(int centerX, int startY) {
        int currentY = startY;

        drawSectionTitle("General Settings", centerX, currentY);
        currentY += 25;

        addDrawableChild(CyclingButtonWidget.<ModConfig.Position>builder(pos ->
                        Text.literal("Position: " + pos.getDisplayName()))
                .values(ModConfig.Position.values())
                .initially(config.getPosition())
                .build(
                        centerX - BUTTON_WIDTH/2,
                        currentY,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        Text.literal("Position"),
                        (button, position) -> config.setPosition(position)
                ));
        currentY += SPACING;

        addNumberField("Text Scale", String.valueOf(config.getTextScale()),
                value -> config.setTextScale(parseFloat(value, 0.5f, 2.0f)), centerX, currentY);
        currentY += SPACING * 3;

        addNumberField("Line Spacing", String.valueOf(config.getLineSpacing()),
                value -> config.setLineSpacing(parseInt(value, 5, 20)), centerX, currentY);
        currentY += SPACING * 3;

        return currentY;
    }

    private void drawSectionTitle(String title, int centerX, int y) {
        addDrawableChild(new TextWidget(centerX - BUTTON_WIDTH/2, y, BUTTON_WIDTH, 15,
                Text.literal(title), textRenderer));
    }

    private void addToggleButton(String label, boolean initialState, Consumer<Boolean> setter, int centerX, int y) {
        addDrawableChild(CyclingButtonWidget.onOffBuilder(initialState)
                .build(
                        centerX - BUTTON_WIDTH/2,
                        y,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        Text.literal(label),
                        (button, state) -> setter.accept(state)
                ));
    }

    private void addNumberField(String label, String initialValue, Consumer<String> setter, int centerX, int y) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label), button -> {})
                .dimensions(centerX - BUTTON_WIDTH/2, y - 15, BUTTON_WIDTH, 15)
                .build());

        TextFieldWidget field = new TextFieldWidget(
                textRenderer,
                centerX - BUTTON_WIDTH/2,
                y,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                Text.literal(label)
        );
        field.setText(initialValue);
        field.setTextPredicate(text -> text.matches("^\\d*\\.?\\d*$"));
        field.setChangedListener(setter);
        textFields.add(field);
        addDrawableChild(field);
    }

    private void addTextField(String label, String initialValue, Consumer<String> setter, int centerX, int y) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label), button -> {})
                .dimensions(centerX - BUTTON_WIDTH/2, y - 15, BUTTON_WIDTH, 15)
                .build());

        TextFieldWidget field = new TextFieldWidget(
                textRenderer,
                centerX - BUTTON_WIDTH/2,
                y,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                Text.literal(label)
        );
        field.setText(initialValue);
        field.setChangedListener(setter);
        textFields.add(field);
        addDrawableChild(field);
    }

    private float parseFloat(String value, float min, float max) {
        try {
            float parsed = Float.parseFloat(value);
            return Math.max(min, Math.min(max, parsed));
        } catch (NumberFormatException e) {
            return min;
        }
    }

    private int parseInt(String value, int min, int max) {
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(min, Math.min(max, parsed));
        } catch (NumberFormatException e) {
            return min;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
