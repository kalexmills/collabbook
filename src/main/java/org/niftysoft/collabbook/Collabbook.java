package org.niftysoft.collabbook;

import org.fusesource.jansi.AnsiConsole;
import org.niftysoft.collabbook.commands.CheckCommand;
import org.niftysoft.collabbook.commands.NoteCommand;
import org.niftysoft.collabbook.commands.TaskCommand;
import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.model.Task;
import org.niftysoft.collabbook.util.ItemUtil;
import org.niftysoft.collabbook.util.ResponseUtil;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

import static org.niftysoft.collabbook.util.AnsiUtil.*;
import static org.niftysoft.collabbook.util.ItemUtil.isTask;

@Command(description="Collaborate on tasks in your git repo from the terminal.",
         name="cb", mixinStandardHelpOptions = true, version="")
public class Collabbook implements Callable<Void>  {

    // TODO: Make this configurable
    private static final Path FILESTORE_PATH = Paths.get(".collabbook");

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec cmd;

    private ViewModel view;

    private ItemStore store;

    public Collabbook(ItemStore store) {
        this.store = store;
    }

    public static void main(String[] args) throws Exception {

        ItemStore store = new ItemStore();
        try {
            ItemStore.ItemStoreFileReader.readItemStore(store, FILESTORE_PATH);
        } catch (NoSuchFileException e) {
            Files.createFile(FILESTORE_PATH);
        }

        AnsiConsole.systemInstall();
        try {
            CommandLine cmd = new CommandLine(new Collabbook(store))
                    .addSubcommand("task", new TaskCommand(store))
                    .addSubcommand("note", new NoteCommand(store))
                    .addSubcommand("check", new CheckCommand(store));

            List<Object> result = cmd.parseWithHandler(new RunAll(), args);

            ItemStore.ItemStoreFileWriter.writeItemStore(store, FILESTORE_PATH);

        } finally {
            AnsiConsole.systemUninstall();
        }
        System.exit(0);
    }

    @Override
    public Void call() {
        store.setCmdContext(cmd.commandLine());

        // First... compile the view. this object will be used by all other commands.
        ArrayList<String> boards = new ArrayList<>(store.getBoards());

        boards.removeAll(Arrays.asList(ItemStore.ARCHIVE_BOARD, ItemStore.DEFAULT_BOARD));
        boards.add(0, ItemStore.DEFAULT_BOARD);

        initializeView(boards);

        if (!cmd.commandLine().getParseResult().hasSubcommand()) {
            // If no subcommand has been requested, show the present state of the tasks
            if (!boards.isEmpty() && !store.itemsInBoards(boards.toArray(new String[0])).isEmpty()) {
                System.out.println();
                showItemsInBoards(boards);
                showSummaryFooter();
            } else {
                System.out.println();
                ResponseUtil.success("\\(^_^)/", "All done!");
                System.out.println();
            }
        }
        return null;
    }

    private void initializeView(List<String> boards) {
        view = new ViewModel();
        int nComplete = 0, nTasks = 0, nNotes = 0;
        int i = 1;
        for (String board : boards) {
            List<Item> items = store.itemsInBoards(board);
            if (!items.isEmpty()) {
                for (Item item : store.itemsInBoards(board)) {
                    view.getItemIdToViewId().put(item.getId(), i);
                    view.getViewIdToItem().put(i++, item);

                    if (isTask(item)) {
                        if (((Task) item).isCompleted()) nComplete++;
                        nTasks++;
                    } else {
                        nNotes++;
                    }
                }
            }
        }
        view.setNumComplete(nComplete);
        view.setNumTasks(nTasks);
        view.setNumNotes(nNotes);
    }

    private void showItemsInBoards(List<String> boards) {
        // TODO: Cleanup & make use of view properly.
        for (String board : boards) {
            List<Item> items = store.itemsInBoards(board);
            if (!items.isEmpty()) {
                List<String> tasks = new LinkedList<>();
                int tasksInBoard = 0; int completeInBoard = 0;
                for (Item item : store.itemsInBoards(board)) {
                    int viewId = view.getItemIdToViewId().get(item.getId());
                    tasks.add("  " + grey(String.format("%4d.", viewId)) + " " + checkbox(item) + " " + description(item));
                }
                showBoardHeading(board, completeInBoard, tasksInBoard);
                tasks.forEach(System.out::println);
                System.out.println();
            }
        }
    }

    private void showSummaryFooter() {
        int done = view.getNumComplete();
        int pending = view.getNumTasks() - view.getNumComplete();
        int notes = view.getNumNotes();
        int tasks = view.getNumTasks();

        System.out.printf("  %d%% of all tasks complete.\n", (int)((100.0 * done) / (1.0 * tasks)));
        System.out.println("  " + String.join(grey(" - "), new String[] {
                green(""  + done)            + grey(" done"),
                yellow("" + pending) + grey(" pending"),
                blue(""   + notes)                    + grey(" notes"),
        }));
        System.out.println();
    }

    private String checkbox(Item i) {
        return ItemUtil.processBySubclass(i,
                (task) -> task.isCompleted() ? grey("[")+green("x")+grey("]") : grey("[ ]"),
                (note) -> white(" - ")
        );
    }

    private String description(Item i) {
        return ItemUtil.processBySubclass(i,
                (task) -> task.isCompleted() ? grey(task.getDescription()) : task.getDescription(),
                (note) -> note.getDescription()
        );
    }

    private void showBoardHeading(String board, int complete, int total) {
        System.out.println("  " + white(board) + " " + grey("[" + complete + "/" + total + "]"));
    }


    public ViewModel getView() {
        return view;
    }
}
