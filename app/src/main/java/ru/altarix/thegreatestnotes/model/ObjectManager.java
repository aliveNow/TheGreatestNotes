package ru.altarix.thegreatestnotes.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;
import java.util.Observer;

/**
 * Created by samsmariya on 22.10.15.
 */
public interface ObjectManager<T> {

    String OBJECT_KEY = "object";
    String ACTION_KEY = "action";

    enum Action { INSERT, UPDATE, DELETE }

    void addObserver(Observer observer);
    void deleteObserver(Observer observer);

    List<T> getAllObjects();
    Cursor getAllObjectsCursor();
    T createObject();
    void saveObject(T obj);
    void removeObject(T obj);

    T cursorToObject(Cursor cursor);
    ContentValues contentValuesFromObject(T obj);
}
