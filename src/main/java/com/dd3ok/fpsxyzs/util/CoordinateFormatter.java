package com.dd3ok.fpsxyzs.util;

public final class CoordinateFormatter {
    private CoordinateFormatter() {
    }

    public static String format(int x, int y, int z, boolean useCommaSeparator, boolean showFacingAxis, float yaw) {
        String separator = useCommaSeparator ? ", " : " ";

        if (!showFacingAxis) {
            return x + separator + y + separator + z;
        }

        DirectionAxis axis = DirectionAxis.fromYaw(yaw);
        return x + " X" + axis.xSuffix
                + separator + y + " Y"
                + separator + z + " Z" + axis.zSuffix;
    }

    public static int facingQuarter(float yaw) {
        return Math.floorMod((int) Math.floor((yaw * 4.0f / 360.0f) + 0.5f), 4);
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

        private static DirectionAxis fromYaw(float yaw) {
            return switch (facingQuarter(yaw)) {
                case 1 -> WEST;
                case 2 -> NORTH;
                case 3 -> EAST;
                default -> SOUTH;
            };
        }
    }
}
