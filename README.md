# io-functions

The `io-functions` library provides functional interfaces for I/O operations. These are basically copies of the functional interfaces in [java.util.functions](http://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html) except their methods can throw [IOExceptions](http://docs.oracle.com/javase/8/docs/api/java/io/IOException.html).

Each of these interfaces also contains static methods `unchecked` and `checked` to convert them into their matching JSE equivalents. For example, to delete all files in a directory that match a filter, you can use [IOConsumer.unchecked](https://robtimus.github.io/io-functions/apidocs/com/github/robtimus/io/function/IOConsumer.html#unchecked-com.github.robtimus.io.function.IOConsumer-):

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
        stream.forEach(unchecked(Files::delete));
    } catch (UncheckedIOException e) {
        throw e.getCause();
    }
