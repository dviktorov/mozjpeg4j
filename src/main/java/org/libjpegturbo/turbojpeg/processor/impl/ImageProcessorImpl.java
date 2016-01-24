package org.libjpegturbo.turbojpeg.processor.impl;

import org.libjpegturbo.turbojpeg.*;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessException;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessInfo;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 *
 * Implementation of JPEG compression algorithms.
 *
 * @since version 1.0,	01/06/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageProcessorImpl implements ImageProcessor {

    static TJScalingFactor[] sf = null;

    // Retrieve scaling factors
    static {
        try { sf = TJ.getScalingFactors(); } catch (Exception e) {}
    }

    @Override
    public boolean isUsable() {
        return TJ.isUsable();
    }

    @Override
    public BufferedImage decompressGeneralImage(byte[] inImage, int numerator, int denominator, int flags) throws ImageProcessException {

        checkUsable();
        checkInputImage(inImage);

        // Create decompressor and retrieve attributes
        try (TJDecompressor decompressor = createDecompressor(inImage, new TJTransform())) {

            TJScalingFactor scaleFactor = new TJScalingFactor(numerator, denominator);
            int width = scaleFactor.getScaled(decompressor.getWidth());
            int height = scaleFactor.getScaled(decompressor.getHeight());

            BufferedImage decompImage = decompressor.decompress(width, height, BufferedImage.TYPE_INT_RGB, flags);

            return decompImage;

        } catch (Exception e) {
            throw new ImageProcessException(e);
        }

    }

    @Override
    public Map<String, Object> decompressJpegImage(byte[] inImage, int numerator, int denominator, int flags) throws ImageProcessException {

        checkUsable();
        checkInputImage(inImage);

        // Create decompressor and retrieve attributes
        try (TJDecompressor decompressor = createDecompressor(inImage, new TJTransform())) {

            TJScalingFactor scaleFactor = new TJScalingFactor(numerator, denominator);
            int width = scaleFactor.getScaled(decompressor.getWidth());
            int height = scaleFactor.getScaled(decompressor.getHeight());

            byte[] bmpBuffer = decompressor.decompress(width, 0, height, TJ.PF_BGRX, flags);

            ImageProcessInfo info = ImageProcessInfo.newInstance().
                    setInputWidth(width).setInputHeight(height).
                    setOutputWidth(width).setOutputHeight(height).
                    setOutputImage(bmpBuffer).
                    setOutputImageSize(decompressor.getJPEGSize());

            return info.toMap();

        } catch (Exception e) {
            throw new ImageProcessException(e);
        }

    }

    @Override
    public ByteArrayInputStream compressGeneralImage(BufferedImage inImage, int quality, int subsampling, int flags) throws ImageProcessException {
        return compressImage(inImage, 0, 0, quality, subsampling, flags);
    }

    @Override
    public ByteArrayInputStream compressJpegImage(byte[] inImage, int width, int height, int quality, int subsampling, int flags) throws ImageProcessException {
        return compressImage(inImage, width, height, quality, subsampling, flags);
    }

    protected static ByteArrayInputStream compressImage(Object inImage, int width, int height, int quality, int subsampling, int flags) throws ImageProcessException {

        checkUsable();
        checkInputImage(inImage);

        try (TJCompressor compressor = createCompressor(quality, subsampling)) {

            if (inImage instanceof BufferedImage) {
                compressor.setSourceImage((BufferedImage) inImage, 0, 0, 0, 0);
            } else {
                compressor.setSourceImage((byte[]) inImage, 0, 0, width, 0, height, TJ.PF_BGRX);
            }

            byte[] data = compressor.compress(flags);
            int size = compressor.getCompressedSize();

            return new ByteArrayInputStream(data, 0, size);

        } catch (Exception e) {
            throw new ImageProcessException(e);
        }

    }

    protected static TJDecompressor createDecompressor(byte[] image, TJTransform transform) throws Exception {

        TJDecompressor decompressor = null;

        // If transformation is needed
        if (transform.op != TJTransform.OP_NONE || transform.options != 0 || transform.cf != null) {

            TJTransform[] transforms = new TJTransform[1];
            transforms[0] = transform;
            transforms[0].options |= TJTransform.OPT_TRIM;

            TJTransformer transformer = new TJTransformer(image);
            TJDecompressor[] decompressors = transformer.transform(transforms, 0);
            decompressor = decompressors[0];

        } else {
            decompressor = new TJDecompressor(image);
        }

        return decompressor;

    }

    protected static TJCompressor createCompressor(int quality, int subsamp) throws Exception {
        TJCompressor compressor = new TJCompressor();
        compressor.setJPEGQuality(quality);
        compressor.setSubsamp(subsamp);
        return compressor;
    }

    protected static void checkUsable() throws ImageProcessException {
        if (!TJ.isUsable()) {
            throw new ImageProcessException("Native library can't be used at the current platform");
        }
    }

    protected static void checkInputImage(Object inImage) throws ImageProcessException {
        if (inImage == null) {
            throw new ImageProcessException("Input image can't be null");
        }
    }

}
