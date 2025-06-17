package com.example.fetchapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

/**
 * Utility class for network-related operations.
 *
 * Responsibilities:
 * - Provides methods to check network connectivity
 * - Centralizes network logic for reuse
 *
 * Usage in Repository/ViewModel:
 * - Used to ensure network availability before making network requests
 * - Supports robust error handling and user feedback
 */
public class NetworkUtils {
        /**
         * Checks if the device is connected to a network (modern API 23+ approach).
         *
         * @param context The application context
         * @return true if network is available, false otherwise
         */
        public static boolean isNetworkAvailable(Context context) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager == null) {
                        return false;
                }

                Network network = connectivityManager.getActiveNetwork();
                if (network == null) {
                        return false;
                }

                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null &&
                                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        }
}