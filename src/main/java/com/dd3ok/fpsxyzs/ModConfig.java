package com.dd3ok.fpsxyzs;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private transient Path configPath;
    private boolean enabled = true;
    private boolean showFps = true;
    private boolean showCoords = true;
    private boolean showBiome = true;
    private boolean showGameTime = true;
    private boolean showRealTime = true;
    private boolean showCoordinateFacingAxis = false;
    private Position position = Position.TOP_RIGHT;
    private float textScale = 1.0f;
    private int textColor = 0xFFFFFFFF;
    private int lineSpacing = 10;

    private int fpsLine = 1;
    private int coordsLine = 2;
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
    private boolean coordinateCommaSeparator = false;

    public ModConfig() {
        this(defaultConfigPath());
    }

    ModConfig(Path configPath) {
        this.configPath = configPath;
    }

    public enum Position {
        TOP_LEFT("Top Left"),
        TOP_CENTER("Top Center"),
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
        Path configPath = defaultConfigPath();
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                ModConfig config = GSON.fromJson(json, ModConfig.class);
                if (config == null) {
                    return new ModConfig(configPath);
                }
                config.configPath = configPath;
                return config;
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
            }
        }
        return new ModConfig(configPath);
    }

    private static Path defaultConfigPath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve("fpsxyzs.json");
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
            if (configPath.getParent() != null) {
                Files.createDirectories(configPath.getParent());
            }
            String json = GSON.toJson(this);
            Files.writeString(configPath, json,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    public void applySettings(
            boolean enabled,
            boolean showFps,
            boolean showCoords,
            boolean showBiome,
            boolean showGameTime,
            boolean showRealTime,
            boolean showCoordinateFacingAxis,
            Position position,
            boolean coordinateCommaSeparator,
            float textScale,
            int lineSpacing
    ) {
        deferSave = true;
        dirty = false;

        setEnabled(enabled);
        setShowFps(showFps);
        setShowCoords(showCoords);
        setShowBiome(showBiome);
        setShowGameTime(showGameTime);
        setShowRealTime(showRealTime);
        setShowCoordinateFacingAxis(showCoordinateFacingAxis);
        setPosition(position);
        setCoordinateCommaSeparator(coordinateCommaSeparator);
        setTextScale(textScale);
        setLineSpacing(lineSpacing);

        deferSave = false;
        if (dirty) {
            dirty = false;
            writeToDisk();
        }
    }

    public void resetDisplayDefaults() {
        applySettings(
                true,
                true,
                true,
                true,
                true,
                true,
                false,
                Position.TOP_LEFT,
                false,
                1.0f,
                10
        );
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

    public boolean isCoordinateCommaSeparator() {
        return coordinateCommaSeparator || ", ".equals(coordinateSeparator);
    }

    public void setCoordinateCommaSeparator(boolean coordinateCommaSeparator) {
        if (this.coordinateCommaSeparator == coordinateCommaSeparator && ", ".equals(coordinateSeparator) == coordinateCommaSeparator) return;
        this.coordinateCommaSeparator = coordinateCommaSeparator;
        this.coordinateSeparator = coordinateCommaSeparator ? ", " : " ";
        save();
    }

    public Position getPosition() { return position; }
    public void setPosition(Position position) {
        if (this.position == position) return;
        this.position = position;
        save();
    }

    public float getTextScale() {
        return textScale < 1.0f ? 1.0f : textScale;
    }
    public void setTextScale(float scale) {
        float newTextScale = Math.max(1.0f, Math.min(2.0f, scale));
        if (Float.compare(this.textScale, newTextScale) == 0) return;
        this.textScale = newTextScale;
        save();
    }

    public int getTextColor() {
        return withOpaqueAlpha(textColor);
    }
    public void setTextColor(int color) {
        int newColor = withOpaqueAlpha(color);
        if (this.textColor == newColor) return;
        this.textColor = newColor;
        save();
    }

    public int getLineSpacing() {
        return lineSpacing < 10 ? 10 : lineSpacing;
    }
    public void setLineSpacing(int spacing) {
        int newLineSpacing = Math.max(10, Math.min(20, spacing));
        if (this.lineSpacing == newLineSpacing) return;
        this.lineSpacing = newLineSpacing;
        save();
    }

    private static int withOpaqueAlpha(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }
}
