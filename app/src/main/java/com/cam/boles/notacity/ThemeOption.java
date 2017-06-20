package com.cam.boles.notacity;

/**
 * Created by boles on 6/7/2017.
 *
 * Class for theme objects.
 */

public class ThemeOption {

    public static final String ORIGINAL_THEME = "Original";
    public static final String MIDNIGHT_THEME = "Midnight";
    public static final String CLASSIC_THEME = "Notepad";
    public static final String CLOUD_THEME = "Cloud";
    public static final String BLACK_THEME = "Black";
    public static final String RED_THEME = "Red";


    private String mName;
    private int mColor;

    public ThemeOption(String name, int color) {
        mName = name;
        mColor = color;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }
}
