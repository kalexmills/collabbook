package org.niftysoft.collabbook.commands;


import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.util.ResponseUtil;
import picocli.CommandLine;

import java.util.Collection;

import static org.niftysoft.collabbook.util.AnsiUtil.grey;
import static org.niftysoft.collabbook.util.AnsiUtil.white;


@CommandLine.Command(description="Check/uncheck task", name="check", aliases={"c"},
        mixinStandardHelpOptions = true)
public class CheckCommand extends ItemToggleCommand {

    public CheckCommand(ItemStore store) {
        super(store);
    }

    @Override
    protected void toggleParameter(long taskId) {
        store.toggleTaskIsChecked(taskId);
    }


    protected void reportSuccess(Collection<Integer> viewIdsSucceeded) {
        ResponseUtil.success(":-)", white("Checked tasks: ")  + grey(joinIds(viewIdsSucceeded)));
    }

    protected void reportFailure(Collection<Integer> viewIdsTried) {
        ResponseUtil.failure(">_<", white("Unable to find tasks with id(s): " + grey(joinIds(viewIdsTried))));
    }
}

