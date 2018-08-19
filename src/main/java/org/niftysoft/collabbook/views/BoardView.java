package org.niftysoft.collabbook.views;

import org.niftysoft.collabbook.model.ItemStore;
import java.util.List;
import java.util.stream.Collectors;

public class BoardView extends SectionView {

    private List<String> boards;

    public BoardView(ItemStore store, List<String> boards) {
        super(store);
        this.boards = boards;
    }

    protected List<Section> initializeSections() {
        return boards.stream()
                .map((board) -> new SectionView.Section(board, store.itemsInBoard(board)))
                .collect(Collectors.toList());
    }
}
