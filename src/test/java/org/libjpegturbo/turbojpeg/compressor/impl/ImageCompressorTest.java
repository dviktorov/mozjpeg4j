package org.libjpegturbo.turbojpeg.compressor.impl;

import org.junit.Before;
import org.junit.Test;
import org.libjpegturbo.turbojpeg.compressor.api.ImageCompressor;
import org.libjpegturbo.turbojpeg.compressor.api.ImageProcessException;
import org.libjpegturbo.turbojpeg.compressor.api.ImageProcessInfo;
import org.libjpegturbo.turbojpeg.compressor.api.ImageProcessParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 * Tests for the compression utilities.
 *
 * @since version 1.0,	01/05/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageCompressorTest {

    private final static Logger log = LoggerFactory.getLogger(ImageCompressorTest.class);

    private File inImage = null;

    @Before
    public void doBefore() throws IOException, URISyntaxException {
        URL imageUrl = ImageCompressorTest.class.getResource("/images/i10_620p_sport.jpg");
        inImage = new File(imageUrl.toURI());
    }

    @Test
    public void compressorImplTest() throws IOException, URISyntaxException, ImageProcessException {
        compressionTest(new ImageCompressorImpl(), inImage);
    }

    @Test
    public void compressorDelegateTest() throws IOException, URISyntaxException, ImageProcessException {
        compressionTest(new ImageCompressorReflectionDelegate(), inImage);
    }

    public static void compressionTest(ImageCompressor imageCompressor, File inImage) throws IOException, URISyntaxException, ImageProcessException {

        log.info("Testing compressor implementation: {}", imageCompressor.getClass());

        assertTrue("Compressor is not detected as usable", imageCompressor.isUsable());

        assertNotNull("Input image couldn't be found", inImage);

        File outImage = File.createTempFile("out", ".processed.jpg");

        long start = System.currentTimeMillis();

        Map<String, Object> procParams = ImageProcessParameters.fromEmptyMap().setQuality(80).toMap();
        assertNotNull("Process parameters can't be null", procParams);

        Map<String, Object> processResult = imageCompressor.compressImage(inImage, outImage, procParams);
        assertNotNull("Process result is never expected to be null", processResult);
        ImageProcessInfo processInfo = ImageProcessInfo.fromMap(processResult);

        log.info("Total time: {} msec", System.currentTimeMillis() - start);
        log.info("Output file size: {} bytes", outImage.length());

        assertTrue("Size of output image is 0 bytes", outImage.length() > 0);
        assertTrue("Image size can't be bigger than the image buffer", processInfo.getOutputImage().length >= processInfo.getOutputImageSize());
        assertEquals("Image file size is not equal to the byte array size", processInfo.getOutputImageSize(), outImage.length());

        outImage.delete();

    }

}
