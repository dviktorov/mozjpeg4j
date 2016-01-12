package org.libjpegturbo.turbojpeg.utils;

/**
 *
 * Information related to a processed image.
 *
 * @since version 1.0,	01/07/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageProcessInfo {

    private int inputWidth = 0;
    private int inputHeight = 0;

    private int outputWidth = 0;
    private int outputHeight = 0;

    private byte[] outputImage = null;

    public int getInputWidth() {
        return inputWidth;
    }

    public void setInputWidth(int inputWidth) {
        this.inputWidth = inputWidth;
    }

    public int getInputHeight() {
        return inputHeight;
    }

    public void setInputHeight(int inputHeight) {
        this.inputHeight = inputHeight;
    }

    public int getOutputWidth() {
        return outputWidth;
    }

    public void setOutputWidth(int outputWidth) {
        this.outputWidth = outputWidth;
    }

    public int getOutputHeight() {
        return outputHeight;
    }

    public void setOutputHeight(int outputHeight) {
        this.outputHeight = outputHeight;
    }

    public String getInputDimension() {
        return getInputWidth() + "x" + getInputHeight();
    }

    public String getOutputDimension() {
        return getOutputWidth() + "x" + getOutputHeight();
    }

    public byte[] getOutputImage() {
        return outputImage;
    }

    public void setOutputImage(byte[] outputImage) {
        this.outputImage = outputImage;
    }

}
