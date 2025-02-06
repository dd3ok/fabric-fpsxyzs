package com.dd3ok.fpsxyzs;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("fpsxyzs.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private boolean enabled = true;
    private boolean showFps = true;
    private boolean showCoords = true;
//    private boolean showWeather = true;
    private boolean showBiome = true;
    private boolean showGameTime = true;
    private boolean showRealTime = true;
    private Position position = Position.TOP_RIGHT;
    private float textScale = 0.8f;
    private int textColor = 0xFFFFFF;
    private int lineSpacing = 9;

    private int fpsLine = 1;
    private int coordsLine = 2;
//    private int weatherLine = 4;
    private int gameTimeLine = 3;
    private int realTimeLine = 3;
    private int biomeLine = 4;

    public int getGameTimeLine() {
        return gameTimeLine;
    }

    public void setGameTimeLine(int gameTimeLine) {
        this.gameTimeLine = gameTimeLine;
    }

    public int getFpsLine() {
        return fpsLine;
    }

    public void setFpsLine(int fpsLine) {
        this.fpsLine = fpsLine;
    }

    public int getCoordsLine() {
        return coordsLine;
    }

    public void setCoordsLine(int coordsLine) {
        this.coordsLine = coordsLine;
    }

    public int getBiomeLine() {
        return biomeLine;
    }

    public void setBiomeLine(int biomeLine) {
        this.biomeLine = biomeLine;
    }

    public int getRealTimeLine() {
        return realTimeLine;
    }

    public void setRealTimeLine(int realTimeLine) {
        this.realTimeLine = realTimeLine;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    // 같은 줄에 있는 정보들 사이의 구분자
    private String separator = " | ";

    public enum Position {
        TOP_LEFT("Top Left"),
        TOP_RIGHT("Top Right");

        private final String displayName;

        Position(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static ModConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, ModConfig.class);
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
            }
        }
        return new ModConfig();
    }

    public void save() {
        try {
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    public boolean isShowFps() { return showFps; }
    public void setShowFps(boolean showFps) {
        this.showFps = showFps;
        save();
    }

    public boolean isShowCoords() { return showCoords; }
    public void setShowCoords(boolean showCoords) {
        this.showCoords = showCoords;
        save();
    }

    public boolean isShowBiome() { return showBiome; }
    public void setShowBiome(boolean showBiome) {
        this.showBiome = showBiome;
        save();
    }

    public boolean isShowGameTime() { return showGameTime; }
    public void setShowGameTime(boolean showGameTime) {
        this.showGameTime = showGameTime;
        save();
    }

    public boolean isShowRealTime() { return showRealTime; }
    public void setShowRealTime(boolean showRealTime) {
        this.showRealTime = showRealTime;
        save();
    }

    public Position getPosition() { return position; }
    public void setPosition(Position position) {
        this.position = position;
        save();
    }

    public float getTextScale() { return textScale; }
    public void setTextScale(float scale) {
        this.textScale = Math.max(0.5f, Math.min(2.0f, scale));
        save();
    }

    public int getTextColor() { return textColor; }
    public void setTextColor(int color) {
        this.textColor = color;
        save();
    }

    public int getLineSpacing() { return lineSpacing; }
    public void setLineSpacing(int spacing) {
        this.lineSpacing = Math.max(5, Math.min(20, spacing));
        save();
    }
}
