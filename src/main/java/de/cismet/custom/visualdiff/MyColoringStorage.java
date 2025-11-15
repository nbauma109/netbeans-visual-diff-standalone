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
import org.w3c.dom.Document;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

@ServiceProvider(service = StorageDescription.class)
public class MyColoringStorage implements StorageDescription<String, AttributeSet>, StorageImpl.Operations<String, AttributeSet> {

    public static final String FAV_TOKEN = "token"; // NOI18N
    public static final String ID = "FontsColors"; // NOI18N
    public static final String MIME_TYPE = "text/x-nbeditor-fontcolorsettings"; // NOI18N

    private final ColoringStorage coloringStorage;

    @Override
    public Map<String, AttributeSet> load(MimePath mimePath, String profile, boolean defaults) throws IOException {
        Map<String, AttributeSet> colorMap = new HashMap<>(coloringStorage.load(mimePath, profile, defaults));
        String resource =
                isUsingDarkMode()
                ? "/de/cismet/custom/visualdiff/dark.theme.properties"
                : "/de/cismet/custom/visualdiff/eclipse.theme.properties";

        Properties theme = new Properties();
        try (InputStream in = getClass().getResourceAsStream(resource)) {
            if (in != null) {
                theme.load(in);
            }
        }

        // Accept keys in two forms:
        //   name.fg=FFAABBCC   -> sets Foreground
        //   name.bg=FF112233   -> sets Background
        // For backward compatibility also accept plain 'name=FFAABBCC' meaning foreground.
        Map<String, SimpleAttributeSet> pending = new HashMap<>();

        for (Entry<Object, Object> e : theme.entrySet()) {
            String key = String.valueOf(e.getKey()).trim();
            String value = String.valueOf(e.getValue()).trim();

            String name = key;
            boolean isBg = false;
            boolean isFg = false;

            int dot = key.lastIndexOf('.');
            if (dot > 0) {
                String suffix = key.substring(dot + 1);
                name = key.substring(0, dot);
                if ("bg".equalsIgnoreCase(suffix)) isBg = true;
                if ("fg".equalsIgnoreCase(suffix)) isFg = true;
            } else {
                isFg = true; // legacy form was foreground-only
            }

            Color c = argb(value);

            SimpleAttributeSet over = pending.computeIfAbsent(name, n -> new SimpleAttributeSet());
            if (isBg) {
                over.addAttribute(StyleConstants.Background, c);
            } else if (isFg) {
                over.addAttribute(StyleConstants.Foreground, c);
            }
        }

        for (Entry<String, SimpleAttributeSet> e : pending.entrySet()) {
            String name = e.getKey();
            AttributeSet base = colorMap.get(name);
            if (base != null) {
                colorMap.put(name, AttrSet.merge(e.getValue(), base));
            } else {
                colorMap.put(name, e.getValue());
            }
        }

        return colorMap;
    }

    /**
     * Returns true if JD-GUI config file has
     * XPath //UIMainWindowPreferencesProvider.darkMode/text() = true
     */
    public static boolean isUsingDarkMode() {
        File configFile = ConfigFileUtil.getJDGUIConfigFile();
        if (configFile == null || !configFile.isFile()) {
            return false; // no config file found
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setNamespaceAware(false);
            dbf.setIgnoringComments(true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(configFile);

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expr = xpath.compile("//UIMainWindowPreferencesProvider.darkMode/text()");
            String value = (String) expr.evaluate(doc, XPathConstants.STRING);

            return "true".equalsIgnoreCase(value.trim());
        } catch (Exception ex) {
            // log if you have a logger, otherwise silently fall back to false
            return false;
        }
    }

	private static Color argb(String hex) {
        // Accept ARGB with or without 0x prefix
        String s = hex.startsWith("0x") || hex.startsWith("0X") ? hex.substring(2) : hex;
        long v = Long.parseUnsignedLong(s, 16);
        // If RGB provided (6 hex digits), force opaque
        if (s.length() == 6) v |= 0xFF000000L;
        return new Color((int) v, true);
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