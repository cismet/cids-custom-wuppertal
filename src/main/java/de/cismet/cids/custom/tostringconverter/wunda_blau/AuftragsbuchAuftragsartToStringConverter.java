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
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class AuftragsbuchAuftragsartToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String auftragsart = (String)cidsBean.getProperty("auftragsart");
        if (auftragsart != null) {
            return auftragsart;
        } else {
            return "keine Auftragsart ausgew\u00E4hlt";
        }
    }
}
