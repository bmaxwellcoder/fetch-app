package com.example.fetchrewardsexercisejava.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.fetchrewardsexercisejava.model.HiringObject;
import com.example.fetchrewardsexercisejava.network.HiringObjectApiCallback;
import com.example.fetchrewardsexercisejava.network.HiringObjectApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Database(entities = {HiringObject.class}, version = 1)
public abstract class HiringObjectDatabase extends RoomDatabase {
    private static volatile HiringObjectDatabase  DATABASE_INSTANCE;
    public abstract HiringObjectDao hiringObjectDao();
    public static HiringObjectDatabase getDatabase(final Context context) {
        if (DATABASE_INSTANCE == null) {
            synchronized (HiringObjectDatabase.class) {
                if (DATABASE_INSTANCE == null) {
                    // create hiring object database
                    DATABASE_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HiringObjectDatabase.class, "hiring_object_database")
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }

        return DATABASE_INSTANCE;
    }

    private static final RoomDatabase.Callback roomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    populateDatabase();
                }
            };

    private static void populateDatabase() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Get the DAO
            HiringObjectDao hiringObjectDao = DATABASE_INSTANCE.hiringObjectDao();

            // Delete all existing data
            hiringObjectDao.deleteAll();

            // Create a CountDownLatch to wait for the API call to complete
            final CountDownLatch latch = new CountDownLatch(1);
            final List<HiringObject> apiResult = new ArrayList<>();

            // Make API call
            new HiringObjectApiService().getHiringObjects(new HiringObjectApiCallback() {
                @Override
                public void onSuccess(ArrayList<HiringObject> hiringObjectArrayList) {
                    // Add all received objects to our result list
                    apiResult.addAll(hiringObjectArrayList);
                    latch.countDown(); // Signal that data is received
                }

                @Override
                public void onError(String error) {
                    Log.e("Database", "Error fetching data: " + error);
                    latch.countDown(); // Signal even on error
                }
            });

            try {
                // Wait for the API call to complete (with timeout)
                boolean apiCompleted = latch.await(10, TimeUnit.SECONDS);

                if (apiCompleted) {
                    // Insert all fetched objects into database
                    for (HiringObject hiringObject : apiResult) {
                        if (hiringObject.getName() != null && !hiringObject.getName().isEmpty()) {
                            hiringObjectDao.insert(hiringObject);
                        }
                    }
                } else {
                    Log.e("Database", "API call timed out after 10 seconds");
                }
            } catch (InterruptedException e) {
                Log.e("Database", "API call interrupted", e);
            }
        });
    }
}
