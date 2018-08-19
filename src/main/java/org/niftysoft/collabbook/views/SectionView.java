package org.niftysoft.collabbook.views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.model.Task;
import org.niftysoft.collabbook.util.AnsiUtil;
import org.niftysoft.collabbook.util.ItemUtil;
import org.niftysoft.collabbook.util.ResponseUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.niftysoft.collabbook.util.AnsiUtil.*;
import static org.niftysoft.collabbook.util.AnsiUtil.grey;
import static org.niftysoft.collabbook.util.AnsiUtil.white;

public abstract class SectionView implements View{

    protected ItemStore store;

    protected int nComplete, nTasks, nNotes;

    protected List<Section> sections;

    public SectionView(ItemStore store) {
        this.store = store;
    }

    public void showView() {
        sections = initializeSections();
        // If no subcommand has been requested, show the present state of the tasks
        if (!sections.isEmpty()) {
            System.out.println();
            showItemsInSections();
            showSummaryFooter();
        } else {
            ResponseUtil.success("\\(^_^)/", "All done!");
        }
    }

    protected abstract List<Section> initializeSections();

    private void showItemsInSections() {
        for (Section section : sections) {
            if (!section.items.isEmpty()) {
                List<String> tasks = new LinkedList<>();
                int tasksInBoard = 0; int completeInBoard = 0;
                for (Item item : section.items) {
                    if (item.getClass().equals(Task.class)) {
                        tasksInBoard++;
                        if (((Task)item).isCompleted()) completeInBoard++;
                    }
                    tasks.add("  " + grey(String.format("%4d.", item.getId())) + " " + checkbox(item) + " " + star(item)
                            + description(item) + " " + star(item));
                }
                showBoardHeading(section.sectionHeading, completeInBoard, tasksInBoard);
                tasks.forEach(System.out::println);
                System.out.println();

                nTasks += tasksInBoard;
                nComplete += completeInBoard;
                nNotes += tasks.size() - tasksInBoard;
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

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Section{
        @Getter @Setter
        private String sectionHeading;
        @Getter @Setter
        private List<Item> items = new ArrayList<>();
    }
}
