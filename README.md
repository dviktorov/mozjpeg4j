# MozJpeg4J
This project is based on Mozilla JPEG Encoder (MozJpeg). The original project is written in C and provides a Java 
wrapper with the expectation to find a compiled library in the system. MozJpeg4J simplifies its use in Java by including
a set of pre-compiled libraries for Windows, Linux, and Mac. It also extends the Java wrapper by loading the native 
OS library and providing utilities for image compression.

To build the library or run demo applications, you will need to install [Git](https://git-scm.com/) and [Gradle](http://gradle.org) in your system. 
Once frameworks are installed, run the following command to clone the project:
    
    **git clone https://github.com/dviktorov/mozjpeg4j.git**


# Building Java library
To build Java library, run the following command from the cloned project folder:
    **gradle clean test jar**
    
After the process is finished you may find the library jar file at ./build/libs of the project



