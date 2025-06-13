package core.utils;

import java.awt.Color;

public class Utils {
    public static Color setAlpha(Color c, float v) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (v * 255));
    }

    public static String getFormatedTime(long time) {
        return "%02d:%02d:%02d.%03d".formatted(
                ((time / 1000) * 3600) % 24, 
                ((time / 1000) / 60) % 60,
                ((time / 1000) % 60), 
                time % 1000);
    }
}
