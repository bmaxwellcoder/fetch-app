package com.example.fetchrewardsexercisejava.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.fetchrewardsexercisejava.model.HiringObject;

public class HiringObjectApiService {
    private static final String TAG = "HiringObjectApiService";
    private static final String REQUEST_URL = "https://hiring.fetch.com/hiring.json";
    private ArrayList<HiringObject> hiringObjectArrayList = new ArrayList<>();

    public List<HiringObject> getHiringObjects(final HiringObjectApiCallback callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                REQUEST_URL,
                null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            HiringObject hiringObject = new HiringObject();

                            String name = jsonObject.getString("name");
                            if (name == null || name.trim().isEmpty()) {
                                continue;
                            }

                            hiringObject.setId(jsonObject.getInt("id"));
                            hiringObject.setListId(jsonObject.getInt("listId"));
                            hiringObject.setName(jsonObject.getString("name"));

                            hiringObjectArrayList.add(hiringObject);

                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing object at index " + i + ": " + e.getMessage());
                        }
                    }

                    if (callback != null) {
                        callback.onSuccess(hiringObjectArrayList);
                    }
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.getMessage());
                    if (callback != null) {
                        callback.onError("Network error: " + error.getMessage());
                    }
                });

        HiringObjectRequestQueue.getInstance().addToRequestQueue(jsonArrayRequest);

        return hiringObjectArrayList;
    }
}
