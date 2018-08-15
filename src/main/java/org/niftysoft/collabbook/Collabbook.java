package org.niftysoft.collabbook;

import org.fusesource.jansi.AnsiConsole;
import org.niftysoft.collabbook.commands.NoteCommand;
import org.niftysoft.collabbook.commands.TaskCommand;
import org.niftysoft.collabbook.model.ItemStore;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Command(description="Collaborate on tasks in your git repo from the terminal.",
         name="cb", mixinStandardHelpOptions = true, version="")
public class Collabbook implements Callable<Void>  {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec cmd;

    private ItemStore store;

    public Collabbook(ItemStore store) {
        this.store = store;
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

        CommandLine cmd = new CommandLine(new Collabbook(store))
                .addSubcommand("task", new TaskCommand(store))
                .addSubcommand("note", new NoteCommand(store));

        List<Object> result = cmd.parseWithHandler(new RunAll(), args);

        ItemStore.ItemStoreFileWriter.writeItemStore(store, FILESTORE_PATH);

        AnsiConsole.systemUninstall();
        System.exit(0);
    }

    @Override
    public Void call() throws Exception {
        store.setCmdContext(cmd.commandLine());
        return null;
    }
}
