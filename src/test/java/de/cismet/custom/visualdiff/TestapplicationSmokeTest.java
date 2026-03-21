package de.cismet.custom.visualdiff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.api.lexer.Language;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

class TestapplicationSmokeTest {

    private static final String JAVA_MIME_TYPE = "text/x-java";
    private static final String LANGUAGE_PROVIDER_SERVICE = "META-INF/services/org.netbeans.spi.lexer.LanguageProvider";
    private static final Duration WAIT_BETWEEN_CLICKS = Duration.ofMillis(1500);
    private static final Pattern EXCEPTION_PATTERN = Pattern.compile(
            "(?m)^(Exception in thread .+|Caused by: .+|[\\w.$]+(?:Exception|Error): .+)$");
    private static final Pattern SVG_WARNING_PATTERN = Pattern.compile(
            "(?m)^(?:WARNING|AVERTISSEMENT): No interface org\\.openide\\.util\\.spi\\.SVGLoader instance found.*$"
            + "|^(?:INFO|INFOS): No SVG loader available for loading .*$");

    @Test
    @Timeout(value = 90, unit = TimeUnit.SECONDS)
    void clicksAllButtonsWithoutExceptionsInLogs() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Smoke test requires a GUI environment");
        assertLanguageProviderServiceFilePresent();

        ByteArrayOutputStream capturedStdout = new ByteArrayOutputStream();
        ByteArrayOutputStream capturedStderr = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        AtomicReference<Throwable> uncaughtException = new AtomicReference<>();

        PrintStream teeOut = new PrintStream(new TeeOutputStream(originalOut, capturedStdout), true, StandardCharsets.UTF_8);
        PrintStream teeErr = new PrintStream(new TeeOutputStream(originalErr, capturedStderr), true, StandardCharsets.UTF_8);

        Testapplication application = null;

        try {
            System.setOut(teeOut);
            System.setErr(teeErr);
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                uncaughtException.compareAndSet(null, throwable);
                throwable.printStackTrace(System.err);
                if (originalHandler != null) {
                    originalHandler.uncaughtException(thread, throwable);
                }
            });

            application = onEdt(Testapplication::new);
            pause();

            click(application, "btnDiffHTMLFiles");
            pause();

            click(application, "btnDiffJavaFiles");
            pause();
            assertJavaLanguageProviderActivated();

            click(application, "btnDiffJSONFiles");
            pause();

            click(application, "btnDiffTextFiles");
            pause();

            flushEdt();
        } finally {
            if (application != null) {
                Testapplication appToDispose = application;
                onEdt(() -> {
                    appToDispose.dispose();
                    return null;
                });
            }

            Thread.setDefaultUncaughtExceptionHandler(originalHandler);
            System.setOut(originalOut);
            System.setErr(originalErr);

            teeOut.close();
            teeErr.close();
        }

        Throwable throwable = uncaughtException.get();
        if (throwable != null) {
            Assertions.fail("Uncaught exception during smoke test", throwable);
        }

        String logs = capturedStdout.toString(StandardCharsets.UTF_8)
                + System.lineSeparator()
                + capturedStderr.toString(StandardCharsets.UTF_8);

        Matcher matcher = EXCEPTION_PATTERN.matcher(logs);
        if (matcher.find()) {
            Assertions.fail("Smoke test log contains an exception: " + matcher.group(1));
        }

        matcher = SVG_WARNING_PATTERN.matcher(logs);
        if (matcher.find()) {
            Assertions.fail("Smoke test log contains an SVG loader warning: " + matcher.group());
        }
    }

    private static void assertLanguageProviderServiceFilePresent() throws Exception {
        List<String> descriptors = serviceDescriptorContents();
        Assertions.assertFalse(descriptors.isEmpty(), "Missing generated " + LANGUAGE_PROVIDER_SERVICE);

        boolean registered = descriptors.stream()
                .anyMatch(content -> content.contains(LanguageProviderImpl.class.getName()));
        Assertions.assertTrue(registered,
                "Generated " + LANGUAGE_PROVIDER_SERVICE + " does not register " + LanguageProviderImpl.class.getName());
    }

    private static void assertJavaLanguageProviderActivated() {
        Language<?> language = Language.find(JAVA_MIME_TYPE);
        Assertions.assertNotNull(language, "Language.find(\"" + JAVA_MIME_TYPE + "\") returned null");
        Assertions.assertEquals(JAVA_MIME_TYPE, language.mimeType(), "Resolved language mime type mismatch");
    }

    private static List<String> serviceDescriptorContents() throws Exception {
        List<String> contents = new ArrayList<>();
        Enumeration<java.net.URL> urls = TestapplicationSmokeTest.class.getClassLoader().getResources(LANGUAGE_PROVIDER_SERVICE);

        while (urls.hasMoreElements()) {
            java.net.URL url = urls.nextElement();
            try (InputStream in = url.openStream();
                 InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(reader)) {
                contents.add(bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())));
            }
        }

        return contents;
    }

    private static void click(Testapplication application, String fieldName) throws Exception {
        JButton button = button(application, fieldName);
        onEdt(() -> {
            button.doClick();
            return null;
        });
        flushEdt();
    }

    private static JButton button(Testapplication application, String fieldName) throws Exception {
        Field field = Testapplication.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JButton) field.get(application);
    }

    private static void pause() throws Exception {
        flushEdt();
        Thread.sleep(WAIT_BETWEEN_CLICKS.toMillis());
        flushEdt();
    }

    private static void flushEdt() throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            return;
        }
        onEdt(() -> null);
    }

    private static <T> T onEdt(ThrowingSupplier<T> supplier) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            return supplier.get();
        }

        FutureTask<T> task = new FutureTask<>(supplier::get);
        SwingUtilities.invokeAndWait(task);
        return task.get();
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {

        T get() throws Exception;
    }

    private static final class TeeOutputStream extends OutputStream {

        private final OutputStream left;
        private final OutputStream right;

        private TeeOutputStream(OutputStream left, OutputStream right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void write(int b) throws IOException {
            left.write(b);
            right.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            left.write(b, off, len);
            right.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            left.flush();
            right.flush();
        }

        @Override
        public void close() throws IOException {
            flush();
        }
    }
}
