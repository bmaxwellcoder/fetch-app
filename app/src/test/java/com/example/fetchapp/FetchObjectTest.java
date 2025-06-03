package com.example.fetchapp;

import com.example.fetchapp.model.FetchObject;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the FetchObject model class.
 * 
 * These tests verify:
 * 1. Object construction and initialization
 * 2. Getter and setter functionality
 * 3. Object equality and hash code generation
 * 4. String representation formatting
 * 5. Null value handling
 */
public class FetchObjectTest {
    /**
     * Tests the default constructor initialization.
     * Verifies that a newly created FetchObject has:
     * - id set to 0
     * - listId set to 0
     * - name set to null
     */
    @Test
    public void testDefaultConstructor() {
        FetchObject obj = new FetchObject();
        assertEquals(0, obj.getId());
        assertEquals(0, obj.getListId());
        assertNull(obj.getName());
    }

    /**
     * Tests the parameterized constructor.
     * Verifies that all fields are properly initialized with the provided values.
     */
    @Test
    public void testParameterizedConstructor() {
        FetchObject obj = new FetchObject(1, 2, "Test Name");
        assertEquals(1, obj.getId());
        assertEquals(2, obj.getListId());
        assertEquals("Test Name", obj.getName());
    }

    /**
     * Tests the setter methods.
     * Verifies that each field can be updated and retrieved correctly.
     */
    @Test
    public void testSetters() {
        FetchObject obj = new FetchObject();
        obj.setId(5);
        obj.setListId(10);
        obj.setName("New Name");

        assertEquals(5, obj.getId());
        assertEquals(10, obj.getListId());
        assertEquals("New Name", obj.getName());
    }

    /**
     * Tests object equality and hash code generation.
     * Verifies:
     * - Equal objects have equal hash codes
     * - Different objects have different hash codes
     * - Null name handling in equality comparison
     * - Same instance comparison
     */
    @Test
    public void testEqualsAndHashCode() {
        FetchObject obj1 = new FetchObject(1, 2, "Name");
        FetchObject obj2 = new FetchObject(1, 2, "Name");
        FetchObject obj3 = new FetchObject(2, 3, "Other");
        FetchObject obj4 = new FetchObject(1, 2, null);
        FetchObject obj5 = new FetchObject(1, 2, null);

        // Test equals
        assertEquals(obj1, obj2);
        assertNotEquals(obj1, obj3);
        assertNotEquals(obj1, obj4);
        assertEquals(obj4, obj5);
        assertNotEquals(obj1, null);
        assertEquals(obj1, obj1); // Same instance

        // Test hashCode
        assertEquals(obj1.hashCode(), obj2.hashCode());
        assertNotEquals(obj1.hashCode(), obj3.hashCode());
        assertEquals(obj4.hashCode(), obj5.hashCode());
    }

    /**
     * Tests the toString method formatting.
     * Verifies:
     * - Correct string representation for non-null name
     * - Correct string representation for null name
     * - Proper formatting of all fields
     */
    @Test
    public void testToString() {
        FetchObject obj = new FetchObject(1, 2, "Test");
        String str = obj.toString();

        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("listId=2"));
        assertTrue(str.contains("name='Test'"));

        // Test with null name
        FetchObject nullNameObj = new FetchObject(1, 2, null);
        String nullNameStr = nullNameObj.toString();
        assertTrue(nullNameStr.contains("name=null"));
    }

    /**
     * Tests null name handling.
     * Verifies:
     * - Initial null name state
     * - Setting a non-null name
     * - Setting back to null
     */
    @Test
    public void testNullNameHandling() {
        FetchObject obj = new FetchObject(1, 2, null);
        assertNull(obj.getName());
        obj.setName("New Name");
        assertEquals("New Name", obj.getName());
        obj.setName(null);
        assertNull(obj.getName());
    }
}