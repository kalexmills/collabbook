package org.niftysoft.collabbook.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DescriptionUtil {

    /**
     * Extracts a subarray of boards from passed in array, replacing each occurrence of a board by the empty string.
     * @param description String[] description
     * @return String[] the list of boards detected in the array.
     */
    public static List<String> extractBoards(final String[] description) {
        List<String> boards = new ArrayList<>();
        Arrays.asList(description).replaceAll((token) -> {
            if(token.charAt(0) == '@') {
                // Handle quoted strings beginning with '@'
                String[] split = token.split("\\s+");
                boards.add(split[0]);
                split[0] = "";
                return String.join(" ", split);
            }
            return token;
        });
        return boards;
    }
}
