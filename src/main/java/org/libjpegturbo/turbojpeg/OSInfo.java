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

/**
 *
 * System information utilities
 *
 * @since version 1.0,	01/05/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class OSInfo {

    public static OSName getOSName() {

        String val = System.getProperty("os.name");
        if (val != null) {
            val = val.toLowerCase();
            if (val.contains("linux")) {
                return OSName.linux;
            } else if (val.contains("windows")) {
                return OSName.windows;
            } else if (val.contains("mac")) {
                return OSName.mac;
            }
        }

        return OSName.unknown;

    }

    public static OSArch getJVMArch() {
        String val = System.getProperty("os.arch");
        if (val != null) {
            val = val.toLowerCase();
            if (val.contains("86_64") || val.contains("amd64")) {
                return OSArch.x86_64;
            } else if (val.contains("86")) {
                return OSArch.x86;
            }
        }
        return OSArch.unknown;
    }

    public static String getTempDirectory() {
        String val = System.getProperty("java.io.tmpdir");
        val = val != null ? val : "";
        if (val.endsWith(File.separator)) {
            val = val.substring(0, val.length() - 1);
        }
        return val;
    }

    public enum OSName {

        unknown(""),
        windows(".dll"),
        linux(".so"),
        mac(".dylib");

        OSName(String inExtension) {
            this.extension = inExtension;
        }

        private String extension;

        public String getLibExtension() {
            return extension;
        }

    }

    public enum OSArch {
        unknown,
        x86_64,
        x86
    }

}
