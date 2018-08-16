package org.niftysoft.collabbook.commands;


import org.niftysoft.collabbook.Collabbook;
import org.niftysoft.collabbook.exceptions.ItemNotFoundException;
import org.niftysoft.collabbook.exceptions.WrongTypeException;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.util.ResponseUtil;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.niftysoft.collabbook.util.AnsiUtil.grey;
import static org.niftysoft.collabbook.util.AnsiUtil.white;

@CommandLine.Command(description="Check/uncheck task", name="check", aliases={"c"},
        mixinStandardHelpOptions = true)
public class CheckCommand implements Callable<Void> {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.ParentCommand
    private Collabbook parent;

    @CommandLine.Parameters(arity="1..*", description = "List of item identifiers.", paramLabel = "items")
    Integer[] viewIds;

    protected ItemStore store;

    public CheckCommand(ItemStore store) {
        this.store = store;
    }

    @Override
    public Void call() {
        List<Integer> outIds = new ArrayList<>();

        for (int viewId : viewIds) {
            try {
                store.toggleTaskIsChecked(viewId);
                outIds.add(viewId);
            } catch(ItemNotFoundException | WrongTypeException e) {
                continue;
            }
        }

        if (outIds.isEmpty()) outIds = Arrays.asList(viewIds);

        String outIdString = String.join(", ", outIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));

        if (outIds.isEmpty()) {
            ResponseUtil.failure(">_<", white("Unable to find tasks with id(s): " + grey(outIdString) ));
        }
        ResponseUtil.success(":-)", white("Checked tasks: ")  +
                grey(outIdString));
        return null;
    }
}

