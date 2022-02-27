/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.custom.visualdiff;

import org.netbeans.api.diff.Difference;
import org.netbeans.api.diff.StreamSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

/**
 * Custom StreamSource implementation which handles FileToDiff objects.
 *
 * @version  $Revision$, $Date$
 */
public class MyStreamSource extends StreamSource {

    //~ Instance fields ----------------------------------------------------

    private FileToDiff fileToDiff;

    //~ Constructors -------------------------------------------------------

    /**
     * Creates a new MyStreamSource object.
     *
     * @param  fileToDiff  The FileToDiff object to wrap.
     */
    public MyStreamSource(final FileToDiff fileToDiff) {
        this.fileToDiff = fileToDiff;
    }

    //~ Methods ------------------------------------------------------------

    @Override
    public String getName() {
        return "name";
    }

    @Override
    public String getTitle() {
        return fileToDiff.title();
    }

    @Override
    public String getMIMEType() {
        return fileToDiff.mimetype();
    }

    @Override
    public Reader createReader() throws IOException {
        return new StringReader(fileToDiff.content());
    }

    @Override
    public Writer createWriter(final Difference[] conflicts) throws IOException {
        return null;
    }
}