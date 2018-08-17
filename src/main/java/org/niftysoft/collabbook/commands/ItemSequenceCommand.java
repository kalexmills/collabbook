package org.niftysoft.collabbook.commands;


import org.niftysoft.collabbook.Collabbook;
import org.niftysoft.collabbook.ViewModel;
import org.niftysoft.collabbook.exceptions.ItemNotFoundException;
import org.niftysoft.collabbook.exceptions.WrongTypeException;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.util.ResponseUtil;
import picocli.CommandLine;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.niftysoft.collabbook.util.AnsiUtil.grey;
import static org.niftysoft.collabbook.util.AnsiUtil.white;

/**
 * Command allowing the user to toggle one parameter on a list of values.
 */
public abstract class ItemSequenceCommand implements Callable<Void> {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.ParentCommand
    private Collabbook parent;

    Set<Integer> viewIds = new HashSet<>();

    @CommandLine.Parameters(arity="1..*", description = "List of item identifiers and ranges.", paramLabel = "items")
    List<String> idsAndRanges;

    protected ItemStore store;

    private ViewModel view;

    public ItemSequenceCommand(ItemStore store) {
        this.store = store;
    }

    /**
     * @param taskId long id of task to toggle.
     */
    protected abstract void toggleParameter(long taskId);

    @Override
    public Void call() {
        parseRanges();

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
            reportFailure(idsAndRanges);
        } else {
            reportSuccess(successfulIds);
        }
        return null;
    }

    public void parseRanges() {
        for (String str : idsAndRanges) {
            String[] tokens = str.split("-");
            try {
                if (tokens.length == 1) {
                    viewIds.add(Integer.valueOf(tokens[0]));
                } else {
                    int low = Integer.valueOf(tokens[0]);
                    int high = Integer.valueOf(tokens[1]);
                    if (low > high) { int temp = low; low = high; high = temp; }

                    for (int i = low; i <= high; ++i) {
                        viewIds.add(i);
                    }
                }
            } catch (NumberFormatException e) {
                ;; // ignore these silly parameters and move on with life
            }
        }
    }

    protected abstract void reportSuccess(Collection<Integer> viewIdsSucceeded);

    protected void reportFailure(Collection<String> paramsTried) {
        ResponseUtil.failure(">_<", white("Unable to find any item id(s) among parameters: " +
                grey(String.join(", ", paramsTried))));
    }

    protected String joinIds(Collection<Integer> viewIds) {
        return String.join(", ", viewIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
    }
}

