package org.niftysoft.collabbook.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Item {
    private long id;
    private LocalDateTime date = LocalDateTime.now();
    private String description;
    private boolean isStarred = false;

    Item(long id) { this.id = id;}
}
