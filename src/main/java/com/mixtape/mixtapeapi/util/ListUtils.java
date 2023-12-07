package com.mixtape.mixtapeapi.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public static <T> List<List<T>> partition(List<T> items, int partitionSize) {
        // if we don't actually need to do anything, do nothing
        if (items.size() <= partitionSize) {
            return List.of(items);
        }

        // figure out how many partitions we're going to make
        List<List<T>> table = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            // if a partition is full, then add a new one
            if (i % partitionSize == 0) {
                table.add(new ArrayList<>());
            }

            // Push this element to the end of the last list
            table.get(table.size() - 1).add(items.get(i));
        }

        return table;
    }
}
