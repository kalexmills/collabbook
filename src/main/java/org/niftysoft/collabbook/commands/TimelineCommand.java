package org.niftysoft.collabbook.commands;

import org.niftysoft.collabbook.model.ItemStore;
import picocli.CommandLine;

@CommandLine.Command(description="Timeline view", name="timeline", aliases={"ti", "i", "time"},
        mixinStandardHelpOptions = true)
public class TimelineCommand extends ViewTimelineCommand {

    public TimelineCommand(ItemStore store) {
        super(store, store.getActiveItems());
    }
}
