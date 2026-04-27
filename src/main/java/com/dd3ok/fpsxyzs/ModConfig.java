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
    private boolean showCoordinateFacingAxis = false;
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
    private transient boolean deferSave;
    private transient boolean dirty;

    public int getGameTimeLine() {
        return gameTimeLine;
    }

    public void setGameTimeLine(int gameTimeLine) {
        if (this.gameTimeLine == gameTimeLine) return;
        this.gameTimeLine = gameTimeLine;
        save();
    }

    public int getFpsLine() {
        return fpsLine;
    }

    public void setFpsLine(int fpsLine) {
        if (this.fpsLine == fpsLine) return;
        this.fpsLine = fpsLine;
        save();
    }

    public int getCoordsLine() {
        return coordsLine;
    }

    public void setCoordsLine(int coordsLine) {
        if (this.coordsLine == coordsLine) return;
        this.coordsLine = coordsLine;
        save();
    }

    public int getBiomeLine() {
        return biomeLine;
    }

    public void setBiomeLine(int biomeLine) {
        if (this.biomeLine == biomeLine) return;
        this.biomeLine = biomeLine;
        save();
    }

    public int getRealTimeLine() {
        return realTimeLine;
    }

    public void setRealTimeLine(int realTimeLine) {
        if (this.realTimeLine == realTimeLine) return;
        this.realTimeLine = realTimeLine;
        save();
    }

    public String getSeparator() {
        return separator == null ? " | " : separator;
    }

    public void setSeparator(String separator) {
        String newSeparator = separator == null ? " | " : separator;
        if (newSeparator.equals(this.separator)) return;
        this.separator = newSeparator;
        save();
    }

    // 같은 줄에 있는 정보들 사이의 구분자
    private String separator = " | ";
    private String coordinateSeparator = " ";

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
                ModConfig config = GSON.fromJson(json, ModConfig.class);
                return config == null ? new ModConfig() : config;
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
            }
        }
        return new ModConfig();
    }

    public void save() {
        if (deferSave) {
            dirty = true;
            return;
        }

        writeToDisk();
    }

    private void writeToDisk() {
        try {
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    public void applySettings(
            boolean showFps,
            boolean showCoords,
            boolean showBiome,
            boolean showGameTime,
            boolean showRealTime,
            boolean showCoordinateFacingAxis,
            Position position,
            String coordinateSeparator,
            float textScale,
            int lineSpacing
    ) {
        deferSave = true;
        dirty = false;

        setShowFps(showFps);
        setShowCoords(showCoords);
        setShowBiome(showBiome);
        setShowGameTime(showGameTime);
        setShowRealTime(showRealTime);
        setShowCoordinateFacingAxis(showCoordinateFacingAxis);
        setPosition(position);
        setCoordinateSeparator(coordinateSeparator);
        setTextScale(textScale);
        setLineSpacing(lineSpacing);

        deferSave = false;
        if (dirty) {
            dirty = false;
            writeToDisk();
        }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        save();
    }

    public boolean isShowFps() { return showFps; }
    public void setShowFps(boolean showFps) {
        if (this.showFps == showFps) return;
        this.showFps = showFps;
        save();
    }

    public boolean isShowCoords() { return showCoords; }
    public void setShowCoords(boolean showCoords) {
        if (this.showCoords == showCoords) return;
        this.showCoords = showCoords;
        save();
    }

    public boolean isShowBiome() { return showBiome; }
    public void setShowBiome(boolean showBiome) {
        if (this.showBiome == showBiome) return;
        this.showBiome = showBiome;
        save();
    }

    public boolean isShowGameTime() { return showGameTime; }
    public void setShowGameTime(boolean showGameTime) {
        if (this.showGameTime == showGameTime) return;
        this.showGameTime = showGameTime;
        save();
    }

    public boolean isShowRealTime() { return showRealTime; }
    public void setShowRealTime(boolean showRealTime) {
        if (this.showRealTime == showRealTime) return;
        this.showRealTime = showRealTime;
        save();
    }

    public boolean isShowCoordinateFacingAxis() { return showCoordinateFacingAxis; }
    public void setShowCoordinateFacingAxis(boolean showCoordinateFacingAxis) {
        if (this.showCoordinateFacingAxis == showCoordinateFacingAxis) return;
        this.showCoordinateFacingAxis = showCoordinateFacingAxis;
        save();
    }

    public String getCoordinateSeparator() {
        return coordinateSeparator == null || coordinateSeparator.isEmpty() ? " " : coordinateSeparator;
    }

    public void setCoordinateSeparator(String coordinateSeparator) {
        String newCoordinateSeparator = coordinateSeparator == null ? " " : coordinateSeparator;
        if (newCoordinateSeparator.equals(this.coordinateSeparator)) return;
        this.coordinateSeparator = newCoordinateSeparator;
        save();
    }

    public Position getPosition() { return position; }
    public void setPosition(Position position) {
        if (this.position == position) return;
        this.position = position;
        save();
    }

    public float getTextScale() { return textScale; }
    public void setTextScale(float scale) {
        float newTextScale = Math.max(0.5f, Math.min(2.0f, scale));
        if (Float.compare(this.textScale, newTextScale) == 0) return;
        this.textScale = newTextScale;
        save();
    }

    public int getTextColor() { return textColor; }
    public void setTextColor(int color) {
        if (this.textColor == color) return;
        this.textColor = color;
        save();
    }

    public int getLineSpacing() { return lineSpacing; }
    public void setLineSpacing(int spacing) {
        int newLineSpacing = Math.max(5, Math.min(20, spacing));
        if (this.lineSpacing == newLineSpacing) return;
        this.lineSpacing = newLineSpacing;
        save();
    }
}
