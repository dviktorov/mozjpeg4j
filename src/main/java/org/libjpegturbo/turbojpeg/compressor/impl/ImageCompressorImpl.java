package org.libjpegturbo.turbojpeg.compressor.impl;

import org.libjpegturbo.turbojpeg.*;
import org.libjpegturbo.turbojpeg.compressor.api.ImageCompressor;
import org.libjpegturbo.turbojpeg.compressor.api.ImageProcessException;
import org.libjpegturbo.turbojpeg.compressor.api.ImageProcessInfo;
import org.libjpegturbo.turbojpeg.compressor.api.ImageProcessParameters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

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

    // Retrieve scaling factors
    static {
        try { sf = TJ.getScalingFactors(); } catch (Exception e) {}
    }

    @Override
    public boolean isUsable() {
        return TJ.isUsable();
    }

    /**
     * Compresses the submitted JPEG file.
     *
     * @param inputImage - byte array of a JPEG file is required
     * @return ImageProcessInfo with embedded compressed JPEG image as byte array
     * @throws ImageProcessException
     */
    protected static Map<String, Object> compressImage(byte[] inputImage, TJTransform transform, Map<String, Object> processParameters) throws ImageProcessException {

        // Don't process image if TJ is not usable
        if (!TJ.isUsable()) {
            throw new ImageProcessException("Native library can't be used at the current platform");
        }

        if (inputImage == null) {
            throw new IllegalArgumentException("Input image can't be null");
        }

        ImageProcessInfo result = ImageProcessInfo.fromMap(new HashMap<String, Object>());
        ImageProcessParameters params = ImageProcessParameters.fromMap(processParameters);
        int quality = params.getQuality();
        int outSubsamp = params.getSubsampling();
        int flags = params.getFlags();
        int num = params.getNumerator();
        int denom = params.getDenominator();

        TJScalingFactor scaleFactor = new TJScalingFactor(num, denom);

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
            result.setOutputImage(compressor.compress(flags));
            result.setOutputImageSize(compressor.getCompressedSize());

            // Close and nullify compressor
            compressor.close();
            compressor = null;

        } catch (Exception e) {
            throw new ImageProcessException(e);
        }

        return result.toMap();

    }

    @Override
    public Map<String, Object> compressImage(byte[] inputImage, Map<String, Object> processParameters) throws ImageProcessException {
        return compressImage(inputImage, new TJTransform(), processParameters);
    }

    @Override
    public Map<String, Object> compressImage(File inFile, File outFile, Map<String, Object> processParameters) throws ImageProcessException {

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

            Map<String, Object> processMap = compressImage(inputImage, processParameters);
            ImageProcessInfo processInfo = ImageProcessInfo.fromMap(processMap);

            FileOutputStream fos = new FileOutputStream(outFile);
            fos.write(processInfo.getOutputImage(), 0, processInfo.getOutputImageSize());
            fos.close();

            return processMap;

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

    protected static TJCompressor createCompressor(BufferedImage image, int quality, int subsamp) throws Exception {
        TJCompressor compressor = new TJCompressor();
        compressor.setJPEGQuality(quality);
        compressor.setSubsamp(subsamp);
        compressor.setSourceImage(image, 0, 0, 0, 0);
        //compressor.setSourceImage(bmpBuf, 0, 0, result.getOutputWidth(), 0, result.getOutputHeight(), TJ.PF_BGRX);
        return compressor;
    }

}
