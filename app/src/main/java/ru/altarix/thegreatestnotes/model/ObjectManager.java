package ru.altarix.thegreatestnotes.model;

import java.util.List;
import java.util.Observer;

/**
 * Created by samsmariya on 22.10.15.
 */
public interface ObjectManager<T> {

    public static final String OBJECT_KEY = "object";
    public static final String ACTION_KEY = "action";

    public enum Action { INSERT, UPDATE, DELETE }

    public void addObserver(Observer observer);
    public void deleteObserver(Observer observer);

    public List<T> getAllObjects();
    public T createObject();
    public void saveObject(T obj);
    public void removeObject(T obj);
}
