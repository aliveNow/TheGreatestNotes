package ru.altarix.thegreatestnotes.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by samsmariya on 04.10.15.
 */
public class Note implements Parcelable {

    private long id;
    private String title;
    private String text;
    private Uri imageUri;

    public Note() {
    }

    public Note(long id) {
        this.id = id;
    }

    public Note(long id, String title, String text) {
        this.id = id;
        this.text = text;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Uri getImageUri() { return imageUri; }

    public void setImageUri(Uri imageUri) { this.imageUri = imageUri; }

    public void updateValues(Note note)
    {
        id = note.id;
        title = note.title;
        text = note.text;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Note) {
            Note note = (Note) o;
            return note.id > 0 && id > 0 && note.id == id;
        }
        return false;
    }

    // Parcelable

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeParcelable(imageUri, flags);
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    // конструктор, считывающий данные из Parcel
    private Note(Parcel parcel) {
        id = parcel.readLong();
        title = parcel.readString();
        text = parcel.readString();
        imageUri = parcel.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

}