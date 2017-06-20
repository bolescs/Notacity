package com.cam.boles.notacity.Database;

/**
 * Created by boles on 5/6/2017.
 *
 * Class defines column titles for table.
 */

public class NoteDatabaseSchema {

    public static final class NoteTable
    {
        public static final String NAME = "notes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String BODY = "body";
            public static final String PRIORITY = "priority";
        }
    }
}
