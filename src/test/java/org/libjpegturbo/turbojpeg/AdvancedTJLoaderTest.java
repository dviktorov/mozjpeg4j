package org.libjpegturbo.turbojpeg;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 *
 * Tests loading native library.
 *
 * @since version 1.0,	01/04/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class AdvancedTJLoaderTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    static {
        System.setProperty(AdvancedTJLoader.KEY_LIB_RENEWED, "true");
        AdvancedTJLoader.load();
    }

    @Test
    public void loadNativeLibraryLTest() {

        log.info("Native library loaded path: {}", AdvancedTJLoader.getLoadedLibraryPath());
        log.info("Native library extraction path: {}", AdvancedTJLoader.getExtractedLibraryPath());
        log.info("Native library loaded: {}", AdvancedTJLoader.isLibraryLoaded());
        log.info("Native library renewed: {}", AdvancedTJLoader.isInternalLibraryRenewed());
        log.info("Native SIMD enabled: {}", !AdvancedTJLoader.isInternalSimdDisabled());

        assertTrue("Native library couldn't be loaded", AdvancedTJLoader.isLibraryLoaded());
        assertTrue("Native library is not loaded from internal resource", AdvancedTJLoader.isLibraryLoadedFromInternalResource());
        assertEquals("Native library version is wrong", "3.1", AdvancedTJLoader.getInternalLibVersion());
        assertTrue("Native library is not renewed", AdvancedTJLoader.isInternalLibraryRenewed());

        // Check SIMD based on platform
        if (OSInfo.getJVMArch() == OSInfo.OSArch.x86_64) {
            assertFalse("Internal SIMD is not enabled by default", AdvancedTJLoader.isInternalSimdDisabled());
        } else {
            assertTrue("Internal SIMD is not enabled by default", AdvancedTJLoader.isInternalSimdDisabled());
        }

    }

}
