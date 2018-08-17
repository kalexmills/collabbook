package org.niftysoft.collabbook;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ViewModel {
    private Map<Integer, Long> viewIdToItemId = new HashMap<>();
    private Map<Long, Integer> itemIdToViewId = new HashMap<>();

    @Setter
    private int numComplete;

    @Setter
    private int numTasks;

    @Setter
    private int numNotes;
}
