package com.cam.boles.notacity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

/**
 * Created by boles on 5/6/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    public static final String NEEDS_PHOTO = "need_photo";
    public static final String NO_PHOTO = "no_photo";
    public static final String FROM_NOTE_ADAPTER = "note_adapter";

    private List<Note> mNotes;
    private List<Note> notesCopy = new ArrayList<>();
    private FragmentManager mFragmentManager;
    private FragmentActivity mContext;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public NoteAdapter(List<Note> notes, FragmentManager fragmentManager, FragmentActivity context) {
        mNotes = notes;
        notesCopy.addAll(mNotes);
        mFragmentManager = fragmentManager;
        mContext = context;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_note_list_item, parent, false);
        return new NoteHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        Note note = mNotes.get(position);
        viewBinderHelper.bind(holder.mReveal, note.getId().toString());
        holder.bindNote(note);

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    //filter list for searching
    public void filter(String text) {
        mNotes.clear();
        if (text == null) {
            mNotes.addAll(notesCopy);
        } else {
            text = text.toLowerCase();
            for (Note note : notesCopy){
                if((note.getTitle() != null) && (note.getTitle().toLowerCase().contains(text))
                        || (note.getBody() != null) && (note.getBody().toLowerCase().contains(text))) {
                    mNotes.add(note);
                }
            }
        }
        notifyDataSetChanged();
    }

    //ViewHolder inner class
    public class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private Note mNote;
        private SwipeRevealLayout mReveal;
        private TextView mTitle;
        private TextView mDate;
        private CardView mCard;
        private File mPhotoFile;
        private ImageView mThumbnail;
        private ImageButton mDelete;
        private RelativeLayout mDescription;
        private View mTopDiv;
        private View mBottomDiv;
        private List<SwipeRevealLayout> mItems = new ArrayList<>();
        private boolean picIsEmpty;

        public NoteHolder(View v) {
            super(v);
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

            mDelete = (ImageButton) v.findViewById(R.id.list_delete_button);
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPhotoFile = NoteContentProvider.get(mContext).getPhotoFile(mNote);
                    mPhotoFile.delete();
                    NoteContentProvider.get(mContext).deleteNote(mNote.getId());
                    mNotes.remove(mNote);
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), mNotes.size());
                }
            });

            mThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!picIsEmpty) {
                        Intent i = new Intent(mContext, ImageActivity.class);
                        i.putExtra(ImageActivity.IMAGE_PATH, mPhotoFile.getPath());
                        mContext.startActivity(i);
                        mContext.overridePendingTransition(R.anim.list_slide_in, R.anim.activity_fade_out);
                    }
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
                    mTopDiv.setVisibility(View.VISIBLE);
                    mBottomDiv.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSlide(SwipeRevealLayout view, float slideOffset) {

                }
            });
        }

        public void bindNote(Note note) {
            mNote = note;
            mTitle.setText(mNote.getTitle());
            String date = (DateFormat.format("EEE MMM d, yyyy", mNote.getDate())).toString();
            mDate.setText(date);
            mPhotoFile = NoteContentProvider.get(mContext).getPhotoFile(mNote);
            if (mPhotoFile.exists()) {
                picIsEmpty = false;
                Glide.with(mContext)
                        .load(mPhotoFile)
                        .signature(new StringSignature(String.valueOf(mPhotoFile.lastModified())))
                        .into(mThumbnail);
            } else {
                picIsEmpty = true;
                mThumbnail.setImageResource(R.drawable.ic_list_image_placeholder);
            }
        }

        @Override
        public void onClick(View v) {
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

        @Override
        public boolean onLongClick(View v) {
            for (SwipeRevealLayout layout : mItems) {
                layout.open(true);
            }
            return true;
        }
    }
}
