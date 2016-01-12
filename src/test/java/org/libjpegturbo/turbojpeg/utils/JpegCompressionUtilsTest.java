package org.libjpegturbo.turbojpeg.utils;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

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
public class JpegCompressionUtilsTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private File inImage = null;

    @Before
    public void doBefore() throws IOException, URISyntaxException {
        URL imageUrl = JpegCompressionUtilsTest.class.getResource("/images/i10_620p_sport.jpg");
        inImage = new File(imageUrl.toURI());
    }

    @Test
    public void compressionBasicTest() throws IOException, URISyntaxException, ImageProcessException {

        assertNotNull("Input image couldn't be found", inImage);

        File outImage = File.createTempFile("out", ".processed.jpg");

        long start = System.currentTimeMillis();
        ImageProcessInfo processInfo = JpegCompressionUtils.compressJpeg(inImage, outImage, 80);

        log.info("Total time: {} msec", System.currentTimeMillis() - start);
        log.info("Output file size: {} bytes", outImage.length());

        assertTrue("Size of output image is 0 bytes", outImage.length() > 0);
        assertEquals("Image file size is not equal to the byte array size", processInfo.getOutputImage().length, outImage.length());

        outImage.delete();

    }

}
