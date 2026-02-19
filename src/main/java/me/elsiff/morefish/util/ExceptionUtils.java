package me.elsiff.morefish.util;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public interface ExceptionUtils {

    static Collector<Throwable, ?, Optional<IOException>> toIOException(String error) {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            IOException ioException = new IOException(error);
            list.forEach(ioException::addSuppressed);
            if (ioException.getSuppressed().length > 0) {
                return Optional.of(ioException);
            }

            return Optional.empty();
        });
    }

    static void throwIOException(String error, Stream<? extends Throwable> stream) throws IOException {
        Optional<IOException> exception = stream.collect(toIOException(error));
        if (exception.isPresent()) {
            throw exception.get();
        }
    }
}
