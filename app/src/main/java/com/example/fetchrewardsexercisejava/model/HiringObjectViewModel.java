package com.example.fetchrewardsexercisejava.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fetchrewardsexercisejava.util.HiringObjectRepository;

import java.util.List;

public class HiringObjectViewModel extends AndroidViewModel {
    private HiringObjectRepository hiringObjectRepository;
    private LiveData<List<HiringObject>> allHiringObjects;
    public HiringObjectViewModel(@NonNull Application application) {
        super(application);
        hiringObjectRepository = new HiringObjectRepository(application);
        allHiringObjects = hiringObjectRepository.getAllHiringObjects();
    }

    public LiveData<List<HiringObject>> getAllHiringObjects() {
        return allHiringObjects;
    }

    public void insert(HiringObject hiringObject) {
        hiringObjectRepository.insert(hiringObject);
    }
}
