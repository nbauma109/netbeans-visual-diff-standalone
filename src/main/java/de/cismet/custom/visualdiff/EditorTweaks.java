package de.cismet.custom.visualdiff;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;

public final class EditorTweaks {

    private EditorTweaks() {
    }

    public static void disableTextLimitLine() {
        // Global editor preferences (MimePath.EMPTY)
        Preferences prefs = MimeLookup.getLookup(MimePath.EMPTY)
                                      .lookup(Preferences.class);
        if (prefs == null) {
            return;
        }

        // Turn the line off
        prefs.putBoolean("text-limit-line-visible", false);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            // ignore
        }
    }
}
