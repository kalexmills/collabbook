package org.niftysoft.collabbook.commands;

import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.util.ResponseUtil;
import picocli.CommandLine;

import java.util.Collection;

import static org.niftysoft.collabbook.util.AnsiUtil.grey;
import static org.niftysoft.collabbook.util.AnsiUtil.white;


@CommandLine.Command(description="Star/unstar task", name="star", aliases={"s"},
        mixinStandardHelpOptions = true)
public class StarCommand extends ItemToggleCommand {

    public StarCommand(ItemStore store) {
        super(store);
    }

    @Override
    protected void toggleParameter(long taskId)  {
        store.toggleItemIsStarred(taskId);
    }

    protected void reportSuccess(Collection<Integer> viewIdsSucceeded) {
        ResponseUtil.success(":-)", white("Starred tasks: ")  + grey(joinIds(viewIdsSucceeded)));
    }

    protected void reportFailure(Collection<Integer> viewIdsTried) {
        ResponseUtil.failure(">_<", white("Unable to find items with id(s): " + grey(joinIds(viewIdsTried))));
    }
}

