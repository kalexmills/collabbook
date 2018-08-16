package org.niftysoft.collabbook.model;

import org.niftysoft.collabbook.exceptions.BoardNotFoundException;
import org.niftysoft.collabbook.exceptions.ItemNotFoundException;
import org.niftysoft.collabbook.exceptions.WrongTypeException;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ItemStore {
    public static final String DEFAULT_BOARD = "My Board";
    public static final String ARCHIVE_BOARD = "archive";

    private long nextId;

    // Each item appears in the items list
    private SortedMap<Long, Item> items;
    // Item can only appear on one board. There is always a "default" and "archive" board.
    private SortedMap<String, SortedSet<Long>> boards;

    private CommandLine cmd;

    public ItemStore() {
        items = new TreeMap<>();
        boards = new TreeMap<>();

        boards.put(DEFAULT_BOARD, new TreeSet<>());
        boards.put(ARCHIVE_BOARD, new TreeSet<>());

        nextId = 0L;
    }

    public void setCmdContext(CommandLine cmd) {
        this.cmd = cmd;
    }

    /**
     * Toggles the value of isStarred on an item.
     * @param id long id of item to star
     * @throws ItemNotFoundException if no item with id is found.
     */
    public void toggleItemIsStarred(long id) throws ItemNotFoundException {
        if (!items.containsKey(id)) throw new ItemNotFoundException(cmd, id);
        Item i = items.get(id);
        i.setStarred(!i.isStarred());
    }

    /**
     * Toggles the value of isChecked on an item.
     * @param id long id of item to check
     * @throws ItemNotFoundException if item is not found
     * @throws WrongTypeException if item was found, but is not a task.
     */
    public void toggleTaskIsChecked(long id) throws ItemNotFoundException, WrongTypeException {
        if (!items.containsKey(id)) throw new ItemNotFoundException(cmd, id);
        Item i = items.get(id);
        if (!i.getClass().equals(Task.class)) throw new WrongTypeException(cmd, Task.class);
        Task t = (Task)i;
        t.setCompleted(!t.isCompleted());
    }

    public Set<String> getBoards() {
        return Collections.unmodifiableSet(boards.keySet());
    }

    /**
     * @param id long item id to remove.
     * @return Item the deleted item, or null if no item was deleted.
     */
    public Item deleteItem(long id) {
        return items.remove(id);
    }

    /**
     * @return List of Items by date created
     */
    public List<Item> listByDateCreated() {
        List<Item> result = new ArrayList<>(items.values());
        result.sort(Comparator.comparing((i) -> i.getDate()));
        return result;
    }

    /**
     * @param boardArr String... board name(s)
     * @return List<Item> the items contained in the requested board.
     * @throws BoardNotFoundException
     */
    public List<Item> itemsInBoards(String... boardArr) throws BoardNotFoundException {
        List<String> boardList = new LinkedList<String>(Arrays.asList(boardArr));
        boardList.removeIf((board) -> !boards.keySet().contains(board));
        if (boardList.isEmpty())
            throw new BoardNotFoundException(cmd, String.join(",", boardArr));

        return boardList.stream()
                .map((board) -> boards.get(board))
                .flatMap(set -> set.stream())
                .map(id -> items.get(id))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new item, setting its ID, storing it in the local store, and adding it to all specified boards.
     * @param description String description of the task.
     * @return Note the item which was created.
     */
    public Note createNote(String description, Collection<String> boards) {
        Note result = newNote();
        result.setDescription(description);

        items.put(result.getId(), result);

        if (boards.isEmpty())
            addItemToBoard(result.getId(), DEFAULT_BOARD);
        else {
            for (String boardName : boards) {
                addItemToBoard(result.getId(), boardName);
            }
        }

        return result;
    }

    /**
     * Creates a new item, setting its ID, storing it in the local store, and adding it to all specified boards.
     * @param description String description of the task.
     * @return Task the item which was created.
     */
    public Task createTask(String description, Collection<String> boards) {
        Task result = newTask();
        result.setDescription(description);

        items.put(result.getId(), result);

        if (boards.isEmpty())
            addItemToBoard(result.getId(), DEFAULT_BOARD);
        else {
            for (String boardName : boards) {
                addItemToBoard(result.getId(), boardName);
            }
        }

        return result;
    }

    public int numActiveItems() {
        return items.size() - boards.get(ARCHIVE_BOARD).size();
    }

    /**
     * Adds an item to a board, creating the board if it didn't already exist.
     *
     * @param itemId long id of item stored by this class.
     * @param board String board to add item to
     */
    public void addItemToBoard(long itemId, String board) {
        lazyGetBoard(board).add(itemId);
    }

    /**
     * Private convenience method to create a new Item and increment the id.
     * @return Item a new item with a unique ID.
     */
    private Task newTask() { return new Task(nextId++); }

    /**
     * Private convenience method to create a new Item and increment the id.
     * @return Item a new item with a unique ID.
     */
    private Note newNote() { return new Note(nextId++); }

    private Set<Long> lazyGetBoard(String name) {
        return boards.computeIfAbsent(name, (k) -> new TreeSet<>());
    }

    /**
     * Writes an item store to an LF-delimited file, for playing nicely with VCS systems.
     */
    public static class ItemStoreFileWriter {
        public static void writeItemStore(ItemStore store, Path pathToWrite) throws IOException {
            try (BufferedWriter w = Files.newBufferedWriter(pathToWrite, StandardOpenOption.TRUNCATE_EXISTING)) {
                StringBuilder b = new StringBuilder();

                writeItems(store.items.values(), b);
                b.append("=====\n");
                writeBoards(store.boards, b);

                w.write(b.toString());
            }
        }

        private static void writeItems(Iterable<Item> items, StringBuilder b) {
            for (Item item : items){
                // ID
                b.append(item.getId()).append('\n');

                // Type ID
                if (item.getClass().equals(Note.class)) {
                    b.append('N');
                } else if (item.getClass().equals(Task.class)) {
                    b.append('T');
                    // isCompleted
                    b.append('\n');
                    b.append(((Task)item).isCompleted() ? 'T' : 'F');
                } else {
                    throw new IllegalStateException("Unexpected subclass of item encountered" + item.getClass().getCanonicalName());
                }
                b.append('\n');

                // isStarred
                b.append(item.isStarred() ? 'T' : 'F').append('\n');

                // Date
                b.append(item.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append('\n');

                // Description
                b.append(item.getDescription()).append('\n');

                b.append("---\n");
            }
        }

        private static void writeBoards(SortedMap<String, SortedSet<Long>> boards, StringBuilder b) {
            for (Map.Entry<String, SortedSet<Long>> entry : boards.entrySet()) {
                // Board name
                b.append(entry.getKey()).append('\n');

                // List of IDs contained in board.
                for (long id : entry.getValue()) {
                    b.append(id).append('\n');
                }
                b.append("---\n");
            }
        }
    }

    /**
     * Reads an item store from an LF-delimited file, for playing nicely with VCS systems.
     */
    public static class ItemStoreFileReader {
        public static void readItemStore(ItemStore store, Path pathToRead) throws IOException {
            String str = new String(Files.readAllBytes(pathToRead));

            if (str.isEmpty()) return;

            String[] objects = str.split("=====\\n");

            if (objects.length != 2) throw new IOException("Could not parse " + pathToRead + " as valid ItemStore.");

            try {
                long maxId = readItems(store.items, objects[0]);

                store.nextId = maxId+1;

                readBoards(store.boards, objects[1]);
            } catch (Exception e ) {
                throw new RuntimeException("IllegalStateException found when parsing " + pathToRead, e);
            }
        }

        private static long readItems(SortedMap<Long, Item> items, String str) {
            String[] itemTokens = str.split("---\\n");

            long maxId = 0L;
            for (String itemToken : itemTokens) {

                Item item = readItem(itemToken);
                maxId = Math.max(item.getId(), maxId);
                items.put(item.getId(), item);
            }
            return maxId;
        }

        private static Item readItem(String itemToken) {
            Item item;
            String[] dataTokens = itemToken.split("\\n");

            int i = 0;
            long id = Long.valueOf(dataTokens[i++]);

            String typeToken = dataTokens[i++];
            if (typeToken.charAt(0) == 'N') {
                item = new Note(id);
            } else if (typeToken.charAt(0) == 'T') {
                item = new Task(id);
                ((Task) item).setCompleted(dataTokens[i++].charAt(0) == 'T');
            } else {
                throw new IllegalStateException("Unexpected type ID encountered: " + dataTokens[1]);
            }
            item.setStarred(dataTokens[i++].charAt(0) == 'T');
            item.setDate(LocalDateTime.parse(dataTokens[i++]));
            item.setDescription(dataTokens[i++]);

            return item;
        }

        private static void readBoards(SortedMap<String, SortedSet<Long>> boards, String str) {
            String[] boardTokens = str.split("---\\n");

            for (String boardToken : boardTokens) {
                readBoard(boards, boardToken);
            }
        }

        private static void readBoard(SortedMap<String, SortedSet<Long>> boards, String boardToken) {
            String[] dataTokens = boardToken.split("\\n");

            String boardName = dataTokens[0];

            SortedSet<Long> idSet = new TreeSet<>();
            for (int i = 1; i < dataTokens.length; ++i) {
                idSet.add(Long.valueOf(dataTokens[i]));
            }
            boards.put(boardName, idSet);
        }
    }
}
