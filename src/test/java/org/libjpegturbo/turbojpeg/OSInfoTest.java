package org.libjpegturbo.turbojpeg;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests System information.
 *
 * @since version 1.0,	01/04/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class OSInfoTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void osNameTest() {

        OSInfo.OSName osName = OSInfo.getOSName();
        assertNotEquals(OSInfo.OSName.unknown, osName);
        log.info("OS Name: {}", osName);

    }

    @Test
    public void jvmArchTest() {

        OSInfo.OSArch osArch = OSInfo.getJVMArch();
        assertNotEquals(OSInfo.OSArch.unknown, osArch);
        log.info("JVM Arch: {}", osArch);

    }

    @Test
    public void osTempDirTest() {

        String result = OSInfo.getTempDirectory();
        assertNotNull(result);
        log.info("OS Temp Directory: {}", result);

    }

    @Test
    public void osUserHomeDirTest() {

        String result = OSInfo.getUserHomeDirectory();
        assertNotNull(result);
        log.info("User Home Directory: {}", result);

    }

    @Test
    public void osExecutableTempDirectoryTest() {
        String result = OSInfo.getExecutableTempDirectory();
        assertNotNull(result);
        log.info("Executable Temp Directory: {}", result);
    }

}
