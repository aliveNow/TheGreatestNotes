package ru.altarix.thegreatestnotes.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import ru.altarix.thegreatestnotes.R;
import ru.altarix.thegreatestnotes.utils.ImageUtils;

/**
 * Created by samsmariya on 20.10.15.
 */
public class ListViewNotesAdapter extends ArrayAdapter<Note> implements Observer {

    private final LayoutInflater inflater;
    private ObjectManager<Note> dataSource;


    public ListViewNotesAdapter(Context context, int resource, int textViewResourceId, ObjectManager<Note> dataSource) {
        super(context, resource, textViewResourceId, dataSource.getAllObjects());
        this.dataSource = dataSource;
        dataSource.addObserver(this);
        inflater = LayoutInflater.from(this.getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
            holder = new Holder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.textView = (TextView) convertView.findViewById(R.id.text_data);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        holder.textView.setText(note.getTitle());
        ImageUtils.showThumbnailImage(getContext(), holder.imageView, note.getImageUri());
        holder.imageView.setVisibility(note.getImageUri() == null ? View.GONE : View.VISIBLE);

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

    class Holder {
        TextView textView;
        ImageView imageView;
    }

}
