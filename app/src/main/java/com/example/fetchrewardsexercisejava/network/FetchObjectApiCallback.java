package com.example.fetchrewardsexercisejava.network;

import com.example.fetchrewardsexercisejava.model.HiringObject;

import java.util.ArrayList;
import java.util.List;

public interface HiringObjectApiCallback {
    void onSuccess(List<HiringObject> items);
    void onError(String message);
}
