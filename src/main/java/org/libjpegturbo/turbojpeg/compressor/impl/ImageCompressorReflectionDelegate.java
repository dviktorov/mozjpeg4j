package org.libjpegturbo.turbojpeg.compressor.impl;

import org.libjpegturbo.turbojpeg.compressor.api.ImageCompressor;
import org.libjpegturbo.turbojpeg.compressor.api.ImageProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * Singleton class that delegates calls to the ImageCompressorImpl via reflection.
 * It may be useful for the environments where direct code is not available, e.g. OSGI bundle.
 *
 * @since version 1.0,	01/13/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageCompressorReflectionDelegate implements ImageCompressor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Object compressor;
    private Method isUsable;
    private Method compressImageBuffer;
    private Method compressImageFile;

    protected boolean usable = true;

    public ImageCompressorReflectionDelegate(String implClassName) {
        try {

            Class clazz = Class.forName(implClassName);
            compressor = clazz.newInstance();

            isUsable = clazz.getDeclaredMethod(METHOD_IS_USABLE);
            compressImageBuffer = clazz.getDeclaredMethod(METHOD_COMPRESS_IMAGE, byte[].class, Map.class);
            compressImageFile = clazz.getDeclaredMethod(METHOD_COMPRESS_IMAGE, File.class, File.class, Map.class);

        } catch (Exception e) {
            log.error("Couldn't not create instance of " + COMPRESSOR_DEFAULT_IMPL, e);
            setUnusable();
        }
    }

    public ImageCompressorReflectionDelegate() {
        this(COMPRESSOR_DEFAULT_IMPL);
    }

    /**
     * Clear all unnecessary fields if implementation is not usable.
     */
    private void setUnusable() {
        usable = false;
        compressor = null;
        isUsable = null;
        compressImageBuffer = null;
        compressImageFile = null;
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
    public Map<String, Object> compressImage(byte[] inputImage, Map<String, Object> processParameters) throws ImageProcessException {
        try {
            return (Map<String, Object>) compressImageBuffer.invoke(compressor, inputImage, processParameters);
        } catch (Exception e) {
            throw new ImageProcessException(e);
        }
    }

    @Override
    public Map<String, Object> compressImage(File inFile, File outFile, Map<String, Object> processParameters) throws ImageProcessException {
        try {
            return (Map<String, Object>) compressImageFile.invoke(compressor, inFile, outFile, processParameters);
        } catch (Exception e) {
            throw new ImageProcessException(e);
        }
    }

}
