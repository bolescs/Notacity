package com.cam.boles.notacity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_THEME = "theme";

    public static String userTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String theme = preferences.getString(PREFS_THEME, "Original");
        userTheme = theme;

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
        setContentView(R.layout.activity_main);

        Fragment fragment = new MyNotesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "NOTES_FRAGMENT")
                .commit();
    }
}
