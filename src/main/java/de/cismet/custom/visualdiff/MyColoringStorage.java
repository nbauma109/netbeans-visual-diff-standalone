package de.cismet.custom.visualdiff;

import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.modules.editor.settings.AttrSet;
import org.netbeans.modules.editor.settings.storage.StorageImpl;
import org.netbeans.modules.editor.settings.storage.fontscolors.ColoringStorage;
import org.netbeans.modules.editor.settings.storage.spi.StorageDescription;
import org.netbeans.modules.editor.settings.storage.spi.StorageReader;
import org.netbeans.modules.editor.settings.storage.spi.StorageWriter;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

@ServiceProvider(service = StorageDescription.class)
public final class MyColoringStorage implements StorageDescription<String, AttributeSet>, StorageImpl.Operations<String, AttributeSet> {

    public static final String FAV_TOKEN = "token"; // NOI18N
    public static final String ID = "FontsColors"; // NOI18N
    public static final String MIME_TYPE = "text/x-nbeditor-fontcolorsettings"; // NOI18N

    private final ColoringStorage coloringStorage;

    @Override
    public Map<String, AttributeSet> load(MimePath mimePath, String profile, boolean defaults) throws IOException {
        Map<String, AttributeSet> colorMap = new HashMap<>(coloringStorage.load(mimePath, profile, defaults));
        Properties eclipseThemeProps = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/de/cismet/custom/visualdiff/eclipse.theme.properties")) {
            eclipseThemeProps.load(in);
        }
        for (Entry<Object, Object> prop : eclipseThemeProps.entrySet()) {
            AttributeSet attrSet = colorMap.get(prop.getKey());
            if (attrSet != null) {
                SimpleAttributeSet overrideAttrSet = new SimpleAttributeSet();
                Color newColor = new Color((int) Long.parseLong((String) prop.getValue(), 16));
                overrideAttrSet.addAttribute(StyleConstants.Foreground, newColor);
                colorMap.put((String) prop.getKey(), AttrSet.merge(overrideAttrSet, attrSet));
            }
        }
        return colorMap;
    }

    @Override
    public boolean save(MimePath mimePath, String profile, boolean defaults, Map<String, AttributeSet> data, Map<String, AttributeSet> defaultData) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(MimePath mimePath, String profile, boolean defaults) throws IOException {
        throw new UnsupportedOperationException();
    }

    public MyColoringStorage(String type) {
        this.coloringStorage = new ColoringStorage(type);
    }

    // ---------------------------------------------------------
    // StorageDescription implementation
    // ---------------------------------------------------------

    public MyColoringStorage() {
        this(FAV_TOKEN);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean isUsingProfiles() {
        return true;
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    public String getLegacyFileName() {
        return null;
    }

    @Override
    public StorageReader<String, AttributeSet> createReader(FileObject f, String mimePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageWriter<String, AttributeSet> createWriter(FileObject f, String mimePath) {
        throw new UnsupportedOperationException();
    }
}