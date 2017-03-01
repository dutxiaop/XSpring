package lib.Utils;

import java.util.function.Consumer;

/**
 * Created by xiaoP on 2017/2/25.
 */
public class ExceptionHandler {

    public static void ignore(F f) {
        simple(f, Throwable::printStackTrace);
    }

    public static void simple(F f, Consumer<Exception> ec) {
        try {
            f.accept();
        } catch (Exception e) {
            ec.accept(e);
        }
    }

    public static void throwException(F f) {
        simple(f, (e) -> {
            throw new RuntimeException(e);
        });
    }

    public interface F {
        void accept() throws Exception;
    }
}
