package com.dd3ok.fpsxyzs;

import com.dd3ok.fpsxyzs.util.TimeFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

public class InfoDisplay {
    private static final int MAX_LINES = 6;
    private static final int BIOME_UPDATE_DISTANCE = 16;
    private static final int GAME_TIME_UPDATE_INTERVAL = 50;
    private static final int REAL_TIME_UPDATE_INTERVAL = 1000;
    private static final int FPS_UPDATE_INTERVAL = 250;

    private final MinecraftClient client;
    private final ModConfig config;
    private final String[] lines;
    private final Text[] cachedTextObjects;
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

    public InfoDisplay(MinecraftClient client, ModConfig config) {
        this.client = client;
        this.config = config;
        this.lines = new String[MAX_LINES];
        this.cachedTextObjects = new Text[MAX_LINES];
        this.cachedTextWidths = new int[MAX_LINES];
        this.lineBuilder = new StringBuilder(100);
    }

    public void update() {
        if (client.player == null || client.world == null || !config.isEnabled()) {
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
            cachedFps = client.getCurrentFps() + " fps";
            lastFpsUpdate = currentTime;
        }
        appendToLine(config.getFpsLine(), cachedFps);
    }

    private void updateCoords() {
        BlockPos pos = client.player.getBlockPos();
        int lineIndex = config.getCoordsLine();
        lineBuilder.setLength(0);
        lineBuilder.append(pos.getX()).append(' ')
                .append(pos.getY()).append(' ')
                .append(pos.getZ());
        appendToLine(lineIndex, lineBuilder.toString());
    }


    private void updateBiome() {
        BlockPos currentPos = client.player.getBlockPos();
        if (lastBiomePos == null ||
                currentPos.getManhattanDistance(lastBiomePos) > BIOME_UPDATE_DISTANCE) {
            cachedBiomeName = client.world.getBiome(currentPos)
                    .getKey()
                    .map(key -> key.getValue().getPath())
                    .orElse("unknown");
            lastBiomePos = currentPos;
        }
        appendToLine(config.getBiomeLine(), cachedBiomeName);
    }

    private void updateGameTime(long currentTime) {
        if (currentTime - lastGameTimeUpdate > GAME_TIME_UPDATE_INTERVAL) {
            cachedGameTime = TimeFormatter.formatGameTime(client.world.getTimeOfDay() % 24000);
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
                    .append(" " + text);
            lines[index] = lineBuilder.toString();
        }
    }

    public void render(DrawContext context) {
        if (!config.isEnabled() || !needsRendering) return;

        context.getMatrices().push();
        context.getMatrices().scale(config.getTextScale(), config.getTextScale(), 1.0f);

        int screenWidth = (int)(client.getWindow().getScaledWidth() / config.getTextScale());
        int y = 5;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line != null && !line.isEmpty()) {
                updateCachedText(i, line);

                int x = (config.getPosition() == ModConfig.Position.TOP_RIGHT)
                        ? screenWidth - cachedTextWidths[i] - 5
                        : 5;

                context.drawText(
                        client.textRenderer,
                        cachedTextObjects[i],
                        x,
                        (int)(y / config.getTextScale()),
                        config.getTextColor(),
                        false
                );

                y += config.getLineSpacing();
            }
        }

        context.getMatrices().pop();
    }

    private void updateCachedText(int index, String line) {
        if (cachedTextObjects[index] == null ||
                !cachedTextObjects[index].getString().equals(line)) {
            cachedTextObjects[index] = Text.literal(line);
            cachedTextWidths[index] = client.textRenderer.getWidth(line);
        }
    }
}

