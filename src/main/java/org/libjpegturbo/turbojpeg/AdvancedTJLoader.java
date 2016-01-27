/*
 * Copyright (C)2011-2013 D. R. Commander.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the libjpeg-turbo Project nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS",
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.libjpegturbo.turbojpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Logic responsible for loading the native library.
 *
 * @since version 1.0,	01/05/2016
 *
 * @author Josh Aas, D.R. Commander, Dmitry Viktorov
 *
 */
class AdvancedTJLoader {

    final static String LIB_VERSION_DEFAULT = "3.1";

    public final static String KEY_LIB_PATH = "mozjpeg.native.lib.path";
    public final static String KEY_LIB_NAME = "mozjpeg.native.lib.name";
    public final static String KEY_LIB_VERSION = "mozjpeg.native.lib.internal.version";
    public final static String KEY_LIB_SIMD_DISABLED = "mozjpeg.native.lib.internal.simd.disabled";
    public final static String KEY_LIB_RENEWED = "mozjpeg.native.lib.internal.renewed";

    protected final static String LIB_PATH_INTERNAL_PREFIX = "native_mozjpeg";
    protected final static String LIB_NAME = "libturbojpeg";
    protected final static String LIB_NAME_GENERIC = "turbojpeg";

    protected static volatile String loadedLibPath = null;
    protected static volatile String internalLibVersion = null;
    protected static volatile boolean internalLibRenewed = false;
    protected static volatile boolean internalSimdDisabled = false;

    private final static Logger log = LoggerFactory.getLogger(AdvancedTJLoader.class);

    /**
     * Map of internal libraries and their MD5 values.
     */
    protected static Map<String, String> getLibrariesData() {
        return new HashMap<String, String>() {{
            put("/native_mozjpeg/version_3_1/linux_simd_disabled_x86/libturbojpeg.so", "8c7207433b0440bd09617c316b4a0c5d");
            put("/native_mozjpeg/version_3_1/linux_simd_disabled_x86_64/libturbojpeg.so", "e235c8d1cc9bdeeda3d6aed73af8c21f");
            put("/native_mozjpeg/version_3_1/linux_simd_enabled_x86_64/libturbojpeg.so", "bd9d9366848e7e4a5431a95eb54c6302");
            put("/native_mozjpeg/version_3_1/mac_simd_enabled_x86_64/libturbojpeg.dylib", "96c972b96aa87034bf073423dd5b4341");
            put("/native_mozjpeg/version_3_1/mac_simd_disabled_x86_64/libturbojpeg.dylib", "83fe0a59e99c2fe9dba1695fd09a0cda");
            put("/native_mozjpeg/version_3_1/windows_simd_disabled_x86/libturbojpeg.dll", "b7110b82fbb93d5825e06d0e1ea95bd5");
            put("/native_mozjpeg/version_3_1/windows_simd_disabled_x86_64/libturbojpeg.dll", "48ab9dc574c2d4c6c27ac135deae15c1");
            put("/native_mozjpeg/version_3_1/windows_simd_enabled_x86_64/libturbojpeg.dll", "7bdd42d0e63f0bdd18922a374bfb0fc6");
        }};
    }

    /**
     * Quietly loads the native library. {@link #isLibraryLoaded()} should be called
     * to check if library successfully loaded.
     */
    protected static void load() {
        try {
            loadUnsafe();
        } catch (Throwable t) {
            log.error("Native library couldn't be loaded", t);
        }
    }

    /**
     * Loads the native library. If the library can't be loaded, throws exception.
     *
     * @throws IOException - If library can't be loaded
     */
    protected static void loadUnsafe() throws IOException {

        // Don't load the library if it's loaded
        if (isLibraryLoaded()) {
            return;
        }

        // Apply 'simd_disabled' if submitted, or disable it if it's a 32 bit platform
        internalSimdDisabled = Boolean.parseBoolean(System.getProperty(KEY_LIB_SIMD_DISABLED));
        if (OSInfo.getJVMArch() == OSInfo.OSArch.x86) {
            internalSimdDisabled = true;
        }

        // Apply the internal 'renewed' property which if true causes re-extraction of native library
        internalLibRenewed = Boolean.parseBoolean(System.getProperty(KEY_LIB_RENEWED));

        // Apply the internal version if submitted
        internalLibVersion = System.getProperty(KEY_LIB_VERSION, LIB_VERSION_DEFAULT);

        // Remove library if it should be renewed
        if (internalLibRenewed) {
            new File(getFullExtractedLibraryPath()).delete();
        }

        // Set native library name
        String libName = System.getProperty(KEY_LIB_NAME);
        if (libName == null || libName.isEmpty()) {
            libName = getLibraryInternalName();
        }

        // Set native library path
        String libPath = System.getProperty(KEY_LIB_PATH);
        if (libPath == null || libPath.isEmpty()) {
            libPath = getExtractedLibraryPath();
        }

        String fullLibPath = libPath + File.separator + libName;

        // If native library is not found, extract the internal library and reset the full path
        File libFile = new File(fullLibPath);
        if (!libFile.exists() || libFile.length() == 0) {
            log.debug("Extracting native mozjpeg library becuase it's not found at {}", fullLibPath);
            extractNativeLibraryWithRetrial();
            fullLibPath = getFullExtractedLibraryPath();
        }

        if (loadNativeLibrary(fullLibPath)) {
            loadedLibPath = fullLibPath;
        } else {
            loadedLibPath = loadNativeLibraryFromOS();
        }

        if (loadedLibPath == null) {
            throw new IOException("Native library could not be loaded: " + libName);
        }

    }

    protected static String loadNativeLibraryFromOS() {
        String result = null;
        try {
            System.loadLibrary(LIB_NAME_GENERIC);
            result = LIB_NAME_GENERIC;
        } catch (Throwable t) {
            if (OSInfo.getOSName() == OSInfo.OSName.mac) {
                try {
                    String optPath = "/opt/mozjpeg/lib/" + getLibraryInternalName();
                    System.load(optPath);
                    result = optPath;
                } catch (Throwable t2) {
                    String usrPath = "/usr/lib/" + getLibraryInternalName();
                    System.load(usrPath);
                    result = usrPath;
                }
            } else {
                String optPath = "/opt/mozjpeg/lib64/libturbojpeg.so";
                System.load(optPath);
                result = optPath;
            }
        }
        return result;
    }

    protected static boolean loadNativeLibrary(String fullLibPath) {
        boolean result = false;
        if (new File(fullLibPath).exists()) {
            try {
                System.load(fullLibPath);
                result = true;
            } catch (Throwable t) {
                log.error("Error loading native library", t);
            }
        }
        return result;
    }

    protected static void extractNativeLibraryWithRetrial() throws IOException {

        String internalLibPath = getLibraryFullInternalPath("/");
        String extractedLibPath = getFullExtractedLibraryPath();

        extractNativeLibrary(internalLibPath, extractedLibPath);

        // Verify MD5, and ignore exceptions since they don't confirm invalidity of MD5
        boolean md5Matches = true;
        try { md5Matches = verifyExtractedLibraryMD5(internalLibPath, extractedLibPath); } catch (Exception e) {}

        // If MD5 doesn't much, re-extract the library, and verify it again
        if (!md5Matches) {
            new File(extractedLibPath).delete();
            extractNativeLibrary(internalLibPath, extractedLibPath);
            try { md5Matches = verifyExtractedLibraryMD5(internalLibPath, extractedLibPath); } catch (Exception e) {}
        }

        if (!md5Matches) {
            throw new IOException("MD5 for internal and extracted libraries don't match");
        }

    }

    protected static void extractNativeLibrary(String internalLibPath, String extractedLibPath) throws IOException {

        File extractedLibFile = new File(extractedLibPath);
        File parentFile = extractedLibFile.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }

        InputStream reader = AdvancedTJLoader.class.getResourceAsStream(internalLibPath);
        if (reader == null) {
            throw new IOException("Internal library is not found: " + internalLibPath);
        }

        FileOutputStream writer = new FileOutputStream(extractedLibFile);
        byte[] buffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, bytesRead);
        }
        writer.close();
        reader.close();

    }

    /**
     * Compares expected MD5 with the MD5 of extracted library. If MD5 can't be calculated due to unsupported
     * algorithm, or inability to open extracted library then an exception is thrown. The exception mean MD5
     * can't be checked, so it should not be treated as wrong MD5. The MD5 is wrong only when it return true
     * as a result.
     */
    protected static boolean verifyExtractedLibraryMD5(String internalLibPath, String extractedLibPath) throws NoSuchAlgorithmException, IOException {
        // Verify MD5 if possible
        Map<String, String> libsData = getLibrariesData();
        String expectedMD5 = libsData.get(internalLibPath);
        if (expectedMD5 != null) {
            String libMD5 = DigestUtils.getFileMD5(new File(extractedLibPath));
            if (!expectedMD5.equalsIgnoreCase(libMD5)) {
                log.error("Expected MD5 of the library is " + expectedMD5 + " but found " + libMD5 + ". Library path: " + extractedLibPath);
                return false;
            }
        } else {
            log.error("The internal library checksum is not found. Please consider adding it to the code.");
        }
        return true;
    }

    protected static String getLibraryInternalVersionPath() {
        return "version_" + getInternalLibVersion().replaceAll("\\.", "_");
    }

    /**
     * Returns the internal name of the native library.
     */
    protected static String getLibraryInternalName() {
        return LIB_NAME + OSInfo.getOSName().getLibExtension();
    }

    /**
     * Returns the internal path of the native library.
     */
    protected static String getInternalLibraryPath(String separator) {
        String simdProp = internalSimdDisabled ? "simd_disabled" : "simd_enabled";
        OSInfo.OSName osName = OSInfo.getOSName();
        return separator + LIB_PATH_INTERNAL_PREFIX + separator +
            getLibraryInternalVersionPath() + separator + osName.toString() +
            "_" + simdProp + "_" + OSInfo.getJVMArch().toString();
    }

    protected static String getLibraryFullInternalPath(String separator) {
        return getInternalLibraryPath(separator) + separator + getLibraryInternalName();
    }

    protected static String getExtractedLibraryPath() {
        return OSInfo.getExecutableTempDirectory() + getInternalLibraryPath(File.separator);
    }

    protected static String getFullExtractedLibraryPath() {
        return OSInfo.getExecutableTempDirectory() + getLibraryFullInternalPath(File.separator);
    }

    public static String getInternalLibVersion() {
        return internalLibVersion;
    }

    public static boolean isInternalSimdDisabled() {
        return internalSimdDisabled;
    }

    public static String getLoadedLibraryPath() {
        return loadedLibPath;
    }

    public static boolean isLibraryLoaded() {
        return loadedLibPath != null;
    }

    public static boolean isLibraryLoadedFromInternalResource() {
        return (isLibraryLoaded() && loadedLibPath.equalsIgnoreCase(AdvancedTJLoader.getFullExtractedLibraryPath()));
    }

    public static boolean isInternalLibraryRenewed() {
        return internalLibRenewed;
    }

};
