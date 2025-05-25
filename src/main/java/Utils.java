import java.awt.*;

public class Utils {
    public static Color setAlpha(Color c, float v) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (v * 255));
    }
}
