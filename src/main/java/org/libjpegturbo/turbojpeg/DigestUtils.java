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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * Digest Utilities.
 *
 * @since version 1.0,	01/11/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class DigestUtils {

    protected final static String MD5 = "MD5";

    /**
     * Generates MD5 for the provided InputStream or throws an exception.
     */
    public static String getInputStreamMD5(InputStream is) throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance(MD5);

        try (DigestInputStream dis = new DigestInputStream(is, md)) {

            // Read the stream
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }

            byte[] digest = md.digest();

            // This bytes[] has bytes in decimal format, convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i < digest.length ;i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();

        }

    }

    /**
     * Generates MD5 for the provided InputStream or returns null if it fails.
     */
    public static String getInputStreamMD5Quietly(InputStream is) {
        try {
            return getInputStreamMD5(is);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generates MD5 for the provided File or throws an exception.
     */
    public static String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        return getInputStreamMD5(new FileInputStream(file));
    }

    /**
     * Generates MD5 for the provided File or throws an exception.
     */
    public static String getFileMD5Quietly(File file) {
        try {
            return getInputStreamMD5(new FileInputStream(file));
        } catch (Exception e) {
            return null;
        }
    }

}
