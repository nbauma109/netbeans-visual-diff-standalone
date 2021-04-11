package de.cismet.custom.visualdiff;

import javax.swing.text.Document;

import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.lexer.Language;

/**
 * <p>This class is a workaround to support syntax coloring.</p>
 * Check <a href="https://bits.netbeans.org/dev/javadoc/org-netbeans-modules-lexer/org/netbeans/api/lexer/TokenHierarchy.html#get-D-">https://bits.netbeans.org/dev/javadoc/org-netbeans-modules-lexer/org/netbeans/api/lexer/TokenHierarchy.html#get-D-</a>
 * 
 * <p><code>document.putProperty(Language.class, JavaTokenId.language())</code> activates the token hierarchy for syntax coloring</p>
 * 
 * The class name is referenced from here : org/netbeans/modules/java/editor/resources/layer.xml 
 * <pre>
 * {@code
 * <file name="EditorKit.instance">
 *   <attr name="instanceClass" stringvalue="de.cismet.custom.visualdiff.SyntaxColoringJavaKit"/>
 * </file>
 * }
 * </pre>
 * 
 */
public class SyntaxColoringJavaKit extends org.netbeans.modules.editor.java.JavaKit {

    private static final long serialVersionUID = 1L;

    @Override
    public Document createDefaultDocument() {
        Document document = super.createDefaultDocument();
        document.putProperty(Language.class, JavaTokenId.language());
        return document;
    }
    
}
