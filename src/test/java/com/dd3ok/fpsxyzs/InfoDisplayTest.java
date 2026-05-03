package com.dd3ok.fpsxyzs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfoDisplayTest {
    @Test
    void fpsUpdatesOncePerSecond() {
        assertEquals(1000, InfoDisplay.fpsUpdateIntervalMillis());
    }

    @Test
    void computesTopLeftX() {
        assertEquals(5, InfoDisplay.computeTextX(ModConfig.Position.TOP_LEFT, 200, 40));
    }

    @Test
    void computesTopCenterX() {
        assertEquals(80, InfoDisplay.computeTextX(ModConfig.Position.TOP_CENTER, 200, 40));
    }

    @Test
    void computesTopRightX() {
        assertEquals(155, InfoDisplay.computeTextX(ModConfig.Position.TOP_RIGHT, 200, 40));
    }
}
