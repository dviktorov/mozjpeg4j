package org.libjpegturbo.turbojpeg.compressor.api;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 *
 * Tests for the compression utilities.
 *
 * @since version 1.0,	01/15/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageProcessParametersTest {

    @Test
    public void processInfoTest() throws IOException, URISyntaxException, ImageProcessException {

        ImageProcessParameters info = ImageProcessParameters.fromEmptyMap();

        assertEquals(100, info.getQuality());
        info.setQuality(11);
        assertEquals(11, info.getQuality());

        assertEquals(0, info.getFlags());
        info.setFlags(202);
        assertEquals(202, info.getFlags());

        assertEquals(-1, info.getSubsampling());
        info.setSubsampling(202);
        assertEquals(202, info.getSubsampling());

        assertEquals(1, info.getNumerator());
        info.setNumerator(202);
        assertEquals(202, info.getNumerator());

        assertEquals(1, info.getDenominator());
        info.setDenominator(202);
        assertEquals(202, info.getDenominator());

    }

}
