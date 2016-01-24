package org.libjpegturbo.turbojpeg.processor.api;

/**
 *
 * Exception which occurred while processing an image.
 *
 * @since version 1.0,	01/07/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageProcessException extends Exception {

    public ImageProcessException(String message) {
        super(message);
    }

    public ImageProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageProcessException(Throwable cause) {
        super(cause);
    }

}
