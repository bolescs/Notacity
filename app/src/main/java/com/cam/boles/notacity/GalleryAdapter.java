package com.cam.boles.notacity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.util.List;

/**
 * Created by boles on 5/19/2017.
 *
 * Adapter for the Photo Gallery.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageHolder> {

    public static final String FROM_GALLERY_ADAPTER = "gallery_adapter";

    private List<Note> mNotes;
    //private FragmentManager mFragmentManager;
    private FragmentActivity mContext;

    public GalleryAdapter(List<Note> notes, FragmentManager fragmentManager, FragmentActivity context) {
        mNotes = notes;
        //mFragmentManager = fragmentManager;
        mContext = context;
    }

    @Override
    public GalleryAdapter.ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_gallery_item, parent, false);
        return new GalleryAdapter.ImageHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ImageHolder holder, int position) {
        Note note = mNotes.get(position);
        holder.bindNote(note);
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    //ViewHolder inner class
    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Note mNote;
        private ImageView mPicture;

        public ImageHolder(View v) {
            super(v);
            mPicture = (ImageView) v.findViewById(R.id.gallery_item);
            mPicture.setOnClickListener(this);
        }

        public void bindNote(Note note) {
            mNote = note;
            File image = NoteContentProvider.get(mContext).getPhotoFile(mNote);
            Glide.with(mContext)
                    .load(image)
                    .signature(new StringSignature(String.valueOf(image.lastModified())))
                    .into(mPicture);
        }

        public void onClick(View v) {
            Intent intent = GalleryViewPager.newIntent(mContext, mNote.getId());
            mContext.startActivity(intent);
        }
    }
}
