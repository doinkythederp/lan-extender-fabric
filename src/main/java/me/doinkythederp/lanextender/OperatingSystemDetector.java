package me.doinkythederp.lanextender;

public class OperatingSystemDetector {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
