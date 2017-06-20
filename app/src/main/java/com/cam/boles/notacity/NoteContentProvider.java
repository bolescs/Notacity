package com.cam.boles.notacity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.cam.boles.notacity.Database.NoteBaseHelper;
import com.cam.boles.notacity.Database.NoteDatabaseSchema;
import com.cam.boles.notacity.Database.NoteCursorWrapper;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by boles on 5/6/2017.
 *
 * Our content provider for accessing our database.
 */

public class NoteContentProvider {

    private static NoteContentProvider sNoteContentProvider;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static NoteContentProvider get(Context context)
    {
        if (sNoteContentProvider == null)
        {
            sNoteContentProvider = new NoteContentProvider(context);
        }
        return sNoteContentProvider;
    }

    private NoteContentProvider(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new NoteBaseHelper(mContext).getWritableDatabase();

    }

    public void addNote(Note n)
    {
        ContentValues values = getContentValues(n);
        mDatabase.insert(NoteDatabaseSchema.NoteTable.NAME, null, values);
    }

    public void deleteNote(UUID noteID)
    {
        String uuidString = noteID.toString();
        mDatabase.delete(NoteDatabaseSchema.NoteTable.NAME, NoteDatabaseSchema.NoteTable.Cols.UUID + " = ?",
                new String []{uuidString});
    }

    public List<Note> getNotes()
    {
        List<Note> notes = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(null, null);

        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }

        return notes;
    }

    public Note getNote(UUID id)
    {
        NoteCursorWrapper cursor = queryNotes(NoteDatabaseSchema.NoteTable.Cols.UUID + " = ?",
                new String[] {id.toString()});

        try
        {
            if (cursor.getCount() == 0)
            {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getNote();
        }
        finally
        {
            cursor.close();
        }
    }

    //retrieve the taken image from the device's files.
    public File getPhotoFile(Note note)
    {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null)
        {
            return null;
        }

        return new File(externalFilesDir, note.getPhotoFilename());
    }

    public void updateNote(Note note)
    {
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);

        mDatabase.update(NoteDatabaseSchema.NoteTable.NAME, values,
                NoteDatabaseSchema.NoteTable.Cols.UUID + " = ?", new String[] {uuidString});
    }

    //This method stores the aspects of each Note object into the database.
    //  each Note's title is stored in the TITLE column, body in BODY column, etc...
    private static ContentValues getContentValues(Note note)
    {
        ContentValues values = new ContentValues();
        values.put(NoteDatabaseSchema.NoteTable.Cols.UUID, note.getId().toString());
        values.put(NoteDatabaseSchema.NoteTable.Cols.TITLE, note.getTitle());
        values.put(NoteDatabaseSchema.NoteTable.Cols.DATE, note.getDate().getTime());
        values.put(NoteDatabaseSchema.NoteTable.Cols.BODY, note.getBody());
        values.put(NoteDatabaseSchema.NoteTable.Cols.PRIORITY, note.getPriority());
        return values;
    }

    //This method allows for accessing specific locations in the databse.
    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                NoteDatabaseSchema.NoteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new NoteCursorWrapper(cursor);
    }
}
