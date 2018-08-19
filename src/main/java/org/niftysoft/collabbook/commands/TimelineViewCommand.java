package org.niftysoft.collabbook.commands;

import org.niftysoft.collabbook.model.Item;
import org.niftysoft.collabbook.model.ItemStore;
import org.niftysoft.collabbook.views.SectionView;
import picocli.CommandLine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.stream.Collectors.groupingBy;

@CommandLine.Command(description="Timeline view", name="timeline", aliases={"ti", "i", "time"},
        mixinStandardHelpOptions = true)
public class TimelineViewCommand extends SectionView implements Callable<Void> {

    public TimelineViewCommand(ItemStore store) {
        super(store);
    }

    protected List<Section> initializeSections() {
        List<Item> items = store.getActiveItems();
        Map<LocalDate, List<Item>> itemsByDate = items.stream()
                .collect(groupingBy((Item item) -> item.getDate().toLocalDate()));

        List<Section> result = new ArrayList<>();
        List<LocalDate> sortedDates = new ArrayList<>(itemsByDate.keySet());

        sortedDates.sort(Comparator.naturalOrder());
        for (LocalDate date : sortedDates) {
            Section section = new Section();

            section.setSectionHeading(date.format(DateTimeFormatter.ofPattern("EEE MMM dd yyyy")));

            List<Item> itemsForDate = itemsByDate.get(date);
            itemsForDate.sort(Comparator.comparing(item -> item.getDate()));
            section.setItems(itemsForDate);

            result.add(section);
        }
        return result;
    }

    public Void call() {
        showView();
        return null;
    }
}
