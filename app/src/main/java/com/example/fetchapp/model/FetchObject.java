package com.example.fetchapp.model;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Model class representing a Fetch Object from the API.
 *
 * This class is a simple POJO (Plain Old Java Object) used to map JSON data
 * from the Fetch API to Java objects. It contains three main properties:
 * - id: The unique identifier of the object
 * - listId: The identifier of the list this object belongs to
 * - name: The name of the object (can be null)
 *
 * Responsibilities:
 * - Encapsulate the data structure for items retrieved from the Fetch API
 * - Provide getter and setter methods for each property
 * - Implement equals(), hashCode(), and toString() for object comparison and
 * debugging
 *
 * Usage in MVVM:
 * - Used as the data model in the Model layer
 * - Passed between the Repository, ViewModel, and UI layers
 * - Supports easy mapping from Retrofit/Gson responses
 */
public class FetchObject {
    private int id;
    private int listId;
    private String name;

    /**
     * Default constructor required for JSON parsing.
     * This constructor is used by the JSON deserializer to create instances
     * of FetchObject from JSON data.
     */
    public FetchObject() {

    }

    /**
     * Constructor with all required fields.
     * Creates a new FetchObject with the specified properties.
     *
     * @param id     The unique identifier of the object
     * @param listId The identifier of the list this object belongs to
     * @param name   The name of the object (can be null)
     */
    public FetchObject(int id, int listId, String name) {
        this.id = id;
        this.listId = listId;
        this.name = name;
    }

    /**
     * Gets the unique identifier of the object.
     * This ID is used to uniquely identify each FetchObject instance.
     *
     * @return The object's unique ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the object.
     * This should only be called during object creation or deserialization.
     *
     * @param id The unique ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the list identifier this object belongs to.
     * The listId groups related FetchObjects together.
     *
     * @return The list ID this object belongs to
     */
    public int getListId() {
        return listId;
    }

    /**
     * Sets the list identifier this object belongs to.
     * This should only be called during object creation or deserialization.
     *
     * @param listId The list ID to set
     */
    public void setListId(int listId) {
        this.listId = listId;
    }

    /**
     * Gets the name of the object.
     * The name may be null or empty, which should be handled by the caller.
     *
     * @return The object's name, which may be null
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the object.
     * This should only be called during object creation or deserialization.
     *
     * @param name The name to set, which may be null
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of the FetchObject.
     * This method is primarily used for debugging purposes.
     *
     * @return A string containing the object's id, listId, and name
     */
    @NonNull
    @Override
    public String toString() {
        return "FetchObject{" +
                "id=" + id +
                ", listId=" + listId +
                ", name=" + (name == null ? "null" : (name.isEmpty() ? "\"\"" : name)) +
                '}';
    }

    /**
     * Compares this FetchObject with another object for equality.
     * Two FetchObjects are considered equal if they have the same id, listId, and
     * name.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FetchObject that = (FetchObject) o;
        return id == that.id &&
                listId == that.listId &&
                (Objects.equals(name, that.name));
    }

    /**
     * Generates a hash code for this FetchObject.
     * The hash code is based on the object's id, listId, and name.
     *
     * @return A hash code value for this object
     */
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + listId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
