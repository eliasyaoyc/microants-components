package yyc.open.framework.microants.components.kit.common.io;


import yyc.open.framework.microants.components.kit.common.validate.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utilities for common I/O methods. Borrowed heavily from Lucene (org.apache.lucene.util.IOUtils).
 */
public final class IOKit {

    /**
     * UTF-8 charset string.
     * <p>Where possible, use {@link StandardCharsets#UTF_8} instead,
     * as using the String constant may slow things down.
     *
     * @see StandardCharsets#UTF_8
     */
    public static final String UTF_8 = StandardCharsets.UTF_8.name();

    private IOKit() {
        // Static utils methods
    }

    /**
     * Closes all given {@link Closeable}s. Some of the {@linkplain Closeable}s may be null; they are
     * ignored. After everything is closed, the method either throws the first exception it hit
     * while closing with other exceptions added as suppressed, or completes normally if there were
     * no exceptions.
     *
     * @param objects objects to close
     */
    public static void close(final Closeable... objects) throws IOException {
        close(null, Arrays.asList(objects));
    }

    /**
     * @see #close(Closeable...)
     */
    public static void close(@NonNull Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }

    /**
     * Closes all given {@link Closeable}s. Some of the {@linkplain Closeable}s may be null; they are
     * ignored. After everything is closed, the method adds any exceptions as suppressed to the
     * original exception, or throws the first exception it hit if {@code Exception} is null. If
     * no exceptions are encountered and the passed in exception is null, it completes normally.
     *
     * @param objects objects to close
     */
    public static void close(final Exception e, final Closeable... objects) throws IOException {
        close(e, Arrays.asList(objects));
    }

    /**
     * Closes all given {@link Closeable}s. Some of the {@linkplain Closeable}s may be null; they are
     * ignored. After everything is closed, the method either throws the first exception it hit
     * while closing with other exceptions added as suppressed, or completes normally if there were
     * no exceptions.
     *
     * @param objects objects to close
     */
    public static void close(final Iterable<? extends Closeable> objects) throws IOException {
        close(null, objects);
    }

    /**
     * Closes all given {@link Closeable}s. If a non-null exception is passed in, or closing a
     * stream causes an exception, throws the exception with other {@link RuntimeException} or
     * {@link IOException} exceptions added as suppressed.
     *
     * @param ex      existing Exception to add exceptions occurring during close to
     * @param objects objects to close
     * @see #close(Closeable...)
     */
    public static void close(final Exception ex, final Iterable<? extends Closeable> objects) throws IOException {
        Exception firstException = ex;
        for (final Closeable object : objects) {
            try {
                close(object);
            } catch (final IOException | RuntimeException e) {
                if (firstException == null) {
                    firstException = e;
                } else {
                    firstException.addSuppressed(e);
                }
            }
        }

        if (firstException != null) {
            if (firstException instanceof IOException) {
                throw (IOException) firstException;
            } else {
                // since we only assigned an IOException or a RuntimeException to ex above, in this case ex must be a RuntimeException
                throw (RuntimeException) firstException;
            }
        }
    }

    /**
     * Closes all given {@link Closeable}s, suppressing all thrown exceptions. Some of the {@link Closeable}s may be null, they are ignored.
     *
     * @param objects objects to close
     */
    public static void closeWhileHandlingException(final Closeable... objects) {
        closeWhileHandlingException(Arrays.asList(objects));
    }

    /**
     * Closes all given {@link Closeable}s, suppressing all thrown exceptions.
     *
     * @param objects objects to close
     * @see #closeWhileHandlingException(Closeable...)
     */
    public static void closeWhileHandlingException(final Iterable<? extends Closeable> objects) {
        for (final Closeable object : objects) {
            closeWhileHandlingException(object);
        }
    }

    /**
     * @see #closeWhileHandlingException(Closeable...)
     */
    public static void closeWhileHandlingException(final Closeable closeable) {
        // noinspection EmptyCatchBlock
        try {
            close(closeable);
        } catch (final IOException | RuntimeException e) {
        }
    }

    /**
     * Deletes all given files, suppressing all thrown {@link IOException}s. Some of the files may be null, if so they are ignored.
     *
     * @param files the paths of files to delete
     */
    public static void deleteFilesIgnoringExceptions(final Path... files) {
        deleteFilesIgnoringExceptions(Arrays.asList(files));
    }

    /**
     * Deletes all given files, suppressing all thrown {@link IOException}s. Some of the files may be null, if so they are ignored.
     *
     * @param files the paths of files to delete
     */
    public static void deleteFilesIgnoringExceptions(final Collection<? extends Path> files) {
        for (final Path name : files) {
            if (name != null) {
                // noinspection EmptyCatchBlock
                try {
                    Files.delete(name);
                } catch (final IOException ignored) {

                }
            }
        }
    }

    /**
     * Deletes one or more files or directories (and everything underneath it).
     *
     * @throws IOException if any of the given files (or their sub-hierarchy files in case of directories) cannot be removed.
     */
    public static void rm(final Path... locations) throws IOException {
        final LinkedHashMap<Path, Throwable> unremoved = rm(new LinkedHashMap<>(), locations);
        if (!unremoved.isEmpty()) {
            final StringBuilder b = new StringBuilder("could not remove the following files (in the order of attempts):\n");
            for (final Map.Entry<Path, Throwable> kv : unremoved.entrySet()) {
                b.append("   ")
                        .append(kv.getKey().toAbsolutePath())
                        .append(": ")
                        .append(kv.getValue())
                        .append("\n");
            }
            throw new IOException(b.toString());
        }
    }

    private static LinkedHashMap<Path, Throwable> rm(final LinkedHashMap<Path, Throwable> unremoved, final Path... locations) {
        if (locations != null) {
            for (final Path location : locations) {
                // TODO: remove this leniency
                if (location != null && Files.exists(location)) {
                    try {
                        Files.walkFileTree(location, new FileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(final Path dir, final IOException impossible) throws IOException {
                                assert impossible == null;

                                try {
                                    Files.delete(dir);
                                } catch (final IOException e) {
                                    unremoved.put(dir, e);
                                }
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                                try {
                                    Files.delete(file);
                                } catch (final IOException exc) {
                                    unremoved.put(file, exc);
                                }
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
                                if (exc != null) {
                                    unremoved.put(file, exc);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (final IOException impossible) {
                        throw new AssertionError("visitor threw exception", impossible);
                    }
                }
            }
        }
        return unremoved;
    }

    // TODO: replace with constants class if needed (cf. org.apache.lucene.util.Constants)
    public static final boolean WINDOWS = System.getProperty("os.name").startsWith("Windows");
    public static final boolean LINUX = System.getProperty("os.name").startsWith("Linux");
    public static final boolean MAC_OS_X = System.getProperty("os.name").startsWith("Mac OS X");

    /**
     * Ensure that any writes to the given file is written to the storage device that contains it. The {@code isDir} parameter specifies
     * whether or not the path to sync is a directory. This is needed because we open for read and ignore an {@link IOException} since not
     * all filesystems and operating systems support fsyncing on a directory. For regular files we must open for write for the fsync to have
     * an effect.
     *
     * @param fileToSync the file to fsync
     * @param isDir      if true, the given file is a directory (we open for read and ignore {@link IOException}s, because not all file
     *                   systems and operating systems allow to fsync on a directory)
     */
    public static void fsync(final Path fileToSync, final boolean isDir) throws IOException {
        if (isDir && WINDOWS) {
            // opening a directory on Windows fails, directories can not be fsynced there
            if (Files.exists(fileToSync) == false) {
                // yet do not suppress trying to fsync directories that do not exist
                throw new NoSuchFileException(fileToSync.toString());
            }
            return;
        }
        try (FileChannel file = FileChannel.open(fileToSync, isDir ? StandardOpenOption.READ : StandardOpenOption.WRITE)) {
            try {
                file.force(true);
            } catch (final IOException e) {
                if (isDir) {
                    assert (LINUX || MAC_OS_X) == false :
                            "on Linux and MacOSX fsyncing a directory should not throw IOException, " +
                                    "we just don't want to rely on that in production (undocumented); got: " + e;
                    // ignore exception if it is a directory
                    return;
                }
                // throw original exception
                throw e;
            }
        }
    }
}
