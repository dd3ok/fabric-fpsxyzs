package com.dd3ok.fpsxyzs.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinateFormatterTest {
    @Test
    void formatsCoordinatesWithSpaceSeparatorByDefault() {
        assertEquals("10 64 -20", CoordinateFormatter.format(10, 64, -20, false, false, 0));
    }

    @Test
    void formatsCoordinatesWithCommaSeparatorWhenEnabled() {
        assertEquals("10, 64, -20", CoordinateFormatter.format(10, 64, -20, true, false, 0));
    }

    @Test
    void appendsAxisSignsForFacingSouth() {
        assertEquals("10 X 64 Y -20 Z+", CoordinateFormatter.format(10, 64, -20, false, true, 0));
    }

    @Test
    void appendsAxisSignsForFacingWest() {
        assertEquals("10 X- 64 Y -20 Z", CoordinateFormatter.format(10, 64, -20, false, true, 90));
    }

    @Test
    void appendsAxisSignsForFacingNorth() {
        assertEquals("10 X 64 Y -20 Z-", CoordinateFormatter.format(10, 64, -20, false, true, 180));
    }

    @Test
    void appendsAxisSignsForFacingEast() {
        assertEquals("10 X+ 64 Y -20 Z", CoordinateFormatter.format(10, 64, -20, false, true, 270));
    }

    @Test
    void appendsAxisSignsAroundFractionalEastYaw() {
        assertEquals("10 X+ 64 Y -20 Z", CoordinateFormatter.format(10, 64, -20, false, true, -89.6f));
        assertEquals("10 X+ 64 Y -20 Z", CoordinateFormatter.format(10, 64, -20, false, true, 270.4f));
    }

    @Test
    void appendsAxisSignsAroundFractionalSouthYaw() {
        assertEquals("10 X 64 Y -20 Z+", CoordinateFormatter.format(10, 64, -20, false, true, -0.4f));
        assertEquals("10 X 64 Y -20 Z+", CoordinateFormatter.format(10, 64, -20, false, true, 359.6f));
    }
}
