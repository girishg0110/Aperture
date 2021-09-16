package com.example.androidphotos23;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidphotos23.model.Album;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Albums extends AppCompatActivity {
    private ListView albumsList;
    private ArrayList<Album> albums;

    private Button createAlbumButton;
    private Button searchButton;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums);
        setTitle("Albums");

        createAlbumButton = findViewById(R.id.create_album_button);
        createAlbumButton.setOnClickListener(v -> createAlbum());

        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v->search());

        albumsList = findViewById(R.id.tags_list);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(this.getFilesDir(), "albums.dat")))) {
            albums = (ArrayList<Album>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            try (ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("albums.dat", MODE_PRIVATE))) {
                oos.writeObject(new ArrayList<Album>());
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(this.getFilesDir(), "albums.dat")))) {
                    albums = (ArrayList<Album>) ois.readObject();
                }
            } catch (IOException | ClassNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
        ArrayAdapter<Album> adapter = new ArrayAdapter<>(this, R.layout.text, albums);
        albumsList.setAdapter(adapter);
        albumsList.setOnItemClickListener((parent, view, position, id) -> {
            albumClick(position);
        });
    }

    private void update() {
        albumsList.setAdapter(new ArrayAdapter<>(this, R.layout.text, albums));
        try (ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("albums.dat", MODE_PRIVATE))) {
            oos.writeObject(albums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Album");
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String albumName = input.getText().toString();
            if (albums.stream().map(Album::toString).collect(Collectors.toList()).contains(albumName)) {
                dialog.cancel();
            } else {
                albums.add(new Album(albumName));
                update();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void search() {
        // TODO: implement search for key value pair
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText tag1 = new EditText(this);
        EditText val1 = new EditText(this);
        EditText tag2 = new EditText(this);
        EditText val2 = new EditText(this);
        tag1.setHint("Tag 1");
        val1.setHint("Value 1");
        tag2.setHint("(Optional) Tag 2");
        val2.setHint("(Optional) Value 2");
        tag1.setInputType(InputType.TYPE_CLASS_TEXT);
        val1.setInputType(InputType.TYPE_CLASS_TEXT);
        tag2.setInputType(InputType.TYPE_CLASS_TEXT);
        val2.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout button_layout = new LinearLayout(this);
        button_layout.setOrientation(LinearLayout.HORIZONTAL);
        CheckBox andOption = new CheckBox(this);
        andOption.setChecked(false);
        andOption.setText("AND");
        CheckBox orOption = new CheckBox(this);
        orOption.setChecked(false);
        orOption.setText("OR");
        button_layout.addView(andOption);
        button_layout.addView(orOption);

        layout.addView(tag1);
        layout.addView(val1);
        layout.addView(button_layout);
        layout.addView(tag2);
        layout.addView(val2);
        builder.setView(layout);
        builder.setPositiveButton("Search", (dialog, which) -> {
           String tagText1 = tag1.getText().toString();
           String valText1 = val1.getText().toString();
           String tagText2 = tag2.getText().toString();
           String valText2 = val2.getText().toString();
           if (tagText1.length() == 0 || valText1.length() == 0) {
               // tag1 and val1 must be filled
               AlertDialog.Builder fieldsNotFilledError = new AlertDialog.Builder(this);
               fieldsNotFilledError.setTitle("ERROR");
               fieldsNotFilledError.setMessage("Please fill in the first two fields.");
               fieldsNotFilledError.setNegativeButton("OK", (d, w) -> d.cancel());
               fieldsNotFilledError.show();
           } else if ((!tagText1.equals("location") && !tagText1.equals("person")) ||
                        (tagText2.length() > 0 && !tagText2.equals("location") && !tagText2.equals("person"))) {
               // tag1 and tag2 must be either empty or location/person
               AlertDialog.Builder invalidTagsError = new AlertDialog.Builder(this);
               invalidTagsError.setTitle("ERROR");
               invalidTagsError.setMessage("\"location\" and \"person\" are the only valid tags.");
               invalidTagsError.setNegativeButton("OK", (d, w) -> d.cancel());
               invalidTagsError.show();
           } else if (tagText2.length() == 0 ^ valText2.length() == 0) {
               // for tag2-val2 both must both be filled or neither
               AlertDialog.Builder onlyOneSecondTagFieldFilled = new AlertDialog.Builder(this);
               onlyOneSecondTagFieldFilled.setTitle("ERROR");
               onlyOneSecondTagFieldFilled.setMessage("Only one field is filled in the second tag-value pair.");
               onlyOneSecondTagFieldFilled.setNegativeButton("OK", (d, w) -> d.cancel());
               onlyOneSecondTagFieldFilled.show();
           } else if (tagText2.length() > 0 && andOption.isChecked() == orOption.isChecked()) {
               // choose one checkbox
               AlertDialog.Builder buttonSelectionError = new AlertDialog.Builder(this);
               buttonSelectionError.setTitle("ERROR");
               buttonSelectionError.setMessage("Select exactly one checkbox: AND or OR.");
               buttonSelectionError.setNegativeButton("OK", (d, w) -> d.cancel());
               buttonSelectionError.show();
           } else {
               Intent intent = new Intent(this, Search.class);
               intent.putExtra("tag1", tagText1);
               intent.putExtra("val1", valText1);
               intent.putExtra("tag2", tagText2);
               intent.putExtra("val2", valText2);
               intent.putExtra("and", andOption.isChecked());
               startActivity(intent);
           }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void renameAlbum(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Album");
        EditText input = new EditText(this);
        input.setText(albums.get(position).getName());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String albumName = input.getText().toString();
            if (albums.stream().map(Album::toString).collect(Collectors.toList()).contains(albumName)) {
                dialog.cancel();
            } else {
                albums.get(position).setName(albumName);
                update();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deleteAlbum(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Album");
        builder.setMessage("Are you sure you want to delete " + albums.get(position).getName() + "?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            albums.remove(position);
            update();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @SuppressWarnings("unchecked")
    private void albumClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Album Options");
        builder.setItems(R.array.album_options_array, (dialog, which) -> {
            switch (which) {
                case 0: // open
                    Intent intent = new Intent(this, Photos.class);
                    intent.putExtra("album_position", position);
                    startActivity(intent);
                    break;
                case 1: // rename
                    renameAlbum(position);
                    break;
                case 2: // delete
                    deleteAlbum(position);
                    break;
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}