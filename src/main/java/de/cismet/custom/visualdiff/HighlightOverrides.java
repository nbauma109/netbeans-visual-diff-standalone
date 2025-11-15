package de.cismet.custom.visualdiff;

import org.netbeans.modules.editor.settings.storage.EditorSettingsImpl;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static de.cismet.custom.visualdiff.MyColoringStorage.isUsingDarkMode;

/**
 * Installs our highlight overrides for the current font colors profile.
 * We read both caret row highlight and selection highlight from the same
 * theme properties file used for our token customizations.
 *
 * This class updates HIGHLIGHT category values:
 *   highlight-caret-row
 *   selection
 *
 * We call applyFromTheme() once after startup or before showing diff editors.
 */
public final class HighlightOverrides {

    private HighlightOverrides() {
    }

    /**
     * Load and apply all highlight overrides from the theme file.
     *
     * We look for:
     *     highlight-caret-row.bg=FF262626
     *     selection.bg=FF2A2A2A
     *
     * If the keys are absent, we skip them without providing defaults.
     */
    public static void applyFromTheme() {
        Properties theme = loadTheme();

        if (theme == null) {
            return;
        }

        Color caretRow = parseColor(theme.getProperty("highlight-caret-row.bg"));
        Color selection = parseColor(theme.getProperty("selection.bg"));

        if (caretRow != null) {
            applySingle("highlight-caret-row", caretRow);
        }
        if (selection != null) {
            applySingle("selection", selection);
        }
    }

    /**
     * Update a single HIGHLIGHT entry in the currently active profile.
     * We modify only the background color.
     */
    private static void applySingle(String name, Color background) {
        EditorSettingsImpl settings = EditorSettingsImpl.getInstance();
        String profile = settings.getCurrentFontColorProfile();

        Map<String, AttributeSet> merged = settings.getHighlightings(profile);
        Map<String, AttributeSet> updated = new HashMap<>(merged);

        AttributeSet base = updated.get(name);

        SimpleAttributeSet attrs = base != null
                ? new SimpleAttributeSet(base)
                : new SimpleAttributeSet();

        attrs.addAttribute(StyleConstants.NameAttribute, name);
        attrs.addAttribute(StyleConstants.Background, background);

        updated.put(name, attrs);

        settings.setHighlightings(profile, updated);
    }

    /**
     * Load our theme properties file for the current mode (dark or light).
     */
    private static Properties loadTheme() {
        String resource = isUsingDarkMode()
                ? "/de/cismet/custom/visualdiff/dark.theme.properties"
                : "/de/cismet/custom/visualdiff/eclipse.theme.properties";

        Properties p = new Properties();
        try (InputStream in = HighlightOverrides.class.getResourceAsStream(resource)) {
            if (in == null) {
                return null;
            }
            p.load(in);
            return p;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Parse an ARGB color from a hex string.
     * We accept eight-digit ARGB or six-digit RGB (which we upgrade to opaque).
     */
    private static Color parseColor(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        if (s.startsWith("0x") || s.startsWith("0X")) {
            s = s.substring(2);
        }
        long value = Long.parseUnsignedLong(s, 16);
        if (s.length() == 6) {
            value |= 0xFF000000L;
        }
        return new Color((int) value, true);
    }
}
