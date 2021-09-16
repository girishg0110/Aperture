package com.example.androidphotos23.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Album that holds photos
 *
 * @author Maxwell Wang
 * @author Girish Ganesan
 */
public class Album implements Serializable {
    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 1L;
    /**
     * Unique name of the album
     */
    private String name;
    /**
     * List of photos that the album contains
     */
    private ArrayList<Photo> photos;


    /**
     * Initializes album to have a name and empty list of photos
     *
     * @param name name for the album
     */
    public Album(String name) {
        this.setName(name);
        photos = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if album is equal to another using names
     */
    public boolean equals(Object o) {
        if (!(o instanceof Album)) {
            return false;
        } else {
            Album oAlbum = (Album) o;
            return name.equals(oAlbum.name);
        }
    }

    /**
     * Checks if photoToLookFor is within the album
     *
     * @param photoToLookFor the photo to search for in the album
     * @return if photoToLookFor is in the album
     */
    public boolean hasPhoto(Photo photoToLookFor) {
        for (Photo photo : photos) {
            if (photo.equals(photoToLookFor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds photo to album if it isn't already there
     *
     * @param photo photo to be added
     */
    public void addPhoto(Photo photo) {
        if (!hasPhoto(photo)) {
            photos.add(photo);
        }
    }

    /**
     * Deletes photo from album using it's unique path
     *
     * @param path path of photo to be deleted
     */
    public void deletePhoto(String path) {
        for (Photo photo : photos) {
            if (photo.getFilename().equals(path)) {
                photos.remove(photo);
                return;
            }
        }
    }

    /**
     * Gets name of album
     *
     * @return name of album
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of album
     *
     * @param name name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets photos of album
     *
     * @return list of photos in album
     */
    public ArrayList<Photo> getPhotos() {
        return photos;
    }


    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }
}
