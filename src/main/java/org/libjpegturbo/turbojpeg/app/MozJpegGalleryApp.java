package org.libjpegturbo.turbojpeg.app;

import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessException;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessInfo;
import org.libjpegturbo.turbojpeg.processor.api.ImageProcessor;
import org.libjpegturbo.turbojpeg.processor.impl.ImageProcessorImpl;
import org.libjpegturbo.turbojpeg.processor.utils.ImageProcessorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Application which compresses the images and generates an html report
 *
 * @since version 1.0,	01/06/2016
 *
 * @author Dmitry Viktorov
 *
 */
public class MozJpegGalleryApp {

    protected final static int[] QUALITIES = new int[] { 100, 95, 90, 85, 80, 70, 60 };

    protected final static String VERSION = TJ.getDefaultVersion();

    private final static Logger log = LoggerFactory.getLogger(MozJpegGalleryApp.class);

    private final static ImageProcessor processor = new ImageProcessorImpl();

    public static void main(String[] args) throws IOException, ImageProcessException {

        if (args.length < 1 || args[0].isEmpty()) {
            throw new IllegalArgumentException("Output directory for the report is not specified");
        }

        // Init out directory and html
        File outDir = initOutDirectory(args[0]);
        FileWriter writer = new FileWriter(outDir.getPath() + File.separator + "index.html", true); //true tells to append data.
        BufferedWriter html = new BufferedWriter(writer);
        html.write("<html><body><h1>MozJpeg Compression Examples (Version " + VERSION + ")</h1><hr>\r\n");

        File[] images = getImagesFromResources();

        for (File image: images) {

            Path inImage = Paths.get(outDir.getPath() + File.separator + image.getName());
            Files.copy(Paths.get(image.getPath()), inImage);
            long inImageSize = inImage.toFile().length();

            for (int i = 0; i < QUALITIES.length; i++) {

                int q = QUALITIES[i];

                String outImageName = "proc_" + q + "_" + image.getName();
                Path outImage = Paths.get(outDir.getPath() + File.separator + outImageName);

                log.info("Processing image with quality={}: {}", q, inImage.getFileName());

                long startTime = System.currentTimeMillis();
                ImageProcessInfo processInfo = ImageProcessInfo.fromMap(ImageProcessorUtils.compressImage(processor, inImage.toFile(), outImage.toFile(), q));
                long totalTime = System.currentTimeMillis() - startTime;

                long outImageSize = outImage.toFile().length();

                log.info("Total time: {} msec", totalTime);

                html.write("<h2>" + inImage.getFileName() + "</h2>\r\n");
                html.write("<h3>Quality: " + q + "<br>\r\n");

                html.write("In/Out Dimension: " + processInfo.getInputDimension() + " / " + processInfo.getOutputDimension() + "<br>");
                html.write("In/Out Size: " + toReadableByteCount(inImageSize, false) +
                    " / " + toReadableByteCount(outImageSize, false) + " => " +
                    getPercent(inImageSize, outImageSize) + "%<br>\r\n");

                html.write("Conversion time: " + totalTime + " msec</h3><br>\n");
                html.write("<img src='" + image.getName() + "'>&nbsp&nbsp&nbsp<img src='" + outImageName + "'><br>\r\n");
            }

            html.write("<br><hr>\r\n");

        }

        html.write("</body></html>");
        html.close();

    }

    public static File initOutDirectory(String outPath) throws IOException {

        outPath += File.separator + "jpeg_compression";
        File outDir = new File(outPath);
        if (outDir.exists()) {
            deleteDirectory(outDir);
        }
        outDir.mkdirs();
        return outDir;

    }

    public static File[] getImagesFromResources() throws IOException {

        // Get the images folder from resources
        URL resImages = MozJpegGalleryApp.class.getResource("/images");
        File resFolder = null;
        try { resFolder = new File(resImages.toURI()); } catch (Exception e) {}

        // If folder is resolved, return the files
        if (resFolder != null && resFolder.isDirectory()) {
            return resFolder.listFiles();
        }

        return new File[0];

    }

    public static boolean deleteDirectory(File directory) {

        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return directory.delete();

    }

    public static String toReadableByteCount(long bytes, boolean si) {

        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);

    }

    public static int getPercent(long in, long out) {
        return (int) (((double) out) / in * 100);
    }

}
