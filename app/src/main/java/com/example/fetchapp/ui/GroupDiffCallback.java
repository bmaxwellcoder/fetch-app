package com.example.fetchapp.ui;

import androidx.recyclerview.widget.DiffUtil;
import com.example.fetchapp.model.FetchObject;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DiffUtil.Callback implementation for efficient updates of grouped data in the
 * RecyclerView.
 *
 * Responsibilities:
 * - Identifies which groups have changed
 * - Determines if groups have the same content
 * - Minimizes unnecessary view updates
 *
 * Usage in MVVM:
 * - Used by the UI layer to efficiently update the RecyclerView
 * - Supports separation of concerns by handling diffing logic
 * - Ensures smooth UI updates when data changes
 */
public class GroupDiffCallback extends DiffUtil.Callback {
    private final List<Integer> oldListIds;
    private final List<Integer> newListIds;
    private final Map<Integer, List<FetchObject>> oldGroups;
    private final Map<Integer, List<FetchObject>> newGroups;

    /**
     * Creates a new GroupDiffCallback for comparing grouped data.
     * 
     * @param oldListIds List of list IDs from the old data set, representing group
     *                   identifiers
     * @param newListIds List of list IDs from the new data set, representing group
     *                   identifiers
     * @param oldGroups  Map of list IDs to FetchObjects from the old data set,
     *                   containing group contents
     * @param newGroups  Map of list IDs to FetchObjects from the new data set,
     *                   containing group contents
     */
    public GroupDiffCallback(List<Integer> oldListIds, List<Integer> newListIds,
            Map<Integer, List<FetchObject>> oldGroups,
            Map<Integer, List<FetchObject>> newGroups) {
        this.oldListIds = oldListIds;
        this.newListIds = newListIds;
        this.oldGroups = oldGroups;
        this.newGroups = newGroups;
    }

    /**
     * Returns the number of groups in the old data set.
     * 
     * @return The size of the old list IDs collection
     */
    @Override
    public int getOldListSize() {
        return oldListIds.size();
    }

    /**
     * Returns the number of groups in the new data set.
     * 
     * @return The size of the new list IDs collection
     */
    @Override
    public int getNewListSize() {
        return newListIds.size();
    }

    /**
     * Determines if two items represent the same group.
     * Groups are considered the same if they have identical list IDs.
     * 
     * @param oldItemPosition Position in the old list
     * @param newItemPosition Position in the new list
     * @return true if the items have the same list ID, false otherwise
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return Objects.equals(oldListIds.get(oldItemPosition), newListIds.get(newItemPosition));
    }

    /**
     * Determines if two groups have the same content.
     * Groups have the same content if:
     * 1. Both groups are null
     * 2. Both groups have the same size
     * 3. All FetchObjects in the groups have matching IDs and names
     * 
     * @param oldItemPosition Position in the old list
     * @param newItemPosition Position in the new list
     * @return true if the groups have identical content, false otherwise
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int oldListId = oldListIds.get(oldItemPosition);
        int newListId = newListIds.get(newItemPosition);
        List<FetchObject> oldGroup = oldGroups.get(oldListId);
        List<FetchObject> newGroup = newGroups.get(newListId);

        // Handle null cases
        if (oldGroup == null && newGroup == null)
            return true;
        if (oldGroup == null || newGroup == null)
            return false;
        if (oldGroup.size() != newGroup.size())
            return false;

        // Compare group contents using streams
        return oldGroup.stream()
                .allMatch(oldObj -> newGroup.stream()
                        .anyMatch(newObj -> oldObj.getId() == newObj.getId() &&
                                Objects.equals(oldObj.getName(), newObj.getName())));
    }
}