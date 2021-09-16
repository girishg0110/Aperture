package com.example.androidphotos23;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.androidphotos23.model.Photo;

import java.io.FileNotFoundException;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Photo> {
    private final Context context;
    private final int resourceId;
    private final List<Photo> photos;

    public CustomAdapter(Context context, int resourceId, List<Photo> photos) {
        super(context, resourceId, photos);
        this.context = context;
        this.resourceId = resourceId;
        this.photos = photos;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        final ImageView image;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            view = inflater.inflate(resourceId, parent, false);
        } else {
            view = convertView;
        }

        try {
            image = view.findViewById(R.id.image_view);

            if (image == null) {
                throw new RuntimeException("Failed to find view with ID "
                        + context.getResources().getResourceName(R.id.image_view)
                        + " in item layout");
            }
        } catch (ClassCastException e) {
            Log.e("CustomAdapter", "You must supply a resource ID for an ImageView");
            throw new IllegalStateException(
                    "CustomAdapter requires the resource ID to be an ImageView", e);
        }

        Photo photo = photos.get(position);
        try {
            image.setImageBitmap(photo.getThumbnail(context));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return view;
    }
}
