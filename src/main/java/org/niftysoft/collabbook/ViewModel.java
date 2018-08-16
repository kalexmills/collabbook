package org.niftysoft.collabbook;

import lombok.Getter;
import lombok.Setter;
import org.niftysoft.collabbook.model.Item;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ViewModel {
    private Map<Integer, Item> viewIdToItem = new HashMap<>();
    private Map<Long, Integer> itemIdToViewId = new HashMap<>();

    @Setter
    private int numComplete;

    @Setter
    private int numTasks;

    @Setter
    private int numNotes;
}
