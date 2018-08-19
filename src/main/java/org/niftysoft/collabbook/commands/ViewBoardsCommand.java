package org.niftysoft.collabbook.commands;

import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.views.BoardView;

import java.util.List;
import java.util.concurrent.Callable;

public class ViewBoardsCommand extends BoardView implements Callable<Void> {

    public ViewBoardsCommand(ItemStore store, List<String> boards){
        super(store, boards);
    }

    @Override
    public Void call() throws Exception {
        showView();
        return null;
    }
}
