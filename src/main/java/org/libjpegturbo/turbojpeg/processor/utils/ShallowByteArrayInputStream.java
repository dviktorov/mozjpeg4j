package org.libjpegturbo.turbojpeg.processor.utils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

/**
 * Provides direct access to the fields of ByteArrayInputStream.
 * It's may useful to retrieve the fields directly and save time on copy of the
 * internal byte array while reading the stream.
 *
 * @since version 1.0,	01/23/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ShallowByteArrayInputStream extends ByteArrayInputStream {

    public ShallowByteArrayInputStream(byte[] buf, int offset, int length) {
        super(buf, offset, length);
    }

    public ShallowByteArrayInputStream(ByteArrayInputStream is) throws ReflectiveOperationException {
        super(getBuffer(is), getPosition(is), getCount(is));
    }

    public byte[] getBuffer() {
        return buf;
    }

    public int getPosition() {
        return pos;
    }

    public int getCount() {
        return count;
    }

    public static byte[] getBuffer(ByteArrayInputStream is) throws ReflectiveOperationException {
        return getField(is, "buf");
    }

    public static int getPosition(ByteArrayInputStream is) throws ReflectiveOperationException {
        return getField(is, "pos");
    }

    public static int getCount(ByteArrayInputStream is) throws ReflectiveOperationException {
        return getField(is, "count");
    }

    protected static <T> T getField(ByteArrayInputStream is, String name) throws ReflectiveOperationException {
        Field fl = ByteArrayInputStream.class.getDeclaredField(name);
        fl.setAccessible(true);
        return (T) fl.get(is);
    }

}
