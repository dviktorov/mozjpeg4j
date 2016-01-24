package org.libjpegturbo.turbojpeg.processor.impl;

import org.junit.Before;
import org.junit.Test;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessException;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessInfo;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessor;
import org.libjpegturbo.turbojpeg.processor.utils.ImageProcessorUtils;
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
public class ImageProcessorTest {

    private final static Logger log = LoggerFactory.getLogger(ImageProcessorTest.class);

    private File inImage = null;

    @Before
    public void doBefore() throws IOException, URISyntaxException {
        URL imageUrl = ImageProcessorTest.class.getResource("/images/i10_620p_sport.jpg");
        inImage = new File(imageUrl.toURI());
    }

    @Test
    public void compressorImplTest() throws IOException, URISyntaxException, ImageProcessException {
        compressionTest(new ImageProcessorImpl(), inImage);
    }

    @Test
    public void compressorDelegateTest() throws IOException, URISyntaxException, ImageProcessException {
        compressionTest(new ImageProcessorReflectionDelegate(), inImage);
    }

    public static void compressionTest(ImageProcessor processor, File inImage) throws IOException, URISyntaxException, ImageProcessException {

        log.info("Testing compressor implementation: {}", processor.getClass());

        assertTrue("Compressor is not detected as usable", processor.isUsable());

        assertNotNull("Input image couldn't be found", inImage);

        File outImage = File.createTempFile("out", ".processed.jpg");

        long start = System.currentTimeMillis();

        Map<String, Object> processResult = ImageProcessorUtils.compressImage(processor, inImage, outImage, 80);
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
