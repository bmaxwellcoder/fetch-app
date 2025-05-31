package com.example.fetchrewardsexercisejava.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.fetchrewardsexercisejava.model.HiringObject;

import java.util.List;

@Dao
public interface HiringObjectDao {
    @Insert
    void insert(HiringObject hiringObject);

    @Query("SELECT * FROM hiring_object_table")
    LiveData<List<HiringObject>> getAllHiringObjects();

    @Query("SELECT * FROM hiring_object_table GROUP BY hiring_object_list_id ORDER BY hiring_object_list_id")
    LiveData<List<HiringObject>> getHiringObjectsGroupedByListId();

    @Query("UPDATE hiring_object_table SET hiring_object_name = :hiringObjectName WHERE id = :id")
    int updateHiringObjectName(int id, String hiringObjectName); // TODO: should i include?

    @Query("DELETE FROM  hiring_object_table WHERE id = :id") //TODO: should I include?
    int deleteAHiringObject(int id);

    @Query("DELETE FROM hiring_object_table") // TODO: should I include?
    void deleteAll();
}
