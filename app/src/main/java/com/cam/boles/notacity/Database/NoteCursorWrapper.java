package com.cam.boles.notacity.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.cam.boles.notacity.Note;

import java.util.Date;
import java.util.UUID;

/**
 * Created by boles on 5/6/2017.
 */

public class NoteCursorWrapper extends CursorWrapper {

    public NoteCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Note getNote()
    {
        String uuidString = getString(getColumnIndex(NoteDatabaseSchema.NoteTable.Cols.UUID));
        String title = getString(getColumnIndex(NoteDatabaseSchema.NoteTable.Cols.TITLE));
        long date = getLong(getColumnIndex(NoteDatabaseSchema.NoteTable.Cols.DATE));
        String body = getString(getColumnIndex(NoteDatabaseSchema.NoteTable.Cols.BODY));
        int priority = getInt(getColumnIndex(NoteDatabaseSchema.NoteTable.Cols.PRIORITY));

        Note note = new Note(UUID.fromString(uuidString));
        note.setTitle(title);
        note.setDate(new Date(date));
        note.setBody(body);
        note.setPriority(priority);

        return note;
    }
}
