package org.libjpegturbo.turbojpeg.compressor.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

/**
 *
 * Interface for JPEG compression utilities. All methods are expected to be thread-safe.
 *
 * @since version 1.0,	01/12/2016
 *
 * @author Dmitry Viktorov
 *
 */
public interface ImageCompressor {

    public final static String COMPRESSOR_DEFAULT_IMPL = "org.libjpegturbo.turbojpeg.compressor.impl.ImageCompressorImpl";
    public final static String METHOD_IS_USABLE = "isUsable";
    public final static String METHOD_COMPRESS_IMAGE = "compressImage";

    public boolean isUsable();

    public Map<String, Object> compressImage(BufferedImage inImage, int quality, int subsampling, int flags) throws ImageProcessException;

    public Map<String, Object> compressImage(byte[] inputImage, Map<String, Object> processParameters) throws ImageProcessException;

    public Map<String, Object> compressImage(File inFile, File outFile, Map<String, Object> processParameters) throws ImageProcessException;

}
