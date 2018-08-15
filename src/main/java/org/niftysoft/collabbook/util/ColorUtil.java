package org.niftysoft.collabbook.util;

import org.fusesource.jansi.Ansi;

import java.util.function.Function;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Utility class for wrapping strings in colors and returning to the default.
 */
public class ColorUtil {
    public static String cyan(String str) {
        return colorize(str, Ansi::fgCyan);
    }

    public static String yellow(String str) {
        return colorize(str, Ansi::fgYellow);
    }

    public static String red(String str) {
        return colorize(str, Ansi::fgRed);
    }

    public static String magenta(String str) {
        return colorize(str, Ansi::fgMagenta);
    }

    public static String green(String str) {
        return colorize(str, Ansi::fgGreen);
    }

    public static String white(String str) {
        return colorize(str, Ansi::fgBrightDefault);
    }

    public static String colorize(String str, Function<Ansi, Ansi> color) {
        return color.apply(ansi()).a(str).fgDefault().toString();
    }
}
