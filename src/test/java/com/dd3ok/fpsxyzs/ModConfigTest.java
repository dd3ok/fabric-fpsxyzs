package com.dd3ok.fpsxyzs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ModConfigTest {
    @TempDir
    Path tempDir;

    @Test
    void applySettingsCanRestoreGlobalHudEnabledState() {
        ModConfig config = new ModConfig(tempDir.resolve("fpsxyzs.json"));
        config.setEnabled(false);

        config.applySettings(
                true,
                true,
                true,
                true,
                true,
                true,
                false,
                ModConfig.Position.TOP_RIGHT,
                false,
                1.0f,
                10
        );

        assertTrue(config.isEnabled());
    }

    @Test
    void applySettingsCanDisableGlobalHudEnabledState() {
        ModConfig config = new ModConfig(tempDir.resolve("fpsxyzs.json"));

        config.applySettings(
                false,
                true,
                true,
                true,
                true,
                true,
                false,
                ModConfig.Position.TOP_RIGHT,
                false,
                1.0f,
                10
        );

        assertFalse(config.isEnabled());
    }

    @Test
    void resetDisplayDefaultsRestoresVisibleHudSettings() {
        ModConfig config = new ModConfig(tempDir.resolve("fpsxyzs.json"));
        config.applySettings(
                false,
                false,
                false,
                false,
                false,
                false,
                true,
                ModConfig.Position.TOP_LEFT,
                true,
                2.0f,
                20
        );

        config.resetDisplayDefaults();

        assertTrue(config.isEnabled());
        assertTrue(config.isShowFps());
        assertTrue(config.isShowCoords());
        assertTrue(config.isShowBiome());
        assertTrue(config.isShowGameTime());
        assertTrue(config.isShowRealTime());
        assertFalse(config.isCoordinateCommaSeparator());
        assertEquals(1.0f, config.getTextScale());
        assertEquals(10, config.getLineSpacing());
    }

    @Test
    void coordinateCommaSeparatorCanBeEnabled() {
        ModConfig config = new ModConfig(tempDir.resolve("fpsxyzs.json"));

        config.setCoordinateCommaSeparator(true);

        assertTrue(config.isCoordinateCommaSeparator());
    }
    @Test
    void defaultTextColorIsOpaqueWhite() {
        ModConfig config = new ModConfig(tempDir.resolve("fpsxyzs.json"));

        assertEquals(0xFFFFFFFF, config.getTextColor());
    }

    @Test
    void legacyRgbTextColorIsUpgradedToOpaqueArgb() {
        ModConfig config = new ModConfig(tempDir.resolve("fpsxyzs.json"));
        config.setTextColor(0xFFFFFF);

        assertEquals(0xFFFFFFFF, config.getTextColor());
    }
}
