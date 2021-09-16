package com.example.androidphotos23.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Photo object with path of image, tags, and datetime information
 *
 * @author Maxwell Wang
 * @author Girish Ganesan
 */
public class Photo implements Serializable {
    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 1L;


    /**
     * Tag inner class with key-value pair
     */
    public static class Tag implements Serializable {
        /**
         * Serial version ID
         */
        private static final long serialVersionUID = 1L;


        /**
         * Key part of tag
         */
        private String key;


        /**
         * Value part of tag
         */
        private String value;

        /**
         * Initializes tag to be key and value pair
         *
         * @param key   key part of tag
         * @param value value part of tag
         */
        public Tag(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Checks if tags are the same key-value pair
         */
        public boolean equals(Object o) {
            if (!(o instanceof Tag)) {
                return false;
            } else {
                Tag oTag = (Tag) o;
                return key.equals(oTag.key) && value.equals(oTag.value);
            }
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * Filename of image
     */
    private String filename;

    /**
     * Tags attached to the photo
     */
    private ArrayList<Tag> tags;

    /**
     * Initializes photo
     *
     * @param filename filename of photo
     */
    public Photo(String filename) {
        this.setFilename(filename);
        tags = new ArrayList<>();
    }

    /**
     * Checks if photo is equal to another using path
     */
    public boolean equals(Object o) {
        if (!(o instanceof Photo)) {
            return false;
        } else {
            Photo oPhoto = (Photo) o;
            return filename.equals(oPhoto.filename);
        }
    }

    /**
     * Get filename of photo
     *
     * @return filename of photo
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set filename of photo
     *
     * @param filename filename to be set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }


    /**
     * Get list of tags for photo
     *
     * @return list of tags for photo
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public Bitmap getThumbnail(Context context) throws FileNotFoundException {
        File dir = new ContextWrapper(context.getApplicationContext()).getDir("images", Context.MODE_PRIVATE);
        File path = new File(dir, "a" + filename.replace('/', '_').replace(':', '_') + ".png");
        return BitmapFactory.decodeStream(new FileInputStream(path));
    }
}