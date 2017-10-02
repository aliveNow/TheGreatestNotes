package ru.altarix.thegreatestnotes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.altarix.thegreatestnotes.R;
import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.ObjectManager;
import ru.altarix.thegreatestnotes.model.ObjectManagerFactory;

/**
 * Created by samsmariya on 02.10.17.
 */

public class RecyclerViewNotesAdapter extends RecyclerViewCursorAdapter<RecyclerViewNotesAdapter.ViewHolder>
{
    private static final String TAG = RecyclerViewNotesAdapter.class.getSimpleName();
    private final Context mContext;
    private ObjectManager<Note> notesManager = ObjectManagerFactory.getNotesManager();


    public RecyclerViewNotesAdapter(Context context) {
        super(null);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        Note note = notesManager.cursorToObject(cursor);
        holder.textTitle.setText(note.getTitle());
       // ImageUtils.showThumbnailImage(getContext(), holder.imageView, note.getImageUri());
       // holder.imageThumbnail.setVisibility(note.getImageUri() == null ? View.GONE : View.VISIBLE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTitle;
        ImageView imageThumbnail;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);
            imageThumbnail = (ImageView) itemView.findViewById(R.id.image_thumbnail);
        }
    }
}
