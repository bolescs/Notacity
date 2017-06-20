package com.cam.boles.notacity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.util.UUID;

/**
 * Created by boles on 5/10/2017.
 *
 * This class provide the fragment to be displayed within the GalleryViewPager.
 */

public class FullScreenImageFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private ProgressBar mProgress;
    private TouchImageView mImage;
    private File mPhotoFile;
    private Note mNote;
    private boolean screenTapped;

    public static FullScreenImageFragment newInstance(UUID noteId)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, noteId);

        FullScreenImageFragment fragment = new FullScreenImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UUID noteId = (UUID) getArguments().getSerializable(ARG_NOTE_ID);
        mNote = NoteContentProvider.get(getActivity()).getNote(noteId);
        mPhotoFile = NoteContentProvider.get(getActivity()).getPhotoFile(mNote);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_full_image, container, false);

        mProgress = (ProgressBar) rootView.findViewById(R.id.image_frag_progress);
        mImage = (TouchImageView) rootView.findViewById(R.id.frag_full_image);
        mImage.setVisibility(View.INVISIBLE);
        mImage.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        screenTapped = false;
        Glide.with(this)
                .load(mPhotoFile)
                .asBitmap()
                .signature(new StringSignature(String.valueOf(mPhotoFile.lastModified())))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mProgress.setVisibility(View.GONE);
                        mImage.setImageBitmap(resource);
                        mImage.setVisibility(View.VISIBLE);
                    }
                });
        handleGestures();
        return rootView;
    }

    /**
     * Required for detecting tapUp.
     */
    private void handleGestures() {
        final GestureDetectorCompat detectorCompat = new GestureDetectorCompat(getActivity(), new TapGestureListener());
        mImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detectorCompat.onTouchEvent(event);
                return false;
            }
        });
    }

    /**
     * Inner class handling taps.
     */
    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (screenTapped) {
                mImage.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                screenTapped = false;
            } else {
                mImage.setSystemUiVisibility(
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.image_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_image_share:
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                final File photoFile = new File(mPhotoFile.getPath());
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                startActivity(Intent.createChooser(shareIntent, "Share image using"));
                return  true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
