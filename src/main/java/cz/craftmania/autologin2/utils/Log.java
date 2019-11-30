package cz.craftmania.autologin2.utils;

import cz.craftmania.autologin2.Main;

public class Log {

    public static void send(String message) {
        System.out.println(LogColor.WHITE.colorize("[AutoLogin2] ") + message);
    }

    public static void info(String message) {
        send(LogColor.BLUE.colorize("❯") + " " + LogColor.BLUE_HIGH_INTENSITY.colorize(message));
    }

    public static void warning(String message) {
        send(LogColor.YELLOW.colorize("⚠") + " " + LogColor.YELLOW_HIGH_INTENSITY.colorize(message));
    }

    public static void debug(String message) {
        if (!Main.getOptions().isDebug()) return;
        send(LogColor.CYAN.colorize("…") + " " + LogColor.CYAN_HIGH_INTENSITY.colorize(message));
    }

    public static void success(String message) {
        send(LogColor.GREEN.colorize("✔") + " " + LogColor.GREEN.colorize(message));
    }

    public static void error(String message) {
        send(LogColor.RED.colorize("✖") + " " + LogColor.RED_HIGH_INTENSITY.colorize(message));
    }

    public static void fatal(String message) {
        send(LogColor.RED.colorize("✖ FATAL") + " " + LogColor.RED_HIGH_INTENSITY.colorize(message));
    }

    public enum LogColor {

        RESET("\u001B[0m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m"),

        BLACK_HIGH_INTENSITY("\u001B[0;90m"),
        RED_HIGH_INTENSITY("\u001B[0;91m"),
        GREEN_HIGH_INTENSITY("\u001B[0;92m"),
        YELLOW_HIGH_INTENSITY("\u001B[0;93m"),
        BLUE_HIGH_INTENSITY("\u001B[0;94m"),
        PURPLE_HIGH_INTENSITY("\u001B[0;95m"),
        CYAN_HIGH_INTENSITY("\u001B[0;96m"),
        WHITE_HIGH_INTENSITY("\u001B[0;97m");

        private String code;

        LogColor(String code) {
            this.code = code;
        }

        public String colorize(String message) {
            return this.code + message + LogColor.RESET.code;
        }
    }

}
