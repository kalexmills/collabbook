package org.niftysoft.collabbook.commands;

import org.niftysoft.collabbook.model.ItemStore;
import picocli.CommandLine;

@CommandLine.Command(description="Display archived items", name="archive", aliases={"a"},
        mixinStandardHelpOptions = true)
public class ArchiveCommand extends ViewTimelineCommand {

    public ArchiveCommand(ItemStore store) {
        super(store, store.itemsInBoard(ItemStore.ARCHIVE_BOARD));
    }
}
