package com.cam.boles.notacity;

/**
 * Created by boles on 5/23/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Cameron Boles on 11/17/16.
 *
 * This class allows for swiping between images when in the gallery display.
 *
 */

public class GalleryViewPager extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "noteId";
    public static final String VIEW_PAGER_IMAGE_CHILD = "imageview_child";

    private ViewPager mViewPager;
    private List<Note> mNotes;
    private boolean screenTapped;

    public static Intent newIntent(Context packageContext, UUID noteId)
    {
        Intent intent = new Intent(packageContext, GalleryViewPager.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_opaque));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.status_bar_opaque)));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.status_bar_opaque));
        screenTapped = false;
        UUID noteId = (UUID) getIntent().getSerializableExtra(EXTRA_NOTE_ID);
        mViewPager = (ViewPager) findViewById(R.id.image_view_pager);
        mViewPager.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mViewPager.setPageMargin(50);
        mViewPager.setPageMarginDrawable(R.color.black);
        mNotes = NoteContentProvider.get(this).getNotes();
        final List<Note> notesWithPic = new ArrayList<>();

        for (Note note : mNotes) {
            File toAdd = NoteContentProvider.get(this).getPhotoFile(note);
            if (toAdd.exists()) {
                notesWithPic.add(note);
            }
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Note note = notesWithPic.get(position);
                return FullScreenImageFragment.newInstance(note.getId());
            }

            @Override
            public int getCount() {
                return notesWithPic.size();
            }


        });

        for (int i = 0; i < mNotes.size(); i++)
        {
            if (notesWithPic.get(i).getId().equals(noteId))
            {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
        //handleGestures();

        /*
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                TouchImageView image = (TouchImageView) mViewPager.findViewWithTag(VIEW_PAGER_IMAGE_CHILD);
                image.resetZoom();
            }

            @Override
            public void onPageSelected(int position) {
                TouchImageView image = (TouchImageView) mViewPager.findViewWithTag(VIEW_PAGER_IMAGE_CHILD);
                image.resetZoom();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        */
    }

    /*
    private void handleGestures() {
        final GestureDetectorCompat detectorCompat = new GestureDetectorCompat(this, new TapGestureListener());
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detectorCompat.onTouchEvent(event);
                return false;
            }
        });
    }

    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (screenTapped) {
                mViewPager.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                screenTapped = false;
            } else {
                mViewPager.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
                screenTapped = true;
            }
            super.onSingleTapUp(e);
            return true;
        }

    }
    */




}