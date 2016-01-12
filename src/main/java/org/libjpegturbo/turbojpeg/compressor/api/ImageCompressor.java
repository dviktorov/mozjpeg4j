package org.libjpegturbo.turbojpeg.compressor.api;

import org.libjpegturbo.turbojpeg.TJTransform;

import java.io.File;

/**
 *
 * Interface for JPEG compression utilities.
 *
 * @since version 1.0,	01/12/2016
 *
 * @author Dmitry Viktorov
 *
 */
public interface ImageCompressor {

    public boolean isUsable();

    public ImageProcessInfo compressJpeg(File inFile, File outFile, int quality) throws ImageProcessException;

    public ImageProcessInfo compressJpeg(File inFile, File outFile, int quality, int outSubsamp, int flags, int scaleNum, int scaleDenom) throws ImageProcessException;

    public ImageProcessInfo compressJpeg(byte[] inputImage, TJTransform transform, int quality, int outSubsamp, int flags, int scaleNum, int scaleDenom) throws ImageProcessException;

}
