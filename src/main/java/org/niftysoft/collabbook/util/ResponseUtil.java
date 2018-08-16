package org.niftysoft.collabbook.util;

import static org.niftysoft.collabbook.util.AnsiUtil.green;
import static org.niftysoft.collabbook.util.AnsiUtil.red;

public class ResponseUtil {

    public static void success(String emoticon, String message) {
        System.out.println("\n " + green(emoticon) + "  " + message);
    }

    public static void failure(String emoticon, String message) {
        System.out.println("\n " + red(emoticon) + " " + message);
    }
}
