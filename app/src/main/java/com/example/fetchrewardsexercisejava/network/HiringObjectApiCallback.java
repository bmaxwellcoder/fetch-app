package com.example.fetchrewardsexercisejava.network;

import com.example.fetchrewardsexercisejava.model.HiringObject;

import java.util.ArrayList;

public interface HiringObjectApiCallback {
    void onSuccess(ArrayList<HiringObject> hiringObjects);
    void onError(String error);
}
