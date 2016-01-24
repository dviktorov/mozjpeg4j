package org.libjpegturbo.turbojpeg.processor.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Information related to a processed image.
 *
 * @since version 1.0,	01/07/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageProcessInfo {

    protected final Map<String, Object> map;

    private ImageProcessInfo(Map<String, Object> inMap) {
        if (inMap == null) {
            throw new IllegalArgumentException("Input map can't be null");
        }
        this.map = inMap;
    }

    public int getInputWidth() {
        return getValue(ProcessKey.INPUT_WIDTH);
    }

    public ImageProcessInfo setInputWidth(int width) {
        return setValue(ProcessKey.INPUT_WIDTH, width);
    }

    public int getInputHeight() {
        return getValue(ProcessKey.INPUT_HEIGHT);
    }

    public ImageProcessInfo setInputHeight(int height) {
        return setValue(ProcessKey.INPUT_HEIGHT, height);
    }

    public int getOutputWidth() {
        return getValue(ProcessKey.OUTPUT_WIDTH);
    }

    public ImageProcessInfo setOutputWidth(int width) {
        return setValue(ProcessKey.OUTPUT_WIDTH, width);
    }

    public int getOutputHeight() {
        return getValue(ProcessKey.OUTPUT_HEIGHT);
    }

    public ImageProcessInfo setOutputHeight(int height) {
        return setValue(ProcessKey.OUTPUT_HEIGHT, height);
    }

    public byte[] getOutputImage() {
        return getValue(ProcessKey.OUTPUT_IMAGE);
    }

    public ImageProcessInfo setOutputImage(byte[] outputImage) {
        return setValue(ProcessKey.OUTPUT_IMAGE, outputImage);
    }

    public int getOutputImageSize() {
        return getValue(ProcessKey.OUTPUT_IMAGE_SIZE);
    }

    public ImageProcessInfo setOutputImageSize(int size) {
        return setValue(ProcessKey.OUTPUT_IMAGE_SIZE, size);
    }

    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(map);
    }

    public static ImageProcessInfo fromMap(Map<String, Object> map) {
        return new ImageProcessInfo(map);
    }

    public static ImageProcessInfo newInstance() {
        return new ImageProcessInfo(new LinkedHashMap<String, Object>());
    }

    public ImageProcessInfo setValue(ProcessKey key, Object value) {
        map.put(key.name(), value);
        return this;
    }

    public <T> T getValue(ProcessKey key) {
        T value = (T) map.get(key.name());
        return value != null ? value : (T) key.defaultValue;
    }

    public String getInputDimension() {
        return getInputWidth() + "x" + getInputHeight();
    }

    public String getOutputDimension() {
        return getOutputWidth() + "x" + getOutputHeight();
    }

    public enum ProcessKey {

        INPUT_WIDTH(0),
        INPUT_HEIGHT(0),
        OUTPUT_WIDTH(0),
        OUTPUT_HEIGHT(0),
        OUTPUT_IMAGE(null),
        OUTPUT_IMAGE_SIZE(0);

        private final Object defaultValue;

        ProcessKey(Object inValue) {
            this.defaultValue = inValue;
        }

    }

}
