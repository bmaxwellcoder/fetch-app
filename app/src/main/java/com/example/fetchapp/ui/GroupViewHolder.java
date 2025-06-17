package com.example.fetchapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fetchapp.R;
import com.example.fetchapp.model.FetchObject;
import java.util.List;

/**
 * ViewHolder for group items in the RecyclerView.
 *
 * Responsibilities:
 * - Handles the display and interaction of grouped FetchObjects
 * - Manages group expansion/collapse animations
 * - Applies consistent styling and colors to group headers
 * - Renders item rows with proper formatting
 *
 * Usage in MVVM:
 * - Used by the UI layer to display data from the ViewModel
 * - Communicates user interactions (expand/collapse) back to the ViewModel
 * - Maintains separation of concerns by handling UI-specific logic
 */
public class GroupViewHolder extends RecyclerView.ViewHolder {
    // Get animation duration from XML resources
    private final int animationDuration;

    // Header color resource IDs
    private static final int[] HEADER_COLOR_RES_IDS = {
            R.color.header_blue,
            R.color.header_green,
            R.color.header_purple,
            R.color.header_orange
    };

    private final TextView groupHeaderTextView;
    private final ImageView expandCollapseIcon;
    private final LinearLayout itemsContainer;
    private final View headerRow;

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);
        groupHeaderTextView = itemView.findViewById(R.id.groupHeaderTextView);
        expandCollapseIcon = itemView.findViewById(R.id.expandCollapseIcon);
        itemsContainer = itemView.findViewById(R.id.itemsContainer);
        headerRow = itemView.findViewById(R.id.headerRow);

        // Initialize animation duration from XML resources
        animationDuration = itemView.getContext().getResources()
                .getInteger(R.integer.expand_collapse_animation_duration);
    }

    /**
     * Binds the ViewHolder with group data and sets up interactions.
     * 
     * @param listId         The list ID for the group
     * @param items          The list of FetchObjects to display
     * @param isExpanded     Whether the group is expanded
     * @param toggleListener Listener for group expansion/collapse events
     */
    public void bind(int listId, List<FetchObject> items, boolean isExpanded,
            OnGroupToggleListener toggleListener) {
        setupHeader(listId, isExpanded);
        setupItems(items, isExpanded);
        setupClickListener(listId, toggleListener);
    }

    /**
     * Sets up the group header with styling and expansion state.
     * 
     * @param listId     The list ID for the group
     * @param isExpanded Whether the group is expanded
     */
    private void setupHeader(int listId, boolean isExpanded) {
        groupHeaderTextView.setText(groupHeaderTextView.getContext().getString(R.string.list_id, listId));
        groupHeaderTextView.setContentDescription("Group header for list ID " + listId);

        // Set header background color based on listId using color resource IDs
        int colorIndex = Math.abs(listId) % HEADER_COLOR_RES_IDS.length;
        headerRow.setBackgroundColor(ContextCompat.getColor(headerRow.getContext(), HEADER_COLOR_RES_IDS[colorIndex]));

        // Use appropriate drop icon based on expansion state
        int iconRes = isExpanded ? R.drawable.ic_drop_up : R.drawable.ic_drop_down;
        expandCollapseIcon.setImageResource(iconRes);

        headerRow.setSelected(isExpanded);
        expandCollapseIcon.setContentDescription(isExpanded ? "Collapse group" : "Expand group");
    }

    /**
     * Sets up the items container based on expansion state.
     * 
     * @param items      The list of FetchObjects to display
     * @param isExpanded Whether the group is expanded
     */
    private void setupItems(List<FetchObject> items, boolean isExpanded) {
        itemsContainer.removeAllViews();
        if (!isExpanded || items == null) {
            itemsContainer.setVisibility(View.GONE);
            return;
        }

        itemsContainer.setVisibility(View.VISIBLE);
        addHeaderRow();
        addItemRows(items);
    }

    /**
     * Adds a header row to the items container.
     */
    private void addHeaderRow() {
        // Use dedicated table header layout instead of programmatic styling
        View headerRow = LayoutInflater.from(itemsContainer.getContext())
                .inflate(R.layout.item_table_header, itemsContainer, false);

        itemsContainer.addView(headerRow);
        addDivider(itemsContainer);
    }

    /**
     * Adds item rows to the items container.
     * 
     * @param items The list of FetchObjects to display
     */
    private void addItemRows(List<FetchObject> items) {
        for (int i = 0; i < items.size(); i++) {
            View row = inflateRow();
            FetchObject item = items.get(i);

            TextView idText = row.findViewById(R.id.itemIdText);
            TextView nameText = row.findViewById(R.id.itemNameText);

            idText.setText(String.valueOf(item.getId()));
            nameText.setText(item.getName());

            itemsContainer.addView(row);
            if (i < items.size() - 1) {
                addDivider(itemsContainer);
            }
        }
    }

    /**
     * Inflates a row layout for the items container.
     * 
     * @return The inflated row view
     */
    private View inflateRow() {
        return LayoutInflater.from(itemsContainer.getContext())
                .inflate(R.layout.item_fetch_object, itemsContainer, false);
    }

    /**
     * Adds a divider to the items container.
     * 
     * @param container The container to add the divider to
     */
    private void addDivider(ViewGroup container) {
        View divider = new View(container.getContext());
        divider.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(ContextCompat.getColor(container.getContext(), R.color.divider));
        container.addView(divider);
    }

    /**
     * Sets up the click listener for the group header.
     * 
     * @param listId         The list ID for the group
     * @param toggleListener Listener for group expansion/collapse events
     */
    private void setupClickListener(int listId, OnGroupToggleListener toggleListener) {
        headerRow.setOnClickListener(v -> toggleListener.onGroupToggle(listId));
    }

    /**
     * Interface for handling group expansion/collapse.
     */
    public interface OnGroupToggleListener {
        void onGroupToggle(int listId);
    }
}