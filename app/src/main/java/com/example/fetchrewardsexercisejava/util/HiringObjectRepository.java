package com.example.fetchrewardsexercisejava.util;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.fetchrewardsexercisejava.data.HiringObjectDao;
import com.example.fetchrewardsexercisejava.data.HiringObjectDatabase;
import com.example.fetchrewardsexercisejava.model.HiringObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HiringObjectRepository {
    private HiringObjectDao hiringObjectDao;
    LiveData<List<HiringObject>> allHiringObjects;
    private final ExecutorService executorService;

    public HiringObjectRepository(Application application) {
        // Get data from a remote API and put in list
        HiringObjectDatabase hiringObjectDatabase = HiringObjectDatabase.getDatabase(application);
        hiringObjectDao = hiringObjectDatabase.hiringObjectDao();
        allHiringObjects = hiringObjectDao.getAllHiringObjects();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<HiringObject>> getAllHiringObjects() {
        return allHiringObjects;
    }

    public void insert(HiringObject hiringObject) {
        executorService.execute(() -> hiringObjectDao.insert(hiringObject));
    }
}