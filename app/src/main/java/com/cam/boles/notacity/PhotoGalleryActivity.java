package com.cam.boles.notacity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by boles on 5/19/2017.
 *
 * Activity acting as parent of GalleryFragment.
 */

public class PhotoGalleryActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_THEME = "theme";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String theme = preferences.getString(PREFS_THEME, "Original");

        switch (theme) {
            case ThemeOption.ORIGINAL_THEME:
                setTheme(R.style.AppTheme);
                break;

            case ThemeOption.MIDNIGHT_THEME:
                setTheme(R.style.AppTheme_Dark);
                break;

            case ThemeOption.CLASSIC_THEME:
                setTheme(R.style.AppTheme_Classic);
                break;

            case ThemeOption.CLOUD_THEME:
                setTheme(R.style.AppTheme_Cloud);
                break;

            case ThemeOption.BLACK_THEME:
                setTheme(R.style.AppTheme_Black);
                break;

            case ThemeOption.RED_THEME:
                setTheme(R.style.AppTheme_White);

            default:
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        Fragment fragment = new MyGalleryFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.gallery_frame, fragment, "GALLERY_FRAGMENT")
                .commit();

    }
}
