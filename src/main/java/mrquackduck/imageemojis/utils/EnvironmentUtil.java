package mrquackduck.imageemojis.utils;

public class EnvironmentUtil {
    public static boolean isSpigot() {
        try {
            Class.forName("org.bukkit.entity.Player$Spigot");
            return true;
        } catch (Throwable tr) {
            return false;
        }
    }

    public static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            return true;
        } catch (Throwable tr) {
            return false;
        }
    }
}
