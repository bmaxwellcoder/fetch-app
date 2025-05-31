package com.example.fetchrewardsexercisejava.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hiring_object_table")
public class HiringObject {
    @PrimaryKey()
    private int id;
    @ColumnInfo(name = "hiring_object_list_id")
    private int listId;
    @ColumnInfo(name = "hiring_object_name")
    private String name;

    public HiringObject() {

    }

    public HiringObject(int id, int listId, String name) {
        this.id = id;
        this.listId = listId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "HiringObject{" +
                "id='" + id + '\'' +
                ", listId='" + listId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
