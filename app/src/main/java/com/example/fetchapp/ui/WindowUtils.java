package com.example.fetchapp.ui;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Utility class for window-related operations.
 *
 * Responsibilities:
 * - Handles edge-to-edge display setup
 * - Manages window insets
 * - Provides system UI utilities
 *
 * Usage in MVVM:
 * - Used by the UI layer to ensure proper display and insets
 * - Helps maintain separation of concerns by centralizing window setup logic
 * - Supports modern Android UI practices
 */
public class WindowUtils {

    /**
     * Sets up window insets for edge-to-edge display.
     * Handles system UI insets properly for a modern Android look.
     * 
     * @param rootView The root view to apply insets to
     */
    public static void setupEdgeToEdge(View rootView) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}