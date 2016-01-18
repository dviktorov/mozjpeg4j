package org.libjpegturbo.turbojpeg.compressor.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Information related to a processed image.
 *
 * @since version 1.0,	01/13/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageProcessParameters {

    protected final Map<String, Object> map;

    private ImageProcessParameters(Map<String, Object> inMap) {
        if (inMap == null) {
            throw new IllegalArgumentException("Input map can't be null");
        }
        this.map = inMap;
    }

    public static ImageProcessParameters fromMap(Map<String, Object> map) {
        return new ImageProcessParameters(map);
    }

    public static ImageProcessParameters fromEmptyMap() {
        return new ImageProcessParameters(new LinkedHashMap<String, Object>());
    }

    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(map);
    }

    public <T> T getValue(ParamKey key) {
        T value = (T) map.get(key.name());
        return value != null ? value : (T) key.defaultValue;
    }

    public ImageProcessParameters setValue(ParamKey key, Object value) {
        map.put(key.name(), value);
        return this;
    }

    public int getQuality() {
        return getValue(ParamKey.QUALITY);
    }

    public ImageProcessParameters setQuality(int value) {
        return setValue(ParamKey.QUALITY, value);
    }

    public int getSubsampling() {
        return getValue(ParamKey.SUBSAMPLING);
    }

    public ImageProcessParameters setSubsampling(int value) {
        return setValue(ParamKey.SUBSAMPLING, value);
    }

    public int getFlags() {
        return getValue(ParamKey.FLAGS);
    }

    public ImageProcessParameters setFlags(int value) {
        return setValue(ParamKey.FLAGS, value);
    }

    public int getNumerator() {
        return getValue(ParamKey.SCALE_NUMERATOR);
    }

    public ImageProcessParameters setNumerator(int value) {
        return setValue(ParamKey.SCALE_NUMERATOR, value);
    }

    public int getDenominator() {
        return getValue(ParamKey.SCALE_DENOMINATOR);
    }

    public ImageProcessParameters setDenominator(int value) {
        return setValue(ParamKey.SCALE_DENOMINATOR, value);
    }

    public enum ParamKey {

        QUALITY(100),       // Value from 1 to 100. Most usable values are from 5 to 95. Main parameter that determines the size of output.
        SUBSAMPLING(-1),    // Subsampling parameters for the output image
        FLAGS(0),
        SCALE_NUMERATOR(1),
        SCALE_DENOMINATOR(1);

        private final Object defaultValue;

        ParamKey(Object inValue) {
            this.defaultValue = inValue;
        }

    }

}
