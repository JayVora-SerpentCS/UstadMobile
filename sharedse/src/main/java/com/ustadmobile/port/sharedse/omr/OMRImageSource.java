/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustadmobile.port.sharedse.omr;


/**
 * Interface representing an image buffer (e.g. from a live preview) that can be 
 * checked to see if it's a valid OMR image with finder patterns etc.  This 
 * requires high speed retrieval of particular cropped areas of the image in
 * grayscale
 * 
 * @author mike
 */
public interface OMRImageSource {
    
     /**
     * Flag used with getGrayscaleImage etc. for it to store min and max values found: indicates the minimum (e.g. darkest) spot found
     */
    public static final int MINMAX_BUF_MIN = 0;
    
    /**
     * Flag used with getGrayscaleImage etc. for it to store min and max values found: indicates the maximum (e.g. brightest) spot found
     */
    public static final int MINMAX_BUF_MAX = 1;
    
    /**
     * Puts the grayscale image into the buffer - each pixel is 32bit aRGB pixel that will have
     * the same R, G and B values with full opacity
     * 
     * @param buf
     * @param x
     * @param y
     * @param width
     * @param height
     * @@param minMaxBuffer a Buffer that will contain the minimum (e.g. darkest) and maximum (e.g. brightest) value found
     * @return 
     */
    public void getGrayscaleImage(int[][] buf, int x, int y, int width, int height, int[] minMaxBuffer);

    /**
     * Returns the width of the image
     * 
     * @return Width of image in pixels
     */
    public int getWidth();
    
    /**
     * Returns the height of the image
     * 
     * @return Height of image in pixels
     */
    public int getHeight();
    
    /**
     * Update the raw buffer of the image data
     * 
     * @param buf 
     */
    public void setBuffer(byte[] buf);
    
    /**
     * Get the raw buffer of the image data
     */
    public byte[] getBuffer();
    
    /**
     * Return a shallow copy (e.g. references only) of this OMRImageSource
     * e.g. a new instance of the same class with the same width and height
     * 
     * This is used by the thread that runs in the background to check for matches
     * 
     * @return new instance of the same implementing class with the same width and height
     */
    public OMRImageSource copy();
    
}
