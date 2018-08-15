package org.niftysoft.collabbook.commands;

import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.ItemStore;
import picocli.CommandLine.Command;

import java.util.List;
import java.util.concurrent.Callable;

@Command(description="Create note", name="note", aliases={"n"})
public class NoteCommand extends ItemCreateCommand {

    public NoteCommand(ItemStore store) {
        super(store, "note");
    }

    @Override
    protected Item createItem(String description, List<String> boards) {
        return store.createNote(description, boards);
    }
}
