package com.cam.boles.notacity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by boles on 5/5/2017.
 *
 * This class allows users to change theme and view licenses.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_THEME = "theme";

    private static final String PREF_CURRENT_THEME_NAME = "current_name";
    private static final String PREF_CURRENT_THEME_COLOR = "current_color";

    private LinearLayout mChooseTheme;
    private ImageView mThemeColor;
    private List<ThemeOption> themeObjects;
    private AlertDialog alert;

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
        setContentView(R.layout.activity_settings);

        //Create each available theme (must be better way of doing this?)
        themeObjects = new ArrayList<>();
        themeObjects.add(new ThemeOption(ThemeOption.ORIGINAL_THEME, R.color.colorPrimary));
        themeObjects.add(new ThemeOption(ThemeOption.MIDNIGHT_THEME, R.color.colorPrimaryInverse));
        themeObjects.add(new ThemeOption(ThemeOption.CLASSIC_THEME, R.color.colorPrimaryLightClassic));
        themeObjects.add(new ThemeOption(ThemeOption.CLOUD_THEME, R.color.colorPrimaryCloud));
        themeObjects.add(new ThemeOption(ThemeOption.BLACK_THEME, R.color.black));
        themeObjects.add(new ThemeOption(ThemeOption.RED_THEME, R.color.redPrimary));

        mThemeColor = (ImageView) findViewById(R.id.theme_choose_color);
        mThemeColor.setImageResource(preferences.getInt(PREF_CURRENT_THEME_COLOR, R.color.colorPrimary));

        mChooseTheme = (LinearLayout) findViewById(R.id.theme_choose_button);
        mChooseTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayThemeDialog();
            }
        });

        TextView licences = (TextView) findViewById(R.id.licence_text_view);
        licences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLicenseDialog();
            }
        });
    }

    //For choosing theme.
    public void displayThemeDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.theme_dialog_layout, null);
        dialog.setView(view);
        alert = dialog.create();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.theme_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new ThemeAdapter(themeObjects));
        alert.show();
    }

    //For displaying licenses.
    public void displayLicenseDialog() {
        WebView licenceView = (WebView) getLayoutInflater().inflate(R.layout.activity_licenses, null);
        licenceView.loadUrl("file:///android_asset/licenses.html");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setView(licenceView)
                .setTitle(R.string.open_source_licences)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    /**
     * NoteAdapter inner class.
     */
    private class ThemeAdapter extends RecyclerView.Adapter<ThemeHolder> {

        private List<ThemeOption> mThemes;

        public ThemeAdapter(List<ThemeOption> themes) {
            mThemes = themes;
        }

        @Override
        public ThemeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.theme_list_item, parent, false);
            return new ThemeHolder(inflatedView, mThemes);
        }

        @Override
        public void onBindViewHolder(ThemeHolder holder, int position) {
            ThemeOption themeOption = mThemes.get(position);
            holder.bindTheme(themeOption);
        }

        @Override
        public int getItemCount() {
            return mThemes.size();
        }
    }

    /**
     * NoteHolder inner class.
     */
    private class ThemeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private List<ThemeOption> mThemes;
        private ThemeOption mThemeOption;
        private TextView themeTitle;
        private ImageView themeColor;

        public ThemeHolder(View v, List<ThemeOption> themes) {
            super(v);
            mThemes = themes;
            themeTitle = (TextView) v.findViewById(R.id.theme_name);
            themeColor = (ImageView) v.findViewById(R.id.theme_color);

            v.setOnClickListener(this);
        }

        public void bindTheme(ThemeOption themeOption) {
            mThemeOption = themeOption;
            themeTitle.setText(themeOption.getmName());
            themeColor.setImageResource(themeOption.getmColor());
        }

        @Override
        public void onClick(View v) {
            mThemeColor.setImageResource(mThemeOption.getmColor());
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString(PREF_CURRENT_THEME_NAME, mThemeOption.getmName());
            editor.putInt(PREF_CURRENT_THEME_COLOR, mThemeOption.getmColor());
            editor.putString(PREFS_THEME, mThemeOption.getmName());
            editor.apply();
            alert.dismiss();
            recreate();
        }
    }
}
