package com.cam.boles.notacity;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

/**
 * Created by boles on 5/6/2017.
 *
 * Our Note object class.
 */

public class Note implements Comparable<Note> {

    public static final int PRIORITY_TRUE = 1;
    public static final int PRIORITY_FALSE = 0;

    private UUID mId;
    private String mTitle;
    private String mBody;
    private Date mDate;
    private int mPriority;

    public Note()
    {
        this(UUID.randomUUID());
    }

    public Note(UUID id)
    {
        mId = id;
        mDate = new Date();
        mPriority = PRIORITY_FALSE;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    public String getPhotoFilename()
    {
        return "IMG_" + getId().toString() + ".jpg";
    }

    @Override
    public int compareTo(@NonNull Note note) {
        long time = ((Note) note).getDate().getTime();
        if (this.mDate.getTime() > time) {
            return -1;
        } else if (this.mDate.getTime() < time) {
            return 1;
        }
        return 0;
    }
}
