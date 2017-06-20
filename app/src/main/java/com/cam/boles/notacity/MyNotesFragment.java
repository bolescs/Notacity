package com.cam.boles.notacity;


import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by boles on 5/6/2017.
 *
 * This class displays user's notes in a recyclerview.
 */

public class MyNotesFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static final String NO_PHOTO = "no_photo";

    private AppBarLayout mCollapsingLayout;
    private SearchView mSearch;
    private TextView mEmptyNotes;
    private RecyclerView mNotesRecyclerView;
    private NoteAdapter mAdapter;
    private ImageButton mFilter;
    private boolean filterActive;
    private FloatingActionButton mNewNote;
    private FloatingActionButton mExit;
    private FragmentManager mFragmentManager;

    //fields accessed in NoteAdapter and NoteViewHolder.
    private List<Note> notes;
    private List<Note> notesCopy = new ArrayList<>();
    private List<SwipeRevealLayout> mItems = new ArrayList<>();
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private boolean deleteMode;
    private Animation note_implode_start;
    private Animation exit_implode_start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //custom actionBar font.
        TextView tv = new TextView(getActivity());
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        tv.setLayoutParams(lp);
        tv.setText(R.string.app_name);
        tv.setTextSize(24);
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        AssetManager am = getActivity().getAssets();
        Typeface tf = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "Pacifico-Regular.ttf"));
        tv.setTypeface(tf);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(tv);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        mFragmentManager = getActivity().getSupportFragmentManager();
        mCollapsingLayout = (AppBarLayout) rootView.findViewById(R.id.collapsing_layout);
        mFilter = (ImageButton) rootView.findViewById(R.id.filter_button);
        mSearch = (SearchView) rootView.findViewById(R.id.search_view);
        View v = mSearch.findViewById(android.support.v7.appcompat.R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);
        mSearch.setOnQueryTextListener(this);
        mEmptyNotes = (TextView) rootView.findViewById(R.id.no_notes_text);
        mNotesRecyclerView = (RecyclerView) rootView.findViewById(R.id.notes_recycler_view);
        mNotesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNewNote = (FloatingActionButton) rootView.findViewById(R.id.new_note_button);
        mExit = (FloatingActionButton) rootView.findViewById(R.id.button_exit);

        //Must update theme.
        switch (MainActivity.userTheme) {
            case ThemeOption.ORIGINAL_THEME:
                mCollapsingLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                //mSearch.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                mNewNote.setBackgroundTintList(ColorStateList.
                        valueOf(ContextCompat.getColor(getActivity(), R.color.colorPrimary)));
                mNewNote.setRippleColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryLight));
                break;

            case ThemeOption.MIDNIGHT_THEME:
                mCollapsingLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.windowBackgroundDark));
                mFilter.setImageResource(R.drawable.ic_action_filter_white);
                mNewNote.setBackgroundTintList(ColorStateList.
                        valueOf(ContextCompat.getColor(getActivity(), R.color.colorPrimaryInverse)));
                mNewNote.setRippleColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryLightInverse));
                break;

            case ThemeOption.CLASSIC_THEME:
                mCollapsingLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryLightClassic));
                mNewNote.setBackgroundTintList(ColorStateList.
                        valueOf(ContextCompat.getColor(getActivity(), R.color.colorPrimaryClassic)));
                mNewNote.setRippleColor(ContextCompat.getColor(getActivity(), R.color.divider_color));
                break;

            case ThemeOption.CLOUD_THEME:
                mCollapsingLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                mNewNote.setBackgroundTintList(ColorStateList.
                        valueOf(ContextCompat.getColor(getActivity(), R.color.colorPrimaryCloud)));
                mNewNote.setRippleColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryLightCloud));
                break;

            case ThemeOption.BLACK_THEME:
                mCollapsingLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blackWindow));
                mFilter.setImageResource(R.drawable.ic_action_filter_white);
                mNewNote.setBackgroundTintList(ColorStateList.
                        valueOf(ContextCompat.getColor(getActivity(), R.color.blackPrimary)));
                mNewNote.setRippleColor(ContextCompat.getColor(getActivity(), R.color.button_selected));
                break;

            case ThemeOption.RED_THEME:
                mCollapsingLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                mNewNote.setBackgroundTintList(ColorStateList.
                        valueOf(ContextCompat.getColor(getActivity(), R.color.redPrimary)));
                mNewNote.setRippleColor(ContextCompat.getColor(getActivity(), R.color.redLight));
                break;

            default:
                break;
        }

        //Filter button listener
        filterActive = false;
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filterActive) {
                    mAdapter.showPriority();
                    mFilter.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.star_color));
                    filterActive = true;
                } else {
                    updateRecycler();
                    mFilter.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                    filterActive = false;
                }
            }
        });

        mNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewNote();
            }
        });

        mNotesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 && mNewNote.isShown()) {
                    mNewNote.hide();
                } else if (dy < 0) {
                    mNewNote.show();
                }
            }
        });

        //instantiate necessary animations for ViewHolder.
        note_implode_start = AnimationUtils.loadAnimation(getActivity(), R.anim.button_implode);
        exit_implode_start = AnimationUtils.loadAnimation(getActivity(), R.anim.button_implode);
        final Animation explode_temp = AnimationUtils.loadAnimation(getActivity(), R.anim.button_explode);
        note_implode_start.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mNewNote.setVisibility(View.GONE);
                mExit.setVisibility(View.VISIBLE);
                mExit.startAnimation(explode_temp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        exit_implode_start.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mExit.setVisibility(View.GONE);
                mNewNote.setVisibility(View.VISIBLE);
                mNewNote.startAnimation(explode_temp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //create adapter and populate recyclerView.
        updateRecycler();

        return rootView;
    }

    public void updateRecycler() {
        NoteContentProvider contentProvider = NoteContentProvider.get(getActivity());
        notes = contentProvider.getNotes();
        Collections.sort(notes);
        if (notes.isEmpty()) {
            mCollapsingLayout.setVisibility(View.GONE);
            mEmptyNotes.setVisibility(View.VISIBLE);
        } else {
            mCollapsingLayout.setVisibility(View.VISIBLE);
            mEmptyNotes.setVisibility(View.GONE);
        }
        mAdapter = new NoteAdapter(notes);
        mNotesRecyclerView.setAdapter(mAdapter);
    }

    public void createNewNote() {
        Note note = new Note();
        NoteContentProvider.get(getActivity()).addNote(note);
        Fragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putSerializable(NoteFragment.ARG_NOTE_ID, note.getId());
        fragment.setArguments(args);
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.note_slide_in, R.anim.list_slide_out, R.anim.list_slide_in,
                        R.anim.note_slide_out)
                .replace(R.id.content_frame, fragment, "NEW_NOTE")
                .addToBackStack("newNote")
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_gallery);

        updateRecycler();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getActivity(), PhotoGalleryActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mAdapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.filter(query);
        return false;
    }

    /**
     * NoteAdapter inner class.
     */
    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {

        private List<Note> mNotes;

        public NoteAdapter(List<Note> notes) {
            mNotes = notes;
            notesCopy.clear();
            notesCopy.addAll(mNotes);
            viewBinderHelper.setOpenOnlyOne(true);
        }

        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_note_list_item, parent, false);
            return new NoteHolder(inflatedView, mNotes);
        }

        @Override
        public void onBindViewHolder(final NoteHolder holder, int position) {
            Note note = mNotes.get(position);
            viewBinderHelper.bind(holder.mReveal, note.getId().toString());
            holder.bindNote(note);

            //off screen notes were not displaying deleteMode unless loaded first.
            mNotesRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    if (deleteMode) {
                        holder.mReveal.open(true);
                    }
                }

                @Override
                public void onChildViewDetachedFromWindow(View view) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        //Filter list for searching
        public void filter(String text) {
            mNotes.clear();
            if (text == null) {
                mNotes.addAll(notesCopy);
            } else {
                text = text.toLowerCase();
                for (Note note : notesCopy) {
                    if ((note.getTitle() != null) && (note.getTitle().toLowerCase().contains(text))
                            || (note.getBody() != null) && (note.getBody().toLowerCase().contains(text))) {
                        mNotes.add(note);
                    }
                }
            }
            notifyDataSetChanged();
        }

        //Filter list for priority
        private void showPriority() {
            List<Note> temp = new ArrayList<>();
            for (Note note : mNotes) {
                if (note.getPriority() == Note.PRIORITY_TRUE) {
                    temp.add(note);
                }
            }
            mAdapter = new NoteAdapter(temp);
            mNotesRecyclerView.setAdapter(mAdapter);
        }
    }

    /**
     * NoteHolder inner class.
     */
    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private List<Note> mNotes;
        private Note mNote;
        private SwipeRevealLayout mReveal;
        private TextView mTitle;
        private TextView mDate;
        private ImageButton mPriority;
        private CardView mCard;
        private File mPhotoFile;
        private ImageView mThumbnail;
        private ImageButton mDelete;
        private RelativeLayout mDescription;
        private View mTopDiv;
        private View mBottomDiv;

        public NoteHolder(View v, final List<Note> notes) {
            super(v);
            mNotes = notes;
            mTitle = (TextView) v.findViewById(R.id.note_title);
            mDate = (TextView) v.findViewById(R.id.note_date);
            mCard = (CardView) v.findViewById(R.id.recycler_card_view);
            mThumbnail = (ImageView) v.findViewById(R.id.note_thumbnail);
            mTopDiv = v.findViewById(R.id.divider_top);
            mBottomDiv = v.findViewById(R.id.divider_bottom);
            mReveal = (SwipeRevealLayout) v.findViewById(R.id.swipe_reveal);
            mReveal.setMinFlingVelocity(1000);
            mItems.add(mReveal);
            mDescription = (RelativeLayout) v.findViewById(R.id.description_layout);
            mDescription.setOnClickListener(this);
            mDescription.setOnLongClickListener(this);

            mPriority = (ImageButton) v.findViewById(R.id.note_list_priority);

            mDelete = (ImageButton) v.findViewById(R.id.list_delete_button);
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPhotoFile = NoteContentProvider.get(getActivity()).getPhotoFile(mNote);
                    mPhotoFile.delete();
                    NoteContentProvider.get(getActivity()).deleteNote(mNote.getId());
                    mNotes.remove(mNote);
                    notesCopy.remove(mNote); //NOTES DUPLICATING
                    mAdapter.notifyItemRemoved(getAdapterPosition());
                    mAdapter.notifyItemRangeChanged(getAdapterPosition(), mNotes.size());
                }
            });

            mThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ImageActivity.class);
                    i.putExtra(ImageActivity.IMAGE_PATH, mPhotoFile.getPath());
                    getActivity().startActivity(i);
                    getActivity().overridePendingTransition(R.anim.list_slide_in, R.anim.activity_fade_out);
                }
            });

            mReveal.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
                @Override
                public void onClosed(SwipeRevealLayout view) {
                    mReveal.close(true);
                    mTopDiv.setVisibility(View.GONE);
                    mBottomDiv.setVisibility(View.GONE);
                }

                @Override
                public void onOpened(SwipeRevealLayout view) {
                    mReveal.open(true);
                    if (!deleteMode) {
                        mTopDiv.setVisibility(View.VISIBLE);
                        mBottomDiv.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onSlide(SwipeRevealLayout view, float slideOffset) {

                }
            });
        }

        public void bindNote(Note note) {
            mNote = note;
            if (mNote.getPriority() == Note.PRIORITY_TRUE) {
                mPriority.setVisibility(View.VISIBLE);
            }
            mTitle.setText(mNote.getTitle());
            String date = (DateFormat.format("EEE MMM d, yyyy", mNote.getDate())).toString();
            mDate.setText(date);
            mPhotoFile = NoteContentProvider.get(getActivity()).getPhotoFile(mNote);
            if (mPhotoFile.exists()) {
                mCard.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(mPhotoFile)
                        .signature(new StringSignature(String.valueOf(mPhotoFile.lastModified())))
                        .into(mThumbnail);
            } else {
                mCard.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (deleteMode) {
                closeLayouts();
            }
            else if (mReveal.isOpened()) {
                mReveal.close(true);
            } else {
                closeLayouts();
                Fragment fragment = new NoteFragment();
                Bundle args = new Bundle();
                args.putSerializable(NoteFragment.ARG_NOTE_ID, mNote.getId());
                args.putString(NoteFragment.LIST_TAKE_PHOTO, NO_PHOTO);
                fragment.setArguments(args);
                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.note_slide_in, R.anim.list_slide_out, R.anim.list_slide_in,
                                R.anim.note_slide_out)
                        .replace(R.id.content_frame, fragment, "CLICKED_NOTE")
                        .addToBackStack("clickedNote")
                        .commit();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            openLayouts();
            mExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeLayouts();
                }
            });
            return true;
        }

        private void openLayouts() {
            viewBinderHelper.setOpenOnlyOne(false);
            deleteMode = true;
            for (SwipeRevealLayout layout : mItems) {
                layout.open(true);
            }
            mNewNote.startAnimation(note_implode_start);
        }

        private void closeLayouts() {
            viewBinderHelper.setOpenOnlyOne(true);
            deleteMode = false;
            for (SwipeRevealLayout layout : mItems) {
                layout.close(true);
            }
            mExit.startAnimation(exit_implode_start);
        }
    }
}
