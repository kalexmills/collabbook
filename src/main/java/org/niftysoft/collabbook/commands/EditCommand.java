package org.niftysoft.collabbook.commands;

import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.util.DescriptionUtil;
import org.niftysoft.collabbook.util.ResponseUtil;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

import static org.niftysoft.collabbook.util.AnsiUtil.white;
import static org.niftysoft.collabbook.util.ItemUtil.isTask;

/**
 * Command used to create an Item of a specified type. Subclassees must specify the type of item to be created.
 */
@CommandLine.Command(description="Edit item", name="edit", aliases={"e"},
                     mixinStandardHelpOptions = true)
public class EditCommand implements Callable<Void> {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(arity="1", index="0", description = "Item id.", paramLabel="id")
    private long id;

    @CommandLine.Parameters(arity="1..*", index="1..*", description = "Item description.", paramLabel = "description")
    private String[] description;

    protected ItemStore store;

    public EditCommand(ItemStore store) {
        this.store = store;
    }

    @Override
    public Void call() {
        List<String> boards = DescriptionUtil.extractBoards(description);

        Item i = store.get(id);


        String desc = String.join(" ", description).trim();
        i.setDescription(desc);

        String itemName = isTask(i) ? "task" : "note";
        if (desc.isEmpty()) throw new CommandLine.ParameterException(spec.commandLine(),
                "No description found when editing " + itemName +" #" + id + ".");

        ResponseUtil.success(":-)", white("Edited " + itemName + ':') + ' ' + i.getId());
        return null;
    }
}
