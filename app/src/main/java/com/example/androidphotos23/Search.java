package com.example.androidphotos23;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidphotos23.model.Album;
import com.example.androidphotos23.model.Photo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Search extends AppCompatActivity {

    private ListView resultList;
    private ArrayList<Album> albums;
    private ArrayList<Photo> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(this.getFilesDir(), "albums.dat")))) {
            albums = (ArrayList<Album>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        String tag1 = intent.getStringExtra("tag1");
        String val1 = intent.getStringExtra("val1");
        String tag2 = intent.getStringExtra("tag2");
        String val2 = intent.getStringExtra("val2");
        boolean andOption = intent.getBooleanExtra("and", false);
        setTitle((tag2.length() == 0) ? tag1 + "=" + val1 : tag1 + "=" + val1 + (andOption ? " AND " : " OR ") + tag2 + "=" + val2);

        photos = new ArrayList<Photo>();
        for (Album a : albums) {
            for (Photo p : a.getPhotos()) {
                boolean duplicate = false;
                for (Photo p_other : photos) {
                    if (p_other.equals(p)) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    boolean flag1 = false;
                    boolean flag2 = false;
                    for (Photo.Tag t : p.getTags()) {
                        if (startsWith(t.getKey(), tag1) && startsWith(t.getValue(), val1)) flag1 = true;
                        if (startsWith(t.getKey(), tag2) && startsWith(t.getValue(), val2)) flag2 = true;
                    }
                    boolean passedTagCheck = false;
                    if (tag2.length() == 0) passedTagCheck = flag1; // no second tag provided
                    else passedTagCheck = (andOption && (flag1 && flag2)) || (!andOption && (flag1 || flag2));
                    if (passedTagCheck) {
                        photos.add(p);
                    }
                }
            }
        }

        resultList = findViewById(R.id.result_list);
        CustomAdapter adapter = new CustomAdapter(this, R.layout.photo, photos);
        resultList.setAdapter(adapter);
    }

    private boolean startsWith(String str, String prefix) {
        if (str.length() < prefix.length()) return false;
        return str.toLowerCase().substring(0, prefix.length()).equals(prefix.toLowerCase());
    }
}