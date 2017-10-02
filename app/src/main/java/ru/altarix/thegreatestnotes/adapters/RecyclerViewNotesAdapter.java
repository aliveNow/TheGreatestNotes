package ru.altarix.thegreatestnotes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.altarix.thegreatestnotes.R;
import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.ObjectManager;
import ru.altarix.thegreatestnotes.model.ObjectManagerFactory;
import ru.altarix.thegreatestnotes.utils.Constants.Action;
import ru.altarix.thegreatestnotes.utils.OnNoteActionSelectedListener;

/**
 * Created by samsmariya on 02.10.17.
 */

public class RecyclerViewNotesAdapter extends RecyclerViewCursorAdapter<RecyclerViewNotesAdapter.ViewHolder>
{
    private static final String TAG = RecyclerViewNotesAdapter.class.getSimpleName();
    private final Context mContext;
    private ObjectManager<Note> notesManager = ObjectManagerFactory.getNotesManager();
    private OnNoteActionSelectedListener mCallback;

    public RecyclerViewNotesAdapter(Context context, OnNoteActionSelectedListener callback) {
        super(null);
        mContext = context;
        mCallback = callback;
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
        holder.note = note;
       // ImageUtils.showThumbnailImage(getContext(), holder.imageView, note.getImageUri());
       // holder.imageThumbnail.setVisibility(note.getImageUri() == null ? View.GONE : View.VISIBLE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements PopupMenu.OnMenuItemClickListener {
        CardView cardView;
        TextView textTitle;
        ImageView imageThumbnail;
        Note note;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);
            imageThumbnail = (ImageView) itemView.findViewById(R.id.image_thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onNoteActionSelected(note, Action.VIEW);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mCallback == null) {
                        return false;
                    }
                    PopupMenu popup = new PopupMenu(mContext, view);
                    popup.inflate(R.menu.menu_edit_note_in_list);
                    popup.setOnMenuItemClickListener(ViewHolder.this);
                    popup.show();
                    return true;
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mCallback == null) {
                return false;
            }
            switch (item.getItemId()) {
                case R.id.edit_note :
                    mCallback.onNoteActionSelected(note, Action.EDIT);
                    break;
                case R.id.remove_note :
                    mCallback.onNoteActionSelected(note, Action.DELETE);
                    break;
            }
            return true;
        }
    }
}
