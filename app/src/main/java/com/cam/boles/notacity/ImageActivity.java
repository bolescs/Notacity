package com.cam.boles.notacity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;

/**
 * Created by boles on 5/25/2017.
 */

public class ImageActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    public static final String IMAGE_PATH = "image_path";

    private static final String ARG_NOTE_ID = "note_id";
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private ProgressBar mProgress;
    private TouchImageView mImage;
    private File mPhotoFile;
    //private String mLaunchedFrom;
    private Note mNote;
    private boolean screenTapped;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_opaque));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.action_bar_opaque)));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.action_bar_opaque));

        mProgress = (ProgressBar) findViewById(R.id.image_progress);
        mImage = (TouchImageView) findViewById(R.id.full_image);
        mImage.setVisibility(View.INVISIBLE);
        screenTapped = false;

        mPhotoFile = new File(getIntent().getStringExtra(IMAGE_PATH));

        mImage.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

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

        final GestureDetectorCompat flingDetect = new GestureDetectorCompat(this, this);
        mImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flingDetect.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

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
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //fling top to bottom.
        if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY
                && !mImage.isZoomed()) {
            finish();
            overridePendingTransition(R.anim.activity_fade_in, R.anim.image_down);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_fade_in, R.anim.list_slide_out);
                return true;

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
