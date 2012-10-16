/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class VermessungGemarkungToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder result = new StringBuilder();
        final Object id = cidsBean.getProperty("id");
        final Object name = cidsBean.getProperty("name");

        if (id instanceof Integer) {
            result.append((Integer)id);
        } else {
            result.append("Fehlerhafte Gemarkung");
            return result.toString();
        }

        result.append(" - ");

        if (name instanceof String) {
            result.append(name.toString());
        } else {
            result.append("Unbekannte Gemarkung");
        }

        return result.toString();
    }
}
