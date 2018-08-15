package org.niftysoft.collabbook.exceptions;

import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

@Getter
public class ItemNotFoundException extends ParameterException {
    private long id;

    public ItemNotFoundException(CommandLine cmd, long id) {
        super(cmd, "Item " + id + " not found.");
        this.id = id;
    }
}
