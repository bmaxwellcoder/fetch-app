package com.example.fetchrewardsexercisejava.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DiffUtil;
import com.example.fetchrewardsexercisejava.R;
import com.example.fetchrewardsexercisejava.model.FetchObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adapter for displaying grouped FetchObjects in a RecyclerView.
 *
 * Responsibilities:
 * - Groups FetchObjects by listId
 * - Supports expandable/collapsible groups
 * - Efficiently updates the UI using DiffUtil
 * - Maintains group states (expanded/collapsed)
 *
 * Usage in MVVM:
 * - Used by the UI layer to display data from the ViewModel
 * - Handles UI-specific logic (grouping, sorting, expansion)
 * - Communicates user interactions back to the ViewModel
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<GroupViewHolder> {
    private final Map<Integer, List<FetchObject>> groupedItems;
    private final Map<Integer, Boolean> expandedStates;
    private final List<Integer> sortedListIds;

    public RecyclerViewAdapter() {
        this.groupedItems = new HashMap<>();
        this.expandedStates = new HashMap<>();
        this.sortedListIds = new ArrayList<>();
    }

    /**
     * Updates the adapter with new data, maintaining group states.
     * @param items List of FetchObjects to display
     */
    public void setData(List<FetchObject> items) {
        if (items == null) {
            items = new ArrayList<>();
        }
        
        GroupedData newData = processData(items);
        updateAdapterData(newData);
    }

    /**
     * Processes raw data into grouped and sorted format.
     * Groups items by listId and sorts items within each group by name.
     */
    private GroupedData processData(List<FetchObject> items) {
        // Group items by listId
        Map<Integer, List<FetchObject>> newGroupedItems = items.stream()
                .collect(Collectors.groupingBy(FetchObject::getListId));

        // Sort items within each group
        newGroupedItems.values().forEach(group -> 
            group.sort((a, b) -> {
                String nameA = a.getName();
                String nameB = b.getName();
                if (nameA == null && nameB == null) return 0;
                if (nameA == null) return 1;
                if (nameB == null) return -1;
                return nameA.compareTo(nameB);
            })
        );

        // Sort listIds
        List<Integer> newSortedListIds = new ArrayList<>(newGroupedItems.keySet());
        newSortedListIds.sort(Integer::compareTo);

        return new GroupedData(newGroupedItems, newSortedListIds);
    }

    /**
     * Updates adapter data using DiffUtil for efficient updates.
     * Maintains expanded states for existing groups.
     */
    private void updateAdapterData(GroupedData newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GroupDiffCallback(
                sortedListIds, newData.sortedListIds,
                groupedItems, newData.groupedItems
        ));

        // Update data
        this.groupedItems.clear();
        this.groupedItems.putAll(newData.groupedItems);
        this.sortedListIds.clear();
        this.sortedListIds.addAll(newData.sortedListIds);

        // Initialize expanded states for new groups
        newData.sortedListIds.forEach(listId -> 
            expandedStates.putIfAbsent(listId, false));

        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_card, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        int listId = sortedListIds.get(position);
        List<FetchObject> items = groupedItems.get(listId);
        boolean isExpanded = Boolean.TRUE.equals(expandedStates.getOrDefault(listId, false));

        holder.bind(listId, items, isExpanded, this::toggleGroup);
    }

    /**
     * Toggles the expanded state of a group and notifies the adapter.
     * @param listId The listId of the group to toggle
     */
    private void toggleGroup(int listId) {
        int position = sortedListIds.indexOf(listId);
        if (position >= 0) {
            boolean newState = !Boolean.TRUE.equals(expandedStates.getOrDefault(listId, false));
            expandedStates.put(listId, newState);
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return sortedListIds.size();
    }

    /**
     * Expands only the specified group, collapsing others if listId is -1.
     * @param listIdToExpand The listId of the group to expand, or -1 to collapse all
     */
    public void expandOnly(int listIdToExpand) {
        List<Integer> changedPositions = new ArrayList<>();

        for (int i = 0; i < sortedListIds.size(); i++) {
            int listId = sortedListIds.get(i);
            boolean currentState = Boolean.TRUE.equals(expandedStates.getOrDefault(listId, false));
            boolean newState = (listId == listIdToExpand);

            if (currentState != newState) {
                expandedStates.put(listId, newState);
                changedPositions.add(i);
            }
        }

        changedPositions.forEach(this::notifyItemChanged);
    }

    /**
     * Gets the position of a group in the adapter based on its listId.
     * @param listId The listId to find
     * @return The position of the group, or -1 if not found
     */
    public int getPositionForListId(int listId) {
        return sortedListIds.indexOf(listId);
    }

    /**
     * Helper class to hold grouped data.
     */
    private static class GroupedData {
        final Map<Integer, List<FetchObject>> groupedItems;
        final List<Integer> sortedListIds;

        GroupedData(Map<Integer, List<FetchObject>> groupedItems, List<Integer> sortedListIds) {
            this.groupedItems = groupedItems;
            this.sortedListIds = sortedListIds;
        }
    }
}
