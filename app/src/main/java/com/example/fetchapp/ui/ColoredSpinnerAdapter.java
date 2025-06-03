package com.example.fetchapp.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.example.fetchapp.R;
import com.example.fetchapp.model.FetchObject;

import java.util.List;

/**
 * Custom ArrayAdapter for displaying colored items in a Spinner.
 *
 * Responsibilities:
 * - Color-codes items based on list IDs
 * - Provides consistent styling for both normal and dropdown views
 * - Supports a default prompt item
 * - Offers a utility method for spinner setup
 *
 * Usage in MVVM:
 * - Used by the UI layer to display data from the ViewModel
 * - Handles UI-specific logic (coloring, styling)
 * - Communicates user selections back to the ViewModel
 */
public class ColoredSpinnerAdapter extends ArrayAdapter<String> {
    private final List<Integer> listIds;
    private static final int[] HEADER_COLORS = {
            R.color.header_blue,
            R.color.header_green,
            R.color.header_purple,
            R.color.header_orange
    };

    /**
     * Creates a new ColoredSpinnerAdapter.
     * 
     * @param context  The context to use for resource access
     * @param resource The resource ID for the layout of each item
     * @param objects  The list of strings to display
     * @param listIds  The list of IDs corresponding to each item (excluding prompt)
     */
    public ColoredSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects,
            List<Integer> listIds) {
        super(context, resource, objects);
        this.listIds = listIds;
    }

    /**
     * Gets the view for the selected item in the spinner.
     * Applies color styling to the selected item.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        colorize(position, view);
        return view;
    }

    /**
     * Gets the view for an item in the dropdown list.
     * Applies color styling to dropdown items.
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        colorize(position, view);
        return view;
    }

    /**
     * Applies color styling to a view based on its position and corresponding list
     * ID.
     * The prompt item (position 0) uses default styling.
     * 
     * @param position The position of the item
     * @param view     The view to colorize
     */
    private void colorize(int position, View view) {
        TextView textView = view.findViewById(R.id.spinnerLabel);
        if (position == 0 || listIds == null || position - 1 >= listIds.size()) {
            // Default color for the prompt
            textView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        } else {
            int listId = listIds.get(position - 1);
            int colorIndex = Math.abs(listId) % HEADER_COLORS.length;
            int color = ContextCompat.getColor(getContext(), HEADER_COLORS[colorIndex]);
            textView.setBackgroundColor(color);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }

    /**
     * Sets up a spinner with items from the provided FetchObject list.
     * Creates a colored adapter and configures the spinner with it.
     * 
     * @param context The context to use for resource access
     * @param spinner The spinner to configure
     * @param items   The list of FetchObjects to display
     */
    public static void setupSpinner(Context context, Spinner spinner,
            java.util.List<FetchObject> items) {
        java.util.List<String> spinnerItems = new java.util.ArrayList<>();
        java.util.List<Integer> listIds = new java.util.ArrayList<>();
        spinnerItems.add("- Select a list -");

        items.stream()
                .map(FetchObject::getListId)
                .distinct()
                .sorted()
                .forEach(id -> {
                    spinnerItems.add("List ID: " + id);
                    listIds.add(id);
                });

        ColoredSpinnerAdapter spinnerAdapter = new ColoredSpinnerAdapter(
                context,
                R.layout.item_spinner,
                spinnerItems,
                listIds);
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
    }
}