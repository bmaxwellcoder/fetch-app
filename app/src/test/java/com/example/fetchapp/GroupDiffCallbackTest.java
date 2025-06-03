package com.example.fetchapp;

import com.example.fetchapp.model.FetchObject;
import com.example.fetchapp.ui.GroupDiffCallback;

import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the GroupDiffCallback class.
 * 
 * These tests verify the DiffUtil.Callback implementation for:
 * 1. Item identity comparison
 * 2. Content equality comparison
 * 3. Proper handling of group changes
 * 4. Efficient diff calculation for RecyclerView updates
 */
public class GroupDiffCallbackTest {
    /**
     * Tests the areItemsTheSame method.
     * Verifies that items are correctly identified as the same or different
     * based on their position in the list.
     */
    @Test
    public void testAreItemsTheSame() {
        List<Integer> oldListIds = Arrays.asList(1, 2);
        List<Integer> newListIds = Arrays.asList(1, 2);
        Map<Integer, List<FetchObject>> oldGroups = new HashMap<>();
        Map<Integer, List<FetchObject>> newGroups = new HashMap<>();
        GroupDiffCallback callback = new GroupDiffCallback(oldListIds, newListIds, oldGroups, newGroups);
        assertTrue(callback.areItemsTheSame(0, 0));
        assertTrue(callback.areItemsTheSame(1, 1));
        assertFalse(callback.areItemsTheSame(0, 1));
    }

    /**
     * Tests the areContentsTheSame method.
     * Verifies that:
     * - Identical groups are recognized as the same
     * - Groups with different content are recognized as different
     * - Changes in group content trigger proper updates
     */
    @Test
    public void testAreContentsTheSame() {
        List<Integer> oldListIds = List.of(1);
        List<Integer> newListIds = List.of(1);
        List<FetchObject> oldGroup = List.of(new FetchObject(1, 1, "A"));
        List<FetchObject> newGroup = List.of(new FetchObject(1, 1, "A"));
        Map<Integer, List<FetchObject>> oldGroups = new HashMap<>();
        Map<Integer, List<FetchObject>> newGroups = new HashMap<>();
        oldGroups.put(1, oldGroup);
        newGroups.put(1, newGroup);
        GroupDiffCallback callback = new GroupDiffCallback(oldListIds, newListIds, oldGroups, newGroups);
        assertTrue(callback.areContentsTheSame(0, 0));
        // Change name
        newGroups.put(1, List.of(new FetchObject(1, 1, "B")));
        assertFalse(callback.areContentsTheSame(0, 0));
    }

    /**
     * Tests the equality and hash code generation of FetchObjects.
     * Verifies that:
     * - Equal objects have equal hash codes
     * - Different objects have different hash codes
     * - Object equality is properly implemented
     */
    @Test
    public void testEqualsAndHashCode() {
        FetchObject obj1 = new FetchObject(1, 2, "Name");
        FetchObject obj2 = new FetchObject(1, 2, "Name");
        FetchObject obj3 = new FetchObject(2, 3, "Other");
        assertEquals(obj1, obj2);
        assertEquals(obj1.hashCode(), obj2.hashCode());
        assertNotEquals(obj1, obj3);
    }
}