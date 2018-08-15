package org.niftysoft.collabbook.model;

import lombok.Data;

@Data
public class Task extends Item {
    private boolean isCompleted;

    Task(long id) { super(id); }
}
