package org.niftysoft.collabbook.commands;


import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.util.ResponseUtil;
import picocli.CommandLine;

import java.util.Collection;

import static org.niftysoft.collabbook.util.AnsiUtil.grey;
import static org.niftysoft.collabbook.util.AnsiUtil.white;


@CommandLine.Command(description="Delete item(s)", name="delete", aliases={"d", "del", "rm"},
        mixinStandardHelpOptions = true)
public class DeleteCommand extends ItemSequenceCommand {

    public DeleteCommand(ItemStore store) {
        super(store);
    }

    @Override
    protected void toggleParameter(long taskId) {
        store.deleteItem(taskId);
    }


    protected void reportSuccess(Collection<Integer> viewIdsSucceeded) {
        ResponseUtil.success(":-)", white("Deleted tasks: ")  + grey(joinIds(viewIdsSucceeded)));
    }

}

