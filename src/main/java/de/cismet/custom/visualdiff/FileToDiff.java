/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.custom.visualdiff;

/**
 * A wrapper class for the necessary information of the left or right part of
 * Netbeans' diff component.
 * 
 * @param content  The content to diff.
 * @param mimetype The mimetype of the content.
 * @param title    The title to display.
 * @version $Revision$, $Date$
 */
public record FileToDiff(String content, String mimetype, String title) {
}