package com.cam.boles.notacity.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by boles on 5/6/2017.
 *
 *  Class creates database table.
 */

public class NoteBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "noteBase.db";

    public NoteBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + NoteDatabaseSchema.NoteTable.NAME + "(" + " _id integer primary key autoincrement, " +
                NoteDatabaseSchema.NoteTable.Cols.UUID + ", " + NoteDatabaseSchema.NoteTable.Cols.TITLE + ", " +
                NoteDatabaseSchema.NoteTable.Cols.DATE + ", " + NoteDatabaseSchema.NoteTable.Cols.BODY + ", " +
                NoteDatabaseSchema.NoteTable.Cols.PRIORITY + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
