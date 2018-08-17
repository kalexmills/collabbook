package org.niftysoft.collabbook.util;

import org.fusesource.jansi.Ansi;

import java.util.function.Function;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Utility class for wrapping strings in colors and returning to the default.
 */
public class AnsiUtil {
    public static String cyan(String str) {
        return colorize(str, Ansi::fgCyan);
    }

    public static String blue(String str) {
        return colorize(str, Ansi::fgBlue);
    }

    public static String yellow(String str) {
        return colorize(str, Ansi::fgBrightYellow);
    }

    public static String red(String str) {
        return colorize(str, Ansi::fgRed);
    }

    public static String grey(String str) { return colorize(str, Ansi::fgBrightBlack); }

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

    public static String underline(String str)  {
        return ansi().a(Ansi.Attribute.UNDERLINE).a(str).a(Ansi.Attribute.UNDERLINE_OFF).toString();
    }

    public static String emph(String  str) {
        return ansi().a(Ansi.Attribute.ITALIC).a(str).a(Ansi.Attribute.ITALIC_OFF).toString();
    }

    public static String strong(String str) {
        return ansi().bold().a(str).boldOff().toString();
    }
}
