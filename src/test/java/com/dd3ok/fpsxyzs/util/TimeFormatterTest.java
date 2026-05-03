package com.dd3ok.fpsxyzs.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeFormatterTest {
    @Test
    void formatsMinecraftDayStartAsSixAm() {
        assertEquals("06:00", TimeFormatter.formatGameTime(0));
    }

    @Test
    void formatsMinecraftNoonAsSixPm() {
        assertEquals("18:00", TimeFormatter.formatGameTime(12000));
    }

    @Test
    void wrapsMinecraftDayAtTwentyFourThousandTicks() {
        assertEquals("05:59", TimeFormatter.formatGameTime(23999));
    }

    @Test
    void formatsPartialMinecraftHoursAsMinutes() {
        assertEquals("07:30", TimeFormatter.formatGameTime(1500));
    }
}
