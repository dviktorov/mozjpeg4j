# MozJpeg4J
This project is based on [Mozilla JPEG Encoder (MozJpeg)](https://github.com/mozilla/mozjpeg). The original project 
is written in C and provides a Java wrapper with the expectation to find a compiled library in the system. 
MozJpeg4J simplifies its use in Java by including a set of pre-compiled libraries for Windows, Linux, and Mac. 
It also extends the Java wrapper by loading the native OS library and providing utilities for image compression.
Currently default version 3.1 of MozJpeg is embedded into the project.

To build the library or run demo applications, you will need to install [Git](https://git-scm.com/) and 
[Gradle](http://gradle.org) in your system. Once frameworks are installed, run the following command to clone 
the project:
    
    git clone https://github.com/dviktorov/mozjpeg4j.git


# Building Java library
To build Java library, run the following command from the cloned project folder:

    gradle clean test jar

After the process is finished, the library jar file can be found at ./build/libs of the project


# Building OSGi Java library
To build OSGi Java library, run the following command from the cloned project folder:

    gradle clean test jarOSGI

After the process is finished, the OSGi library jar file can be found at ./build/libs of the project


# Running compression demo gallery
To evaluate the quality of the library you may run the command below. It will compress a set of images and generate
an HTML report at ./build/reports/jpeg_compression. You may open index.html in a browser to view the gallery.

    gradle clean test runJpegCompressionDemoApp


# Running MozJpeg command line utility
To run the utility that is presented at [Mozilla JPEG Encoder (MozJpeg)](https://github.com/mozilla/mozjpeg) it you may
run the command below. You may refer to the printed "usage" message to understand how to use the utility and the 
options it recognizes.

    gradle clean test runMozJpeg


