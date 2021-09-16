package com.example.androidphotos23;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidphotos23.model.Album;
import com.example.androidphotos23.model.Photo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Display extends AppCompatActivity {
    private ArrayList<Album> albums;
    private int albumPosition;
    private Album album;
    private ArrayList<Photo> photos;
    private int photoPosition;
    private Photo photo;
    private ArrayList<Photo.Tag> tags;

    private Button leftButton;
    private Button rightButton;
    private Button addTagButton;
    private Button movePhotoButton;
    private ImageView image;
    private ListView tagsList;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        Intent intent = getIntent();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(this.getFilesDir(), "albums.dat")))) {
            albums = (ArrayList<Album>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        albumPosition = (int) intent.getSerializableExtra("album_position");
        album = albums.get(albumPosition);
        photoPosition = (int) intent.getSerializableExtra("photo_position");
        photos = album.getPhotos();
        photo = photos.get(photoPosition);
        tags = photo.getTags();
        setTitle(photo.getFilename());

        leftButton = findViewById(R.id.left_button);
        leftButton.setOnClickListener(v -> goLeft());

        rightButton = findViewById(R.id.right_button);
        rightButton.setOnClickListener(v -> goRight());

        addTagButton = findViewById(R.id.add_tag_button);
        addTagButton.setOnClickListener(v -> addTag());

        movePhotoButton = findViewById(R.id.move_photo_button);
        movePhotoButton.setOnClickListener(v -> movePhoto());

        image = findViewById(R.id.displayed_photo);
        try {
            image.setImageBitmap(photo.getThumbnail(this));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        tagsList = findViewById(R.id.tags_list);
        ArrayAdapter<Photo.Tag> adapter = new ArrayAdapter<>(this, R.layout.text, tags);
        tagsList.setAdapter(adapter);
        tagsList.setOnItemClickListener((parent, view, position, id) -> tagClick(position));
    }

    private void update() {
        photo = photos.get(photoPosition);
        setTitle(photo.getFilename());
        tags = photo.getTags();
        try {
            image.setImageBitmap(photo.getThumbnail(this));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        tagsList.setAdapter(new ArrayAdapter<>(this, R.layout.text, tags));
        try (ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("albums.dat", MODE_PRIVATE))) {
            oos.writeObject(albums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void emptyAlbumUpdate() {
        photo = null;
        setTitle("Empty Album");
        tags = new ArrayList<>();
        image.setImageBitmap(null);
        tagsList.setAdapter(new ArrayAdapter<>(this, R.layout.text, tags));
        try (ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("albums.dat", MODE_PRIVATE))) {
            oos.writeObject(albums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goLeft() {
        photoPosition--;
        if (photoPosition < 0) {
            photoPosition = photos.size() - 1;
        }
        update();
    }

    private void goRight() {
        photoPosition++;
        if (photoPosition > photos.size() - 1) {
            photoPosition = 0;
        }
        update();
    }

    private boolean tagExists(String key, String value) {
        for (Photo.Tag tag : tags) {
            if (tag.getKey().equals(key) && tag.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void addTag() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Tag");
        builder.setMessage("Enter tag as key=value. Keys can only be person or location.");
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String[] tagInfo = input.getText().toString().split("=");
            if (tagInfo.length != 2 || (!tagInfo[0].equals("person") && !tagInfo[0].equals("location"))) {
                dialog.cancel();
            } else {
                if (tagExists(tagInfo[0], tagInfo[1])) {
                    dialog.cancel();
                } else {
                    tags.add(new Photo.Tag(tagInfo[0], tagInfo[1]));
                    update();
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private boolean differentAlbumName(String albumName) {
        if (albumName.equals(album.getName())) {
            return false;
        }
        for (Album a : albums) {
            if (albumName.equals(a.getName())) {
                return true;
            }
        }
        return false;
    }

    private int getDestinationAlbumPosition(String albumName) {
        for (int i = 0; i < albums.size(); i++) {
            if (albumName.equals(albums.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    private void movePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Move Photo");
        ArrayList<Album> options = new ArrayList<>(albums);
        options.remove(albumPosition);
        if (options.isEmpty()) {
            builder.setMessage("There are no other albums to move this photo to.");
            builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        } else {
            builder.setMessage("Which album do you want to move this photo to? Here are the options: " + options);
            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String albumName = input.getText().toString();
                if (!differentAlbumName(albumName)) {
                    dialog.cancel();
                } else {
                    Photo p = photos.remove(photoPosition);
                    albums.get(getDestinationAlbumPosition(albumName)).getPhotos().add(p);
                    if (photos.isEmpty()) {
                        emptyAlbumUpdate();
                    } else {
                        if (photoPosition > photos.size() - 1) {
                            photoPosition = 0;
                        }
                        update();
                    }
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        }
        builder.show();
    }

    private void tagClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Tag");
        builder.setMessage("Are you sure you want to delete " + tags.get(position).toString() + "?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            tags.remove(position);
            update();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}