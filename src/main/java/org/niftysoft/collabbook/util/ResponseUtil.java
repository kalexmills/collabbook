package org.niftysoft.collabbook.util;

import static org.niftysoft.collabbook.util.ColorUtil.green;

public class ResponseUtil {

    public static void success(String emoticon, String message) {
        System.out.println("\n " + green(emoticon) + ' ' + message);
    }
}
