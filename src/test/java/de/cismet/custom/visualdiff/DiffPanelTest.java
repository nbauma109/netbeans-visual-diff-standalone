package de.cismet.custom.visualdiff;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiffPanelTest {

    @Test
    void rejectsMissingAndWhitespaceOnlyContent() {
        assertFalse(DiffPanel.isValidContent(null));
        assertFalse(DiffPanel.isValidContent(""));
        assertFalse(DiffPanel.isValidContent(" \t\r\n"));
        assertFalse(DiffPanel.isValidContent("\u2003"));
    }

    @Test
    void acceptsNonWhitespaceContent() {
        assertTrue(DiffPanel.isValidContent(" text "));
        assertTrue(DiffPanel.isValidContent("\u0000"));
    }
}
