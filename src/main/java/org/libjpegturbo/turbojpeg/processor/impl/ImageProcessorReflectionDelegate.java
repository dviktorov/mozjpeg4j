package org.libjpegturbo.turbojpeg.processor.impl;

import org.libjpegturbo.turbojpeg.processor.api.ImageProcessException;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * Delegate that by default calls ImageProcessorImpl. It may be useful at places where direct code is
 * not available, e.g. OSGi bundle.
 *
 * @since version 1.0,	01/23/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageProcessorReflectionDelegate implements ImageProcessor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Object compressor;
    private Method isUsable;
    private Method compressGeneral;
    private Method compressJpeg;
    private Method decompressGeneral;
    private Method decompressJpeg;

    protected boolean usable = true;

    public ImageProcessorReflectionDelegate(String implClassName) {
        try {

            // Bind methods from impl.
            Class clazz = Class.forName(implClassName);
            compressor = clazz.newInstance();

            isUsable = clazz.getDeclaredMethod(METHOD_IS_USABLE);
            compressGeneral = clazz.getDeclaredMethod(METHOD_COMPRESS_GENERAL, BufferedImage.class, int.class, int.class, int.class);
            compressJpeg = clazz.getDeclaredMethod(METHOD_COMPRESS_JPEG, byte[].class, int.class, int.class, int.class, int.class, int.class);
            decompressGeneral = clazz.getDeclaredMethod(METHOD_DECOMPRESS_GENERAL, byte[].class, int.class, int.class, int.class);
            decompressJpeg = clazz.getDeclaredMethod(METHOD_DECOMPRESS_JPEG, byte[].class, int.class, int.class, int.class);

        } catch (Exception e) {
            log.error("Couldn't not create instance of " + COMPRESSOR_DEFAULT_IMPL, e);
            setUnusable();
        }
    }

    public ImageProcessorReflectionDelegate() {
        this(COMPRESSOR_DEFAULT_IMPL);
    }

    /**
     * Clear all unnecessary fields if implementation is not usable.
     */
    protected void setUnusable() {
        usable = false;
        compressor = null;
        isUsable = null;
        compressGeneral = null;
        compressJpeg = null;
        decompressGeneral = null;
        decompressJpeg = null;
    }

    @Override
    public boolean isUsable() {
        try {
            if (compressor != null && isUsable != null) {
                Object result = isUsable.invoke(compressor);
                return result instanceof Boolean && ((Boolean) result) == true;
            }
        } catch (Exception e) {}
        return false;
    }

    @Override
    public ByteArrayInputStream compressGeneralImage(BufferedImage inImage, int quality, int subsampling, int flags) throws ImageProcessException {
        try {
            return (ByteArrayInputStream) compressGeneral.invoke(compressor, inImage, quality, subsampling, flags);
        } catch (Exception e) {
            throw new ImageProcessException(e);
        }
    }

    @Override
    public ByteArrayInputStream compressJpegImage(byte[] inImage, int width, int height, int quality, int subsampling, int flags) throws ImageProcessException {
        try {
            return (ByteArrayInputStream) compressJpeg.invoke(compressor, inImage, width, height, quality, subsampling, flags);
        } catch (Exception e) {
            throw new ImageProcessException(e);
        }
    }

    @Override
    public BufferedImage decompressGeneralImage(byte[] inImage, int numerator, int denominator, int flags) throws ImageProcessException {
        try {
            return (BufferedImage) decompressGeneral.invoke(compressor, inImage, numerator, denominator, flags);
        } catch (Exception e) {
            throw new ImageProcessException(e);
        }
    }

    @Override
    public Map<String, Object> decompressJpegImage(byte[] inImage, int numerator, int denominator, int flags) throws ImageProcessException {
        try {
            return (Map<String, Object>) decompressJpeg.invoke(compressor, inImage, numerator, denominator, flags);
        } catch (Exception e) {
            throw new ImageProcessException(e);
        }
    }

}
