package org.libjpegturbo.turbojpeg.processor.utils;

import org.libjpegturbo.turbojpeg.processor.api.ImageProcessException;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessInfo;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Utilities which add additional methods for image processing.
 *
 * @since version 1.0,	01/22/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageProcessorUtils {

    protected final static int BUFF_SIZE = 8192;

    public static Map<String, Object> compressImage(ImageProcessor processor, File inFile, File outFile, int quality, int numerator, int denominator, int subsampling, int flags) throws ImageProcessException {

        try (FileInputStream fis = new FileInputStream(inFile)) {

            byte[] image = inputStreamToByteArray(fis);

            Map<String, Object> decompData = compressImage(processor, image, quality, numerator, denominator, subsampling, flags);
            ImageProcessInfo info = ImageProcessInfo.fromMap(new HashMap<String, Object>(decompData));

            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                fos.write(info.getOutputImage(), 0, info.getOutputImageSize());
                return info.toMap();
            }

        } catch (Exception e) {
            throw new ImageProcessException(e);
        }

    }

    public static Map<String, Object> compressImage(ImageProcessor processor, byte[] image, int quality, int numerator, int denominator, int subsampling, int flags) throws ImageProcessException {

        try {

            Map<String, Object> decompData = processor.decompressJpegImage(image, numerator, denominator, flags);
            ImageProcessInfo info = ImageProcessInfo.fromMap(new HashMap<String, Object>(decompData));

            ByteArrayInputStream imageStream = processor.compressJpegImage(
                    info.getOutputImage(), info.getOutputWidth(), info.getOutputHeight(),
                    quality, subsampling, flags);

            byte[] imageBuf = ShallowByteArrayInputStream.getBuffer(imageStream);
            //int imagePos = ShallowByteArrayInputStream.getPosition(imageStream);
            int imageSize = ShallowByteArrayInputStream.getCount(imageStream);

            info.setOutputImage(imageBuf);
            info.setOutputImageSize(imageSize);

            return info.toMap();

        } catch (Exception e) {
            throw new ImageProcessException(e);
        }

    }

    public static Map<String, Object> compressImage(ImageProcessor processor, File inFile, File outFile, int quality) throws ImageProcessException {
        return compressImage(processor, inFile, outFile, quality, 1, 1, 0, 0);
    }

    public static byte[] inputStreamToByteArray(InputStream inStream) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        byte[] data = new byte[BUFF_SIZE];
        int nRead;

        try (InputStream stream = inStream) {
            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }

    }

}
