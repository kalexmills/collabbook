package org.niftysoft.collabbook;

import org.fusesource.jansi.AnsiConsole;
import org.niftysoft.collabbook.commands.*;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.views.BoardView;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

@Command(description="Collaborate on tasks in your git repo from the terminal.",
         name="cb", mixinStandardHelpOptions = true, version="")
public class Collabbook implements Callable<Void>  {

    // TODO: Make this configurable
    private static final Path FILESTORE_PATH = Paths.get(".collabbook");

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec cmd;

    private BoardView defaultView;

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
                    .addSubcommand("check", new CheckCommand(store))
                    .addSubcommand("star", new StarCommand(store))
                    .addSubcommand("delete", new DeleteCommand(store));

            cmd.setUnmatchedArgumentsAllowed(true);
            cmd.setUnmatchedOptionsArePositionalParams(true);

            cmd.parseWithHandler(new RunAll(), args);

            ItemStore.ItemStoreFileWriter.writeItemStore(store, FILESTORE_PATH);

        } finally {
            AnsiConsole.systemUninstall();
        }
        System.exit(0);
    }

    @Override
    public Void call() {
        store.setCmdContext(cmd.commandLine());

        defaultView = new BoardView(store);

        // First... compile the view. this object will be used by all other commands.
        List<String> boards = new ArrayList<>(store.getBoards());

        boards.removeAll(Arrays.asList(ItemStore.ARCHIVE_BOARD, ItemStore.DEFAULT_BOARD));
        boards.add(0, ItemStore.DEFAULT_BOARD);

        if (!cmd.commandLine().getParseResult().hasSubcommand()) {
            defaultView.showView(boards);
        }
        return null;
    }
}
