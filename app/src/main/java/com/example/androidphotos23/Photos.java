package com.example.androidphotos23;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.androidphotos23.model.Album;
import com.example.androidphotos23.model.Photo;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Photos extends AppCompatActivity {
    private ArrayList<Album> albums;
    private int albumPosition;
    private Album album;
    private ArrayList<Photo> photos;

    private Button addPhotoButton;
    private ListView photosList;

    static final int REQUEST_IMAGE_GET = 1;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(this.getFilesDir(), "albums.dat")))) {
            albums = (ArrayList<Album>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        albumPosition = (int) intent.getSerializableExtra("album_position");
        album = albums.get(albumPosition);
        photos = album.getPhotos();
        setTitle(album.getName());

        addPhotoButton = findViewById(R.id.add_photo_button);
        addPhotoButton.setOnClickListener(v -> selectImage());

        photosList = findViewById(R.id.photos_list);
        CustomAdapter adapter = new CustomAdapter(this, R.layout.photo, photos);
        photosList.setAdapter(adapter);
        photosList.setOnItemClickListener((parent, view, position, id) -> photoClick(position));
    }

    private void update() {
        photosList.setAdapter(new CustomAdapter(this, R.layout.photo, photos));
        try (ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("albums.dat", MODE_PRIVATE))) {
            oos.writeObject(albums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            if (!contains(selectedImage.getPath())) {
                Bitmap thumbnail;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    File dir = new ContextWrapper(getApplicationContext()).getDir("images", MODE_PRIVATE);
                    File path = new File(dir, "a" + selectedImage.getPath().replace('/', '_').replace(':', '_') + ".png");
                    try (FileOutputStream fos = new FileOutputStream(path)) {
                        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                photos.add(new Photo(selectedImage.getPath()));
                update();
            }
        }
    }

    private boolean contains(String filename) {
        for (Photo photo : photos) {
            if (photo.getFilename().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    private void removePhoto(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Photo");
        builder.setMessage("Are you sure you want to remove " + photos.get(position).getFilename() + "?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            File dir = new ContextWrapper(getApplicationContext()).getDir("images", Context.MODE_PRIVATE);
            File path = new File(dir, "a" + photos.get(position).getFilename().replace('/', '_').replace(':', '_') + ".png");
            path.delete();
            photos.remove(position);
            update();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void photoClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Photo Options");
        builder.setItems(R.array.photo_options_array, (dialog, which) -> {
            switch (which) {
                case 0: // display
                    Intent intent = new Intent(this, Display.class);
                    intent.putExtra("album_position", albumPosition);
                    intent.putExtra("photo_position", position);
                    startActivity(intent);
                    break;
                case 1: // remove
                    removePhoto(position);
                    break;
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}