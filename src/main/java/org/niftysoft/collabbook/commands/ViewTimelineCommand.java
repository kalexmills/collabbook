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

public class ViewTimelineCommand extends SectionView implements Callable<Void> {

    private List<Item> items;

    public ViewTimelineCommand(ItemStore store, List<Item> itemsInTimelineView) {
        super(store);
        this.items = itemsInTimelineView;
    }

    protected List<Section> initializeSections() {
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
