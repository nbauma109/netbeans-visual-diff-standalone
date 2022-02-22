package de.cismet.custom.visualdiff;

import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.editor.java.JavaKit;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageProvider;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = LanguageProvider.class)
public class LanguageProviderImpl extends LanguageProvider {

    @Override
    public Language<?> findLanguage(String mimeType) {
        return JavaKit.JAVA_MIME_TYPE.equals(mimeType) ? JavaTokenId.language() : null;
    }

    @Override
    public LanguageEmbedding<?> findLanguageEmbedding(Token<?> token, LanguagePath languagePath, InputAttributes inputAttributes) {
        return null;
    }

}