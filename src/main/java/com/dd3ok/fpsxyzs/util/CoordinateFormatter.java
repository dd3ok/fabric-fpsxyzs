package com.dd3ok.fpsxyzs.util;

public final class CoordinateFormatter {
    private CoordinateFormatter() {
    }

    public static String format(int x, int y, int z, String separator, boolean showFacingAxis, float yaw) {
        String safeSeparator = (separator == null || separator.isEmpty()) ? " " : separator;

        if (!showFacingAxis) {
            return x + safeSeparator + y + safeSeparator + z;
        }

        DirectionAxis axis = DirectionAxis.fromQuarter(facingQuarter(yaw));
        return x + " X" + axis.xSuffix
                + safeSeparator + y + " Y"
                + safeSeparator + z + " Z" + axis.zSuffix;
    }

    public static int facingQuarter(float yaw) {
        return Math.floorMod(Math.round(yaw / 90.0f), 4);
    }

    private enum DirectionAxis {
        SOUTH("", "+"),
        WEST("-", ""),
        NORTH("", "-"),
        EAST("+", "");

        private final String xSuffix;
        private final String zSuffix;

        DirectionAxis(String xSuffix, String zSuffix) {
            this.xSuffix = xSuffix;
            this.zSuffix = zSuffix;
        }

        private static DirectionAxis fromQuarter(int quarter) {
            return switch (quarter) {
                case 1 -> WEST;
                case 2 -> NORTH;
                case 3 -> EAST;
                default -> SOUTH;
            };
        }
    }
}
