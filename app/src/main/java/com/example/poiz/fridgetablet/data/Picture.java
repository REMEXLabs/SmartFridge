package com.example.poiz.fridgetablet.data;

/**
 * Created by poiz on 07.09.2016.
 */

import java.util.Date;

/**
 * Class to hold information about a Picture
 */
public class Picture {
    String name;
    byte[] imageBytes;
    long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Constructor
     * @param name name of the image
     * @param imageBytes bytes of the image
     */
    public Picture(String name, byte[] imageBytes) {
        super();
        this.name = name;
        this.imageBytes = imageBytes;
    }
    public Picture(String name, byte[] imageBytes, long timestamp) {
        super();
        this.name = name;
        this.imageBytes = imageBytes;
        this.timestamp = timestamp;
    }


    public Picture(){}

    /**
     * returns the Name of the Picutre
     * @return Name of the Picutre
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Picture
     * @param name name of the Picture
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the Image
     * @return bytes of the image
     */
    public byte[] getImageBytes() {
        return imageBytes;
    }

    /**
     * Sets the Image
     * @param imageBytes bytes of the image
     */
    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}