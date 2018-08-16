package org.niftysoft.collabbook.util;

import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.Note;
import org.niftysoft.collabbook.model.Task;

import java.util.function.Function;

public class ItemUtil {

    public static <R> R processBySubclass(Item i, Function<Task, R> taskProc, Function<Note, R> noteProc) {
        if (isTask(i)) {
            return taskProc.apply((Task)i);
        } else if (isNote(i)) {
            return noteProc.apply((Note)i);
        } else {
            throw new IllegalStateException("Unexpected Item subclass " + i.getClass().getCanonicalName());
        }
    }

    public static boolean isTask(Item i) { return i.getClass().equals(Task.class); }
    public static boolean isNote(Item i) { return i.getClass().equals(Note.class); }
}
