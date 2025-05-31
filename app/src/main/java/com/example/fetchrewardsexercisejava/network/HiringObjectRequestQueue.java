package com.example.fetchrewardsexercisejava.network;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HiringObjectRequestQueue extends Application {
    private static HiringObjectRequestQueue instance;
    private RequestQueue requestQueue;
    public static final String TAG = HiringObjectRequestQueue.class.getSimpleName();

    public static synchronized HiringObjectRequestQueue getInstance() {
        if (instance == null) {
            instance = new HiringObjectRequestQueue();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }
}