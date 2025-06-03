package com.example.fetchrewardsexercisejava.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.android.volley.toolbox.Volley;
import com.example.fetchrewardsexercisejava.model.HiringObject;

public class HiringObjectApiService {
    private static final String TAG = "HiringObjectApiService";
    private static final String REQUEST_URL = "https://hiring.fetch.com/hiring.json";

    public static void fetchHiringObjects(Context context, HiringObjectApiCallback callback) {
        List<HiringObject> hiringObjectArrayList = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                REQUEST_URL,
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String name = jsonObject.getString("name");

                            HiringObject hiringObject = new HiringObject();
                            hiringObject.setId(jsonObject.getInt("id"));
                            hiringObject.setListId(jsonObject.getInt("listId"));
                            hiringObject.setName(name);

                            hiringObjectArrayList.add(hiringObject);
                        }

                        if (callback != null) {
                            callback.onSuccess(hiringObjectArrayList);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing: " + e.getMessage());
                        if (callback != null) {
                            callback.onError("Parsing error: " + e.getMessage());
                        }
                    }
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.getMessage());
                    if (callback != null) {
                        callback.onError("Network error: " + error.getMessage());
                    }
                }
        );

        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }
}
