package org.libjpegturbo.turbojpeg.processor.api;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 *
 * Interface for JPEG compression algorithms. All methods are thread-safe.
 *
 * @since version 1.0,	01/12/2016
 *
 * @author Dmitry Viktorov
 *
 */
public interface ImageProcessor {

    public final static String COMPRESSOR_DEFAULT_IMPL = "org.libjpegturbo.turbojpeg.processor.impl.ImageProcessorImpl";
    public final static String METHOD_IS_USABLE = "isUsable";
    public final static String METHOD_COMPRESS_GENERAL = "compressGeneralImage";
    public final static String METHOD_COMPRESS_JPEG = "compressJpegImage";
    public final static String METHOD_DECOMPRESS_JPEG = "decompressJpegImage";
    public final static String METHOD_DECOMPRESS_GENERAL = "decompressGeneralImage";

    public boolean isUsable();

    /**
     * Decompresses the given image byte array to BufferedImage. Image byte array represents the original
     * binary of the image.
     *
     * This method should be used when the original image is Non-JPEG or the targeted output is the BufferedImage.
     */
    public BufferedImage decompressGeneralImage(byte[] inImage, int numerator, int denominator, int flags) throws ImageProcessException;

    /**
     * Decompresses the given JPEG message to byte array. It has internal specific to MozJpeg and
     * and should be processed further only by MozJpeg.
     *
     * The results can be obtained by wrapping the map over ImageProcessInfo.
     */
    public Map<String, Object> decompressJpegImage(byte[] inImage, int numerator, int denominator, int flags) throws ImageProcessException;

    /**
     * Compresses the submitted decompressed BufferedImage.
     *
     * @param inImage - byte array of a JPEG file is required
     * @return ImageProcessInfo with embedded compressed JPEG image as byte array
     * @throws ImageProcessException
     */
    public ByteArrayInputStream compressGeneralImage(BufferedImage inImage, int quality, int subsampling, int flags) throws ImageProcessException;

    /**
     * Compresses the submitted decompressed JPEG file to its binary.
     *
     * The decompressed byte array must the one created by the MozJpeg compress method.
     */
    public ByteArrayInputStream compressJpegImage(byte[] inImage, int width, int height, int quality, int subsampling, int flags) throws ImageProcessException;

}
