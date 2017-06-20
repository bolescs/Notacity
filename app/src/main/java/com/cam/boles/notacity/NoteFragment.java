package com.cam.boles.notacity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.util.UUID;

/**
 * Created by boles on 5/6/2017.
 *
 * This fragment displays all information about a specific note and offers many actions.
 */

public class NoteFragment extends Fragment implements View.OnLongClickListener, GestureDetector.OnGestureListener {

    public static final String ARG_NOTE_ID = "note_id";
    public static final String LIST_TAKE_PHOTO = "take_photo";
    public static final String FROM_NOTE_FRAG = "note_frag";

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final int REQUEST_PHOTO = 2;

    private Note mNote;
    private EditText mTitle;
    private EditText mBody;
    private ImageButton mPriority;
    private CardView mCard;
    private ImageView mDelete;
    private ImageView mPicture;
    private ProgressBar mProgress;
    private File mPhotoFile;

    //Bottom toolbar
    private LinearLayout mStyleBar;
    private ImageButton mBullets;
    private boolean bulletsActive;
    private ImageButton mNumberList;
    private boolean numbersActive;
    private int numLevel;

    //Actionbar
    private MenuItem mCamera;
    private boolean deleteVisible;
    private boolean isTakingPhoto;

    private Animation moveUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID noteId = (UUID) getArguments().getSerializable(ARG_NOTE_ID);
        mNote = NoteContentProvider.get(getActivity()).getNote(noteId);
        mPhotoFile = NoteContentProvider.get(getActivity()).getPhotoFile(mNote);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_note, container, false);

        //Provide up functionality for fragment.
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_finish );

        //Instantiate all widgets and other variables.
        deleteVisible = false;
        isTakingPhoto = false;
        mPriority = (ImageButton) rootView.findViewById(R.id.note_priority);
        if (mNote.getPriority() == Note.PRIORITY_FALSE) {
            mPriority.setImageResource(R.drawable.ic_action_blank_priority);
        }

        mPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation starIn = AnimationUtils.loadAnimation(getActivity(), R.anim.star_explode);
                if (mNote.getPriority() == Note.PRIORITY_FALSE) {
                    mPriority.setVisibility(View.INVISIBLE);
                    mPriority.setImageResource(R.drawable.ic_action_priority);
                    mPriority.setVisibility(View.VISIBLE);
                    mPriority.startAnimation(starIn);
                    mNote.setPriority(Note.PRIORITY_TRUE);
                } else {
                    mPriority.setImageResource(R.drawable.ic_action_blank_priority);
                    mNote.setPriority(Note.PRIORITY_FALSE);
                }
            }
        });
        mTitle = (EditText) rootView.findViewById(R.id.new_note_title);
        mTitle.setText(mNote.getTitle());
        getActivity().setTitle(mNote.getTitle());
        //Update title as typed.
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                getActivity().setTitle(s.toString());
            }
        });
        mTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDelete.clearAnimation();
                mDelete.setVisibility(View.GONE);
                deleteVisible = false;
                return false;
            }
        });

        mBody = (EditText) rootView.findViewById(R.id.new_note_body);
        mBody.setText(mNote.getBody());

        //Necessary for using bullets/numbers and updating body in database.
        mBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (bulletsActive) {
                    if (s.length() == 0) {
                        bulletsActive = false;
                        mBullets.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                    } else if (s.charAt(mBody.getSelectionStart() - 1) == '\n') {
                        insertBullet();
                    } else if (s.charAt(mBody.getSelectionStart() - 1) == '\t') {
                        bulletsActive = false;
                        mBullets.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                    }
                }

                if (numbersActive) {
                    if (s.length() == 0) {
                        numbersActive = false;
                        mNumberList.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                    } else if (s.charAt(mBody.getSelectionStart() - 1) == '\n') {
                        numLevel++;
                        insertNumber();
                    } else if (s.charAt(mBody.getSelectionStart() - 1) == '\t') {
                        numbersActive = false;
                        mNumberList.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                    }
                }

                mNote.setBody(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBody.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDelete.clearAnimation();
                mDelete.setVisibility(View.GONE);
                deleteVisible = false;
                return false;
            }
        });
        mBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mCard.setVisibility(View.GONE);
                    mStyleBar.setVisibility(View.VISIBLE);
                } else {
                    if (mPicture.getDrawable() != null) {
                        mCard.setVisibility(View.VISIBLE);
                    }
                    mStyleBar.setVisibility(View.GONE);
                }
            }
        });

        bulletsActive = false;
        mStyleBar = (LinearLayout) rootView.findViewById(R.id.style_layout);
        mBullets = (ImageButton) rootView.findViewById(R.id.bullet_button);
        mNumberList = (ImageButton) rootView.findViewById(R.id.number_button);
        //Must use proper buttons based on theme
        if (MainActivity.userTheme.equals(ThemeOption.MIDNIGHT_THEME)
                || MainActivity.userTheme.equals(ThemeOption.BLACK_THEME)) {
            mBullets.setImageResource(R.drawable.ic_action_white_bullets);
            mNumberList.setImageResource(R.drawable.ic_action_white_numbers);
        }
        mBullets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bulletsActive) {
                    mBullets.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.button_selected));
                    bulletsActive = true;
                    insertBullet();
                } else {
                    bulletsActive = false;
                    mBullets.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                }
            }
        });
        mNumberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!numbersActive) {
                    mNumberList.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.button_selected));
                    numbersActive = true;
                    numLevel = 1;
                    insertNumber();
                } else {
                    numbersActive = false;
                    mNumberList.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                    numLevel = 0;
                }
            }
        });

        mCard = (CardView) rootView.findViewById(R.id.card_view);
        mProgress = (ProgressBar) rootView.findViewById(R.id.image_loading);
        mDelete = (ImageView) rootView.findViewById(R.id.image_delete);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCard.setVisibility(View.GONE);
                mPicture.setImageDrawable(null);
                mDelete.setVisibility(View.GONE);
                mPhotoFile.delete();
            }
        });

        mPicture = (ImageView) rootView.findViewById(R.id.photo);
        updatePhotoView();
        final GestureDetectorCompat flingDetect = new GestureDetectorCompat(getActivity(), this);
        mPicture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flingDetect.onTouchEvent(event);
                return false;
            }
        });
        mPicture.setOnLongClickListener(this);
        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               fullScreenImage();
            }
        });

        moveUp = AnimationUtils.loadAnimation(getActivity(), R.anim.image_item_press);

        return rootView;
    }

    public void insertBullet() {
        mBody.getText().insert(mBody.getSelectionStart(), "\t\u2022  ");
    }

    public void insertNumber() {
        mBody.getText().insert(mBody.getSelectionStart(), "\t" + numLevel + ".  ");
    }

    public int getCurrentCursorLine()
    {
        int selectionStart = mBody.getSelectionStart();

        if (selectionStart != -1) {
            return mBody.getLayout().getLineForOffset(selectionStart);
        }

        return -1;
    }

    //Long clicks on image allow for deleting image.
    @Override
    public boolean onLongClick(View v) {
        Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.button_in);
        final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.delete_shake);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDelete.startAnimation(shake);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mDelete.setVisibility(View.VISIBLE);
        mDelete.startAnimation(fadeIn);
        deleteVisible = true;
        return true;
    }

    //Allows for taking pictures.
    public void takePhoto() {
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(getActivity().getPackageManager()) != null;
        mCamera.setVisible(canTakePhoto);

        if (canTakePhoto)
        {
            //Updated to work with API 24+.
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    getActivity().getPackageName() + ".fileprovider", mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            getActivity().setResult(Activity.RESULT_OK, captureImage);
        }
        startActivityForResult(captureImage, REQUEST_PHOTO);
    }

    //For delete note dialog.
    public void displayDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.delete_dialog_layout, null);
        dialog.setView(view);
        final AlertDialog alert = dialog.create();
        Button cancel = (Button) view.findViewById(R.id.cancel_action);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        Button delete = (Button) view.findViewById(R.id.delete_action);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoFile.delete();
                NoteContentProvider.get(getActivity()).deleteNote(mNote.getId());
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        alert.show();
    }

    //Set ImageView to the taken photo.
    private void updatePhotoView()
    {
        if (mPhotoFile == null || !mPhotoFile.exists())
        {
            mPicture.setImageDrawable(null);
        }
        else
        {
            mCard.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(mPhotoFile)
                    .signature(new StringSignature(String.valueOf(mPhotoFile.lastModified())))
                    .listener(new RequestListener<File, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, File model,
                                                   Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, File model,
                                                       Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
                            mProgress.setVisibility(View.GONE);

                            return false;
                        }
                    })
                    .into(mPicture);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }

        if (requestCode == REQUEST_PHOTO)
        {
            mPhotoFile = new File(PictureUtils.compressImage(mPhotoFile));
            updatePhotoView();
        }
    }

    private void fullScreenImage() {
        if (deleteVisible) {
            mDelete.clearAnimation();
            mDelete.setVisibility(View.GONE);
            deleteVisible = false;
        } else {
            Intent i = new Intent(getActivity(), ImageActivity.class);
            i.putExtra(ImageActivity.IMAGE_PATH, mPhotoFile.getPath());
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.image_up, R.anim.activity_fade_out);
        }
    }

    //provides the format for the text sent when sharing notes with others.
    private String getNoteReport()
    {
        String dateFormat = "EEE MMM dd";
        String dateString = DateFormat.format(dateFormat, mNote.getDate()).toString();

        return getString(R.string.note_report, mNote.getTitle(), dateString, mNote.getBody());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mCamera = menu.findItem(R.id.menu_item_camera);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.note_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return true;

            case R.id.menu_item_delete:
                displayDialog();
                return true;

            case R.id.menu_item_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getNoteReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.note_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
                return true;

            case R.id.menu_item_camera:
                isTakingPhoto = true;
                takePhoto();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        NoteContentProvider.get(getActivity()).updateNote(mNote);
        if (!isTakingPhoto) {
            if ((TextUtils.isEmpty(mTitle.getText())) && (TextUtils.isEmpty(mBody.getText()))
                    && (mCard.getVisibility() == View.GONE)) {
                NoteContentProvider.get(getActivity()).deleteNote(mNote.getId());
            }
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mCard.startAnimation(moveUp);
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            fullScreenImage();
        }
        return true;
    }
}
