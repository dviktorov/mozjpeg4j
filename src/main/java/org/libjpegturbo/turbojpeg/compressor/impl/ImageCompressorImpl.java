package org.libjpegturbo.turbojpeg.compressor.impl;

import org.libjpegturbo.turbojpeg.*;
import org.libjpegturbo.turbojpeg.compressor.api.ImageCompressor;
import org.libjpegturbo.turbojpeg.compressor.api.ImageProcessException;
import org.libjpegturbo.turbojpeg.compressor.api.ImageProcessInfo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 *
 * Utilities for JPEG compression. They can be used over its interface hiding the actual implementation.
 *
 * @since version 1.0,	01/06/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class ImageCompressorImpl implements ImageCompressor {

    static TJScalingFactor[] sf = null;

    // Get scaling factors
    static {
        try { sf = TJ.getScalingFactors(); } catch (Exception e) {}
    }

    @Override
    public ImageProcessInfo compressJpeg(File inFile, File outFile, int quality) throws ImageProcessException {
        return compressJpeg(inFile, outFile, quality, -1, 0, 1, 1);
    }

    @Override
    public ImageProcessInfo compressJpeg(File inFile, File outFile, int quality, int outSubsamp, int flags, int scaleNum, int scaleDenom) throws ImageProcessException {

        // Don't process image if TJ is not usable
        if (!TJ.isUsable()) {
            throw new ImageProcessException("Native library can't be used at the current platform");
        }

        try {

            FileInputStream fis = new FileInputStream(inFile);
            int inputSize = fis.available();
            if (inputSize < 1) {
                throw new ImageProcessException("Input file contains no data");
            }

            byte[] inputImage = new byte[inputSize];
            fis.read(inputImage);
            fis.close();

            ImageProcessInfo processInfo = compressJpeg(inputImage, new TJTransform(), quality, outSubsamp, flags, scaleNum, scaleDenom);

            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] outImage = processInfo.getOutputImage();
            fos.write(outImage);
            fos.close();

            return processInfo;

        } catch (Exception e) {
            throw new ImageProcessException(e);
        }

    }

    /**
     * Compresses the submitted JPEG file.
     *
     * @param inputImage - byte array of a JPEG file is required
     * @param quality - Value from 1 to 100. Most usable values are from 5 to 95.
     * @param outSubsamp - Subsampling
     * @param flags - Flags for decompression / compression
     * @param scaleNum - Scaling numerator
     * @param scaleDenom - Scaling denominator
     * @return ImageProcessInfo with embedded compressed JPEG image as byte array
     * @throws ImageProcessException
     */
    @Override
    public ImageProcessInfo compressJpeg(byte[] inputImage, TJTransform transform, int quality, int outSubsamp, int flags, int scaleNum, int scaleDenom) throws ImageProcessException {

        ImageProcessInfo result = new ImageProcessInfo();

        // Don't process image if TJ is not usable
        if (!TJ.isUsable()) {
            throw new ImageProcessException("Native library can't be used at the current platform");
        }

        TJScalingFactor scaleFactor = new TJScalingFactor(scaleNum, scaleDenom);

        BufferedImage decompImage;

        // Create decompressor and retrieve attributes
        TJDecompressor decompressor = null;
        try {

            decompressor = createDecompressor(inputImage, transform);

            result.setInputWidth(decompressor.getWidth());
            result.setInputHeight(decompressor.getHeight());

            result.setOutputWidth(scaleFactor.getScaled(result.getInputWidth()));
            result.setOutputHeight(scaleFactor.getScaled(result.getInputHeight()));

            // Apply subsampling from decompressor if it's not explicitly provided
            if (outSubsamp < 0) {
                outSubsamp = decompressor.getSubsamp();
            }

            decompImage = decompressor.decompress(result.getOutputWidth(), result.getOutputHeight(), BufferedImage.TYPE_INT_RGB, flags);
            //byte[] bmpBuf = decompressor.decompress(result.getOutputWidth(), 0, result.getOutputHeight(), TJ.PF_BGRX, flags);

            // Close decompressor and nullify it
            decompressor.close();
            decompressor = null;

        } catch (Exception e) {
            throw new ImageProcessException(e);
        }

        try {

            TJCompressor compressor = createCompressor(decompImage, quality, outSubsamp);
            byte[] compImage = chopByteArray(compressor.compress(flags), compressor.getCompressedSize());

            // Close and nullify compressor
            compressor.close();
            compressor = null;
            result.setOutputImage(compImage);

        } catch (Exception e) {
            throw new ImageProcessException(e);
        }

        return result;

    }

    protected static byte[] chopByteArray(byte[] buffer, int size) throws Exception {
        if (buffer.length > size) {
            buffer = Arrays.copyOf(buffer, size);
        }
        return buffer;
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

    protected static TJCompressor createCompressor(BufferedImage image, int quality, int subsamp) throws Exception {
        TJCompressor compressor = new TJCompressor();
        compressor.setJPEGQuality(quality);
        compressor.setSubsamp(subsamp);
        compressor.setSourceImage(image, 0, 0, 0, 0);
        //compressor.setSourceImage(bmpBuf, 0, 0, result.getOutputWidth(), 0, result.getOutputHeight(), TJ.PF_BGRX);
        return compressor;
    }

    @Override
    public boolean isUsable() {
        return TJ.isUsable();
    }

}
