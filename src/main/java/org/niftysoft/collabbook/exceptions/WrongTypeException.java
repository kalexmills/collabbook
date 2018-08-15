package org.niftysoft.collabbook.exceptions;

import org.niftysoft.collabbook.model.Item;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

public class WrongTypeException extends ParameterException {
    private Class clazz;

    public WrongTypeException(CommandLine cmd, Class clazz) {
        super(cmd, "Wrong type passed. Expected " + clazz.getCanonicalName());
        this.clazz = clazz;
    }
}
