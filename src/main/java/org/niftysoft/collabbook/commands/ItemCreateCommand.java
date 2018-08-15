package org.niftysoft.collabbook.commands;

import org.niftysoft.collabbook.Collabbook;
import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.util.DescriptionUtil;
import org.niftysoft.collabbook.util.ResponseUtil;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

import static org.niftysoft.collabbook.util.ColorUtil.white;

public abstract class ItemCreateCommand implements Callable<Void> {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.ParentCommand
    private Collabbook parent;

    @CommandLine.Parameters(arity="1..*", description = "Item description.", paramLabel = "description")
    String[] description;

    protected ItemStore store;

    private String itemName;

    public ItemCreateCommand(ItemStore store, String itemName) {
        this.store = store;
        this.itemName = itemName;
    }

    protected abstract Item createItem(String description, List<String> boards);

    @Override
    public Void call() {
        List<String> boards = DescriptionUtil.extractBoards(description);

        String desc = String.join(" ", description).trim();
        if (desc.isEmpty()) throw new CommandLine.ParameterException(spec.commandLine(), "No description found for your " + itemName +".");

        Item i = createItem(String.join(" ", description), boards);

        ResponseUtil.success(":-)", white("Created " + itemName + ':') + ' ' + i.getId());
        return null;
    }
}
