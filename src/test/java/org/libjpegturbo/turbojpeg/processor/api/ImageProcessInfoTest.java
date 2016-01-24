package org.libjpegturbo.turbojpeg.processor.api;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 * Tests for the compression utilities.
 *
 * @since version 1.0,	01/15/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageProcessInfoTest {

    @Test
    public void processInfoTest() throws IOException, URISyntaxException, ImageProcessException {

        ImageProcessInfo info = ImageProcessInfo.fromMap(new HashMap<String, Object>());

        assertNull(info.getOutputImage());
        info.setOutputImage(new byte[100]);
        assertEquals(100, info.getOutputImage().length);

        assertEquals(0, info.getOutputImageSize());
        info.setOutputImageSize(200);
        assertEquals(200, info.getOutputImageSize());

        assertEquals(0, info.getOutputHeight());
        info.setOutputHeight(202);
        assertEquals(202, info.getOutputHeight());

        assertEquals(0, info.getOutputWidth());
        info.setOutputWidth(404);
        assertEquals(404, info.getOutputWidth());

        assertEquals(0, info.getInputWidth());
        info.setInputWidth(505);
        assertEquals(505, info.getInputWidth());

        assertEquals(0, info.getInputHeight());
        info.setInputHeight(606);
        assertEquals(606, info.getInputHeight());

    }

}
