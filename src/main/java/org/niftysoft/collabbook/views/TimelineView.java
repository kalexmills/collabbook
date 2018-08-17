package org.niftysoft.collabbook.views;

import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.model.Task;
import org.niftysoft.collabbook.util.AnsiUtil;
import org.niftysoft.collabbook.util.ItemUtil;
import org.niftysoft.collabbook.util.ResponseUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.niftysoft.collabbook.util.AnsiUtil.*;
import static org.niftysoft.collabbook.util.ItemUtil.isTask;

public class TimelineView {

    private ItemStore store;

    private int nComplete, nTasks, nNotes;

    private List<String> boards;

    public TimelineView(ItemStore store) {
        this.store = store;
    }

    public void showView() {
        this.boards = boards;
        initializeView();
        // If no subcommand has been requested, show the present state of the tasks
        if (!boards.isEmpty() && !store.itemsInBoards(boards.toArray(new String[0])).isEmpty()) {
            System.out.println();
            showItemsInBoards(boards);
            showSummaryFooter();
        } else {
            ResponseUtil.success("\\(^_^)/", "All done!");
        }
    }

    private void initializeView() {
        for (String board : boards) {
            List<Item> items = store.itemsInBoards(board);
            if (!items.isEmpty()) {
                for (Item item : store.itemsInBoards(board)) {
                    if (isTask(item)) {
                        if (((Task) item).isCompleted()) nComplete++;
                        nTasks++;
                    } else {
                        nNotes++;
                    }
                }
            }
        }
    }

    private void showItemsInBoards(List<String> boards) {
        // TODO: Cleanup & make use of view properly.
        for (String board : boards) {
            List<Item> items = store.itemsInBoards(board);
            if (!items.isEmpty()) {
                List<String> tasks = new LinkedList<>();
                int tasksInBoard = 0; int completeInBoard = 0;
                for (Item item : store.itemsInBoards(board)) {
                    if (item.getClass().equals(Task.class)) {
                        tasksInBoard++;
                        if (((Task)item).isCompleted()) completeInBoard++;
                    }
                    tasks.add("  " + grey(String.format("%4d.", item.getId())) + " " + checkbox(item) + " " + star(item)
                            + description(item) + " " + star(item));
                }
                showBoardHeading(board, completeInBoard, tasksInBoard);
                tasks.forEach(System.out::println);
                System.out.println();
            }
        }
    }

    private void showSummaryFooter() {
        int done = nComplete;
        int pending = nTasks - nComplete;
        int notes = nNotes;
        int tasks = nTasks;

        System.out.printf("  %d%% of all tasks complete.\n", (int)((100.0 * done) / (1.0 * tasks)));
        System.out.println("  " + String.join(grey(" - "), new String[] {
                green(""  + done)            + grey(" done"),
                yellow("" + pending) + grey(" pending"),
                blue(""   + notes)                    + grey(" notes"),
        }));
        System.out.println();
    }

    private String star(Item i) {
        return i.isStarred() ? yellow("** ") : "";
    }

    private String checkbox(Item i) {
        return ItemUtil.processBySubclass(i,
                (task) -> task.isCompleted() ? grey("[")+green("x")+grey("]") : grey("[ ]"),
                (note) -> white(" - ")
        );
    }

    private String description(Item i) {
        Function<String, String> yellowOrGrey = i.isStarred() ? AnsiUtil::yellow : AnsiUtil::white;
        return ItemUtil.processBySubclass(i,
                (task) -> task.isCompleted() ? grey(task.getDescription()) : yellowOrGrey.apply(task.getDescription()),
                (note) -> yellowOrGrey.apply(note.getDescription())
        );
    }

    private void showBoardHeading(String board, int complete, int total) {
        System.out.println("  " + white(board) + " " + grey("[" + complete + "/" + total + "]"));
    }

}
