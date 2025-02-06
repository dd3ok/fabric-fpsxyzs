package com.dd3ok.fpsxyzs.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    private static final DateTimeFormatter REAL_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    // 게임 시간 포맷팅을 위한 상수
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int GAME_TIME_OFFSET = 6;

    // StringBuilder 재사용 (단일 스레드용)
    private static final StringBuilder timeBuilder = new StringBuilder(5);

    // 실제 시간 캐싱
    private static String cachedRealTime = "";
    private static long lastRealTimeUpdate = 0;
    private static final long REAL_TIME_UPDATE_INTERVAL = 1000; // 1초

    public static String formatGameTime(long timeOfDay) {
        int hours = (int)((timeOfDay / 1000 + GAME_TIME_OFFSET) % HOURS_PER_DAY);
        int minutes = (int)((timeOfDay % 1000) * MINUTES_PER_HOUR / 1000);

        timeBuilder.setLength(0);

        if (hours < 10) timeBuilder.append('0');
        timeBuilder.append(hours).append(':');
        if (minutes < 10) timeBuilder.append('0');
        timeBuilder.append(minutes);

        return timeBuilder.toString();
    }

    public static String formatRealTime() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRealTimeUpdate >= REAL_TIME_UPDATE_INTERVAL) {
            cachedRealTime = LocalDateTime.now().format(REAL_TIME_FORMAT);
            lastRealTimeUpdate = currentTime;
        }
        return cachedRealTime;
    }
}
