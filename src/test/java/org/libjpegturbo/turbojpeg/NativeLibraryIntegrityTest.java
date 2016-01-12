package org.libjpegturbo.turbojpeg;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * Tests integrity of native libraries.
 *
 * @since version 1.0,	01/11/2016
 *
 * @author Dmitry Viktorov
 *
 */
@RunWith(Parameterized.class)
public class NativeLibraryIntegrityTest {

    private final static Logger log = LoggerFactory.getLogger(NativeLibraryIntegrityTest.class);

    @Parameterized.Parameters(name = "{index}: md5({0})={1}")
    public static Iterable<String[]> data() {
        Map<String, String> libs = AdvancedTJLoader.getLibrariesData();
        String[][] data = new String[libs.size()][2];
        int index = 0;
        for (Map.Entry<String, String> mapEntry : libs.entrySet()) {
            String[] vals = data[index++];
            vals[0] = mapEntry.getKey();
            vals[1] = mapEntry.getValue();
        }
        // Check 8 libraries are presented
        assertEquals(8, data.length);
        return Arrays.asList(data);
    }

    protected String libPath;
    protected String libMD5;

    public NativeLibraryIntegrityTest(String inPath, String inMD5) {
        this.libPath = inPath;
        this.libMD5 = inMD5;
    }

    @Test
    public void nativeLibIntegrityTest() throws NoSuchAlgorithmException, IOException {

        InputStream is = AdvancedTJLoaderTest.class.getResourceAsStream(libPath);

        assertNotNull("Native library is not found: " + libPath, is);

        String calculatedMD5 = DigestUtils.getInputStreamMD5(is);

        assertNotNull("Couldn't generate MD5", calculatedMD5);
        assertEquals("MD5 doesn't match", libMD5, calculatedMD5);

        log.info("MD5: {}", calculatedMD5);

    }

}
