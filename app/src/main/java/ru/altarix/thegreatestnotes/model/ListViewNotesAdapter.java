package ru.altarix.thegreatestnotes.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import ru.altarix.thegreatestnotes.R;

/**
 * Created by samsmariya on 20.10.15.
 */
public class ListViewNotesAdapter extends ArrayAdapter<Note> implements Observer {

    private ObjectManager<Note> dataSource;

    public ListViewNotesAdapter(Context context, int resource, int textViewResourceId, ObjectManager<Note> dataSource) {
        super(context, resource, textViewResourceId, dataSource.getAllObjects());
        this.dataSource = dataSource;
        dataSource.addObserver(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.listview_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text_data);
        textView.setText(note.getTitle());

        return convertView;
    }

    // Observer

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Map) {
            Map map = (Map)data;
            Note note = (Note) map.get(ObjectManager.OBJECT_KEY);
            ObjectManager.Action action = (ObjectManager.Action) map.get(ObjectManager.ACTION_KEY);
            executeAction(action, note);
        }
    }

    private void executeAction(ObjectManager.Action action, Note note) {
        switch (action) {
            case INSERT:
                insert(note, 0);
                break;
            case UPDATE:
                remove(note);
                insert(note, 0);
                break;
            case DELETE:
                remove(note);
                break;
        }
    }
}
