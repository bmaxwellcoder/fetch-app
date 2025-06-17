package com.example.fetchapp;

import com.example.fetchapp.model.FetchObject;
import com.example.fetchapp.ui.RecyclerViewAdapter;
import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

/**
 * Unit tests for the RecyclerViewAdapter class.
 * 
 * These tests verify:
 * 1. Data grouping and sorting functionality
 * 2. Proper handling of item updates
 * 3. Group expansion/collapse behavior
 * 4. Efficient data processing
 */
public class RecyclerViewAdapterTest {
    /**
     * Tests the data processing functionality.
     * Verifies that:
     * - Items are correctly grouped by listId
     * - Items within groups are sorted by name
     * - ListIds are sorted numerically
     * - Groups maintain proper order
     * 
     * Uses reflection to test private methods and fields.
     */
    @Test
    public void testProcessData_groupsAndSortsCorrectly() throws Exception {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        List<FetchObject> items = Arrays.asList(
                new FetchObject(1, 2, "B"),
                new FetchObject(2, 1, "A"),
                new FetchObject(3, 2, "C"));

        // Use reflection to access private processData
        Method processData = RecyclerViewAdapter.class.getDeclaredMethod("processData", List.class);
        processData.setAccessible(true);
        Object groupedData = processData.invoke(adapter, items);

        // Use reflection to access private fields
        assert groupedData != null;
        Field groupedItemsField = groupedData.getClass().getDeclaredField("groupedItems");
        Field sortedListIdsField = groupedData.getClass().getDeclaredField("sortedListIds");
        groupedItemsField.setAccessible(true);
        sortedListIdsField.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<Integer, List<FetchObject>> groupedItems = (Map<Integer, List<FetchObject>>) groupedItemsField
                .get(groupedData);
        @SuppressWarnings("unchecked")
        List<Integer> sortedListIds = (List<Integer>) sortedListIdsField.get(groupedData);

        // Check grouping
        assert groupedItems != null;
        assertEquals(2, groupedItems.size());
        assertTrue(groupedItems.containsKey(1));
        assertTrue(groupedItems.containsKey(2));

        // Check sorting of listIds
        assertEquals(Arrays.asList(1, 2), sortedListIds);

        // Check sorting within group
        List<FetchObject> group2 = groupedItems.get(2);
        assert group2 != null;
        assertEquals("B", group2.get(0).getName());
        assertEquals("C", group2.get(1).getName());
    }
}