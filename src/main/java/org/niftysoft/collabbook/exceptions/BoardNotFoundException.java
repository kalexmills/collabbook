package org.niftysoft.collabbook.exceptions;

import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

@Getter
public class BoardNotFoundException extends ParameterException {
    private String name;

    public BoardNotFoundException(CommandLine cmd, String name) {
        super(cmd, "Board " + name + " not found.");
        this.name = name;
    }
}
