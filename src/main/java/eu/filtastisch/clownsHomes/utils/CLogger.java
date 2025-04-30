package eu.filtastisch.clownsHomes.utils;

import eu.filtastisch.clownsHomes.ClownsHomes;

public class CLogger {

    public static void info(String message) {
        if (!ClownsHomes.getInstance().getDefaultConfig().isDebug()) return;

        ClownsHomes.getInstance().getLogger().info(message);
    }

    public static void warn(String message) {
        if (!ClownsHomes.getInstance().getDefaultConfig().isDebug()) return;

        ClownsHomes.getInstance().getLogger().warning(message);
    }

    public static void error(String message) {
        if (!ClownsHomes.getInstance().getDefaultConfig().isDebug()) return;

        ClownsHomes.getInstance().getLogger().severe(message);
    }

    public static void info(String message, boolean logWithoutDebug) {
        if (!logWithoutDebug) return;
        if (!ClownsHomes.getInstance().getDefaultConfig().isDebug()) return;

        ClownsHomes.getInstance().getLogger().info(message);
    }

    public static void warn(String message, boolean logWithoutDebug) {
        if (!logWithoutDebug) return;
        if (!ClownsHomes.getInstance().getDefaultConfig().isDebug()) return;

        ClownsHomes.getInstance().getLogger().warning(message);
    }

    public static void error(String message, boolean logWithoutDebug) {
        if (!logWithoutDebug) return;
        if (!ClownsHomes.getInstance().getDefaultConfig().isDebug()) return;

        ClownsHomes.getInstance().getLogger().severe(message);
    }

}
