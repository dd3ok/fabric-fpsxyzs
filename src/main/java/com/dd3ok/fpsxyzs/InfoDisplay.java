package com.dd3ok.fpsxyzs;

import com.dd3ok.fpsxyzs.util.CoordinateFormatter;
import com.dd3ok.fpsxyzs.util.TimeFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class InfoDisplay {
    private static final int MAX_LINES = 6;
    private static final int BIOME_UPDATE_DISTANCE = 16;
    private static final int GAME_TIME_UPDATE_INTERVAL = 50;
    private static final int REAL_TIME_UPDATE_INTERVAL = 1000;
    private static final int FPS_UPDATE_INTERVAL = 250;

    private final Minecraft client;
    private final ModConfig config;
    private final String[] lines;
    private final String[] cachedLines;
    private final Component[] cachedTextObjects;
    private final int[] cachedTextWidths;
    private final StringBuilder lineBuilder;

    private long lastUpdateTime;
    private long lastRealTimeUpdate;
    private String cachedRealTime = "";
    private BlockPos lastBiomePos;
    private String cachedBiomeName = "";
    private long lastGameTimeUpdate;
    private String cachedGameTime = "";
    private boolean needsRendering = false;
    private long lastFpsUpdate;
    private String cachedFps = "";
    private int lastCoordX = Integer.MIN_VALUE;
    private int lastCoordY = Integer.MIN_VALUE;
    private int lastCoordZ = Integer.MIN_VALUE;
    private int lastCoordFacingQuarter = Integer.MIN_VALUE;
    private boolean lastShowCoordinateFacingAxis;
    private String lastCoordinateSeparator = "";
    private String cachedCoords = "";

    public InfoDisplay(Minecraft client, ModConfig config) {
        this.client = client;
        this.config = config;
        this.lines = new String[MAX_LINES];
        this.cachedLines = new String[MAX_LINES];
        this.cachedTextObjects = new Component[MAX_LINES];
        this.cachedTextWidths = new int[MAX_LINES];
        this.lineBuilder = new StringBuilder(100);
    }

    public void update() {
        if (client.player == null || client.level == null || !config.isEnabled()) {
            needsRendering = false;
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < 16) return;

        this.lastUpdateTime = currentTime;
        needsRendering = true;

        clearLines();

        if (config.isShowFps()) updateFps(currentTime);
        if (config.isShowCoords()) updateCoords();
        if (config.isShowBiome()) updateBiome();
        if (config.isShowGameTime()) updateGameTime(currentTime);
        if (config.isShowRealTime()) updateRealTime(currentTime);
    }

    private void clearLines() {
        Arrays.fill(lines, "");
    }

    private void updateFps(long currentTime) {
        if (currentTime - lastFpsUpdate > FPS_UPDATE_INTERVAL) {
            cachedFps = client.getFps() + " fps";
            lastFpsUpdate = currentTime;
        }
        appendToLine(config.getFpsLine(), cachedFps);
    }

    private void updateCoords() {
        BlockPos pos = client.player.blockPosition();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        boolean showFacingAxis = config.isShowCoordinateFacingAxis();
        int facingQuarter = showFacingAxis
                ? CoordinateFormatter.facingQuarter(client.player.getYRot())
                : Integer.MIN_VALUE;
        String coordinateSeparator = config.getCoordinateSeparator();

        if (x != lastCoordX
                || y != lastCoordY
                || z != lastCoordZ
                || showFacingAxis != lastShowCoordinateFacingAxis
                || facingQuarter != lastCoordFacingQuarter
                || !coordinateSeparator.equals(lastCoordinateSeparator)) {
            cachedCoords = CoordinateFormatter.format(x, y, z, coordinateSeparator, showFacingAxis, client.player.getYRot());
            lastCoordX = x;
            lastCoordY = y;
            lastCoordZ = z;
            lastShowCoordinateFacingAxis = showFacingAxis;
            lastCoordFacingQuarter = facingQuarter;
            lastCoordinateSeparator = coordinateSeparator;
        }

        appendToLine(config.getCoordsLine(), cachedCoords);
    }


    private void updateBiome() {
        BlockPos currentPos = client.player.blockPosition();
        if (lastBiomePos == null ||
                manhattanDistance(currentPos, lastBiomePos) > BIOME_UPDATE_DISTANCE) {
            cachedBiomeName = client.level.getBiome(currentPos)
                    .unwrapKey()
                    .map(key -> key.identifier().getPath())
                    .orElse("unknown");
            lastBiomePos = currentPos;
        }
        appendToLine(config.getBiomeLine(), cachedBiomeName);
    }

    private void updateGameTime(long currentTime) {
        if (currentTime - lastGameTimeUpdate > GAME_TIME_UPDATE_INTERVAL) {
            cachedGameTime = TimeFormatter.formatGameTime(client.level.getLevelData().getGameTime() % 24000);
            lastGameTimeUpdate = currentTime;
        }
        appendToLine(config.getGameTimeLine(), cachedGameTime);
    }

    private void updateRealTime(long currentTime) {
        if (currentTime - lastRealTimeUpdate > REAL_TIME_UPDATE_INTERVAL) {
            cachedRealTime = TimeFormatter.formatRealTime();
            lastRealTimeUpdate = currentTime;
        }

        appendToLineWithoutSeparator(config.getRealTimeLine(), cachedRealTime);
    }

    private void appendToLine(int index, String text) {
        if (index < 0 || index >= MAX_LINES) return;

        if (lines[index].isEmpty()) {
            lines[index] = text;
        } else {
            lineBuilder.setLength(0);
            lineBuilder.append(lines[index])
                    .append(config.getSeparator())
                    .append(text);
            lines[index] = lineBuilder.toString();
        }
    }

    private void appendToLineWithoutSeparator(int index, String text) {
        if (index < 0 || index >= MAX_LINES) return;

        if (lines[index].isEmpty()) {
            lines[index] = text;
        } else {
            lineBuilder.setLength(0);
            lineBuilder.append(lines[index])
                    .append(' ')
                    .append(text);
            lines[index] = lineBuilder.toString();
        }
    }

    public void render(GuiGraphicsExtractor graphics) {
        if (!config.isEnabled() || !needsRendering) return;

        graphics.pose().pushMatrix();
        float textScale = config.getTextScale();
        ModConfig.Position position = config.getPosition();
        int textColor = config.getTextColor();
        int lineSpacing = config.getLineSpacing();

        graphics.pose().scale(textScale, textScale);

        int screenWidth = (int)(graphics.guiWidth() / textScale);
        int y = 5;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line != null && !line.isEmpty()) {
                updateCachedText(i, line);

                int x = (position == ModConfig.Position.TOP_RIGHT)
                        ? screenWidth - cachedTextWidths[i] - 5
                        : 5;

                graphics.text(
                        client.font,
                        cachedTextObjects[i],
                        x,
                        y,
                        textColor,
                        false
                );

                y += lineSpacing;
            }
        }

        graphics.pose().popMatrix();
    }

    private void updateCachedText(int index, String line) {
        if (!line.equals(cachedLines[index])) {
            cachedLines[index] = line;
            cachedTextObjects[index] = Component.literal(line);
            cachedTextWidths[index] = client.font.width(line);
        }
    }

    private int manhattanDistance(BlockPos first, BlockPos second) {
        return Math.abs(first.getX() - second.getX())
                + Math.abs(first.getY() - second.getY())
                + Math.abs(first.getZ() - second.getZ());
    }
}
