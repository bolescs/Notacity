package com.cam.boles.notacity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boles on 5/19/2017.
 *
 * Fragment displaying recyclerview of images in photo gallery.
 */

public class MyGalleryFragment extends Fragment {

    private FragmentManager mFragmentManager;
    private RecyclerView mImagesRecyclerView;
    private GalleryAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        mFragmentManager = getActivity().getSupportFragmentManager();
        mImagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.images_recycler_view);
        mImagesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        updateRecycler();

        return rootView;
    }

    private void updateRecycler() {
        NoteContentProvider contentProvider = NoteContentProvider.get(getActivity());
        List<Note> notes = contentProvider.getNotes();

        List<Note> notesWithPic = new ArrayList<>();

        for (Note note : notes) {
            File toAdd = contentProvider.getPhotoFile(note);
            if (toAdd.exists()) {
                notesWithPic.add(note);
            }
        }

        mAdapter = new GalleryAdapter(notesWithPic, mFragmentManager, getActivity());
        mImagesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
