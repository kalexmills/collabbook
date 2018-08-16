package org.niftysoft.collabbook;

import org.fusesource.jansi.AnsiConsole;
import org.niftysoft.collabbook.commands.CheckCommand;
import org.niftysoft.collabbook.commands.NoteCommand;
import org.niftysoft.collabbook.commands.TaskCommand;
import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.model.Task;
import org.niftysoft.collabbook.util.ItemUtil;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

import static org.fusesource.jansi.Ansi.ansi;
import static org.niftysoft.collabbook.util.AnsiUtil.*;
import static org.niftysoft.collabbook.util.ItemUtil.isTask;

@Command(description="Collaborate on tasks in your git repo from the terminal.",
         name="cb", mixinStandardHelpOptions = true, version="")
public class Collabbook implements Callable<Void>  {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec cmd;

    private Map<Integer, Item> view;

    private ItemStore store;

    public Collabbook(ItemStore store) {
        this.store = store;
    }

    public Map<Integer, Item> getView() {
        return view;
    }

    private static final Path FILESTORE_PATH = Paths.get(".collabbook");

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

        if (!cmd.commandLine().getParseResult().hasSubcommand()) {
            ArrayList<String> boardsToShow = new ArrayList<>(store.getBoards());

            boardsToShow.removeAll(Arrays.asList(ItemStore.ARCHIVE_BOARD, ItemStore.DEFAULT_BOARD));
            boardsToShow.add(0, ItemStore.DEFAULT_BOARD);

            if (!boardsToShow.isEmpty() && !store.itemsInBoards(boardsToShow.toArray(new String[0])).isEmpty()) {
                System.out.println();
                showTasksInBoards(boardsToShow);
            } else {
                System.out.println();
                System.out.println(yellow("  All done!") + white("  ^_^"));
                System.out.println();
            }
        }
        return null;
    }

    private void showTasksInBoards(List<String> boards) {
        int i = 1;
        int nCompleteTasks = 0, nTasks = 0, nNotes = 0;
        view = new HashMap<>();
        for (String board : boards) {
            List<Item> items = store.itemsInBoards(board);
            if (!items.isEmpty()) {
                List<String> tasks = new LinkedList<>();
                int tasksInBoard = 0; int completeInBoard = 0;
                for (Item item : store.itemsInBoards(board)) {
                    view.put(i, item);
                    tasks.add("  " + grey(String.format("%4d.", i++)) + " " + checkbox(item) + " " + description(item));

                    if (isTask(item)) {
                        tasksInBoard++;
                        if (((Task) item).isCompleted()) completeInBoard++;
                    } else {
                        nNotes++;
                    }
                }
                showBoardHeading(board, completeInBoard, tasksInBoard);
                tasks.forEach(System.out::println);
                System.out.println();

                nCompleteTasks += completeInBoard;
                nTasks += tasksInBoard;
            }
        }

        System.out.printf("  %d%% of all tasks complete.\n", (int)((100.0*nCompleteTasks) / (1.0*nTasks)));
        System.out.println("  " + String.join(grey(" - "), new String[] {
                green(""   + nCompleteTasks)            + grey(" done"),
                yellow("" + (nTasks - nCompleteTasks)) + grey(" pending"),
                blue(""    + nNotes)                    + grey(" notes"),
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

}
