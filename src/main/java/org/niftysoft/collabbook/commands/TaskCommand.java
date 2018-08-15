package org.niftysoft.collabbook.commands;

import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.ItemStore;
import picocli.CommandLine.Command;

import java.util.List;

@Command(description="Create task", name="task", aliases={"t"}, mixinStandardHelpOptions = true)
public class TaskCommand extends ItemCreateCommand {

    public TaskCommand(ItemStore store) {
        super(store, "task");
    }

    @Override
    protected Item createItem(String description, List<String> boards) {
        return store.createTask(description, boards);
    }
}