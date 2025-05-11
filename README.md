# io-functions
[![Maven Central](https://img.shields.io/maven-central/v/com.github.robtimus/io-functions)](https://search.maven.org/artifact/com.github.robtimus/io-functions)
[![Build Status](https://github.com/robtimus/io-functions/actions/workflows/build.yml/badge.svg)](https://github.com/robtimus/io-functions/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Aio-functions&metric=alert_status)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Aio-functions)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Aio-functions&metric=coverage)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Aio-functions)
[![Known Vulnerabilities](https://snyk.io/test/github/robtimus/io-functions/badge.svg)](https://snyk.io/test/github/robtimus/io-functions)

The `io-functions` library provides functional interfaces for I/O operations. These are basically copies of the functional interfaces in [java.util.functions](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html) except their methods can throw [IOExceptions](https://docs.oracle.com/javase/8/docs/api/java/io/IOException.html).

Each of these interfaces also contains static methods `unchecked` and `checked` to convert them into their matching JSE equivalents. For example, to delete all files in a directory that match a filter, you can use [IOConsumer.unchecked](https://robtimus.github.io/io-functions/apidocs/com/github/robtimus/io/function/IOConsumer.html#unchecked-com.github.robtimus.io.function.IOConsumer-):

```java
try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
    stream.forEach(unchecked(Files::delete));
} catch (UncheckedIOException e) {
    throw e.getCause();
}
```
