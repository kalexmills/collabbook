package org.niftysoft.collabbook.commands;


import org.niftysoft.collabbook.Collabbook;
import org.niftysoft.collabbook.ViewModel;
import org.niftysoft.collabbook.exceptions.ItemNotFoundException;
import org.niftysoft.collabbook.exceptions.WrongTypeException;
import org.niftysoft.collabbook.model.ItemStore;
import picocli.CommandLine;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Command allowing the user to toggle one parameter on a list of values.
 */
public abstract class ItemToggleCommand implements Callable<Void> {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.ParentCommand
    private Collabbook parent;

    @CommandLine.Parameters(arity="1..*", description = "List of item identifiers.", paramLabel = "items")
    Set<Integer> viewIds;

    @CommandLine.Unmatched
    List<String> unmatched;

    protected ItemStore store;

    private ViewModel view;

    public ItemToggleCommand(ItemStore store) {
        this.store = store;
    }

    /**
     * @param taskId long id of task to toggle.
     */
    protected abstract void toggleParameter(long taskId);

    @Override
    public Void call() {
        if (unmatched != null) parseRangesFromUnmatchedParameters();

        List<Integer> successfulIds = new ArrayList<>();

        Map<Integer, Long> viewIdToItemId = parent.getView().getViewIdToItemId();
        for (int viewId : viewIds) {

            try {
                if (viewIdToItemId.containsKey(viewId)) {
                    toggleParameter(viewIdToItemId.get(viewId));
                    successfulIds.add(viewId);
                }
            } catch(ItemNotFoundException | WrongTypeException e) {
                continue;
            }
        }

        if (successfulIds.isEmpty()) {
            reportFailure(viewIds);
        } else {
            reportSuccess(successfulIds);
        }
        return null;
    }

    public void parseRangesFromUnmatchedParameters() {
        for (String str : unmatched) {
            String[] tokens = str.split("-");
            if (tokens.length == 2) {
                try {
                    int low = Integer.valueOf(tokens[0]);
                    int high = Integer.valueOf(tokens[1]);
                    if (low > high) { int temp = low; low = high; high = temp; }

                    for (int i = low; i <= high; ++i) {
                        viewIds.add(i);
                    }
                } catch (NumberFormatException e) {
                    ;; // ignore these silly parameters and move on with life
                }
            }
        }
    }

    protected abstract void reportSuccess(Collection<Integer> viewIdsSucceeded);

    protected abstract void reportFailure(Collection<Integer> viewIdsTried);

    protected String joinIds(Collection<Integer> viewIds) {
        return String.join(", ", viewIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
    }
}

