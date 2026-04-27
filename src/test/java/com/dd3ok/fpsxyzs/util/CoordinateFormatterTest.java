package com.dd3ok.fpsxyzs.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinateFormatterTest {
    @Test
    void formatsCoordinatesWithSpaceSeparatorByDefault() {
        assertEquals("10 64 -20", CoordinateFormatter.format(10, 64, -20, " ", false, 0));
    }

    @Test
    void formatsCoordinatesWithCustomSeparator() {
        assertEquals("10, 64, -20", CoordinateFormatter.format(10, 64, -20, ", ", false, 0));
    }

    @Test
    void appendsAxisSignsForFacingSouth() {
        assertEquals("10 X | 64 Y | -20 Z+", CoordinateFormatter.format(10, 64, -20, " | ", true, 0));
    }

    @Test
    void appendsAxisSignsForFacingWest() {
        assertEquals("10 X- / 64 Y / -20 Z", CoordinateFormatter.format(10, 64, -20, " / ", true, 90));
    }

    @Test
    void appendsAxisSignsForFacingNorth() {
        assertEquals("10 X / 64 Y / -20 Z-", CoordinateFormatter.format(10, 64, -20, " / ", true, 180));
    }

    @Test
    void appendsAxisSignsForFacingEast() {
        assertEquals("10 X+ / 64 Y / -20 Z", CoordinateFormatter.format(10, 64, -20, " / ", true, 270));
    }
}
