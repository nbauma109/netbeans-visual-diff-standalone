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
 * Installs our caret row highlight override (highlight-caret-row) for the
 * current font colors profile, based on the same theme properties file
 * that we already use for token colors.
 *
 * We call this once before creating any diff views or editors.
 */
public final class CaretRowHighlightOverrides {

    private CaretRowHighlightOverrides() {
    }

    /**
     * Apply the caret row background color from our theme properties.
     * 
     * It expects a key "highlight-caret-row.bg=FF262626" in the selected
     * theme properties file (dark or light).
     */
    public static void applyFromTheme() {
        Color caretRowColor = loadCaretRowColorFromTheme();
        if (caretRowColor == null) {
            return;
        }
        applyForCurrentProfile(caretRowColor);
    }

    /**
     * Apply a given caret row background color to the current font colors
     * profile by updating the "highlight-caret-row" entry in the
     * highlightings map.
     */
    public static void applyForCurrentProfile(Color caretRowColor) {
        EditorSettingsImpl settings = EditorSettingsImpl.getInstance();
        String profile = settings.getCurrentFontColorProfile();

        // We ask for the current merged highlightings for this profile.
        Map<String, AttributeSet> merged = settings.getHighlightings(profile);
        Map<String, AttributeSet> newMap = new HashMap<>(merged);

        AttributeSet base = newMap.get("highlight-caret-row");
        SimpleAttributeSet attrs = base != null
                ? new SimpleAttributeSet(base)
                : new SimpleAttributeSet();

        attrs.addAttribute(StyleConstants.NameAttribute, "highlight-caret-row");
        attrs.addAttribute(StyleConstants.Background, caretRowColor);

        newMap.put("highlight-caret-row", attrs);

        // This writes a user highlights file and updates internal caches.
        settings.setHighlightings(profile, newMap);
    }

    /**
     * Load our caret row color from the same theme properties that we use
     * for tokens (dark or light).
     */
    private static Color loadCaretRowColorFromTheme() {
        String resource = isUsingDarkMode()
                ? "/de/cismet/custom/visualdiff/dark.theme.properties"
                : "/de/cismet/custom/visualdiff/eclipse.theme.properties";

        Properties theme = new Properties();
        try (InputStream in = CaretRowHighlightOverrides.class.getResourceAsStream(resource)) {
            if (in == null) {
                return null;
            }
            theme.load(in);
        } catch (IOException ex) {
            return null;
        }

        String raw = theme.getProperty("highlight-caret-row.bg");
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        return argb(raw.trim());
    }

    /**
     * Same ARGB parsing logic we already use for token colors.
     * Accepts "FFAABBCC" or "0xFFAABBCC" or "AABBCC" (RGB).
     */
    private static Color argb(String hex) {
        String s = hex.startsWith("0x") || hex.startsWith("0X") ? hex.substring(2) : hex;
        long v = Long.parseUnsignedLong(s, 16);
        if (s.length() == 6) {
            v |= 0xFF000000L;
        }
        return new Color((int) v, true);
    }

}
