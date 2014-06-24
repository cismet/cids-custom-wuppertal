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
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class AuftragsbuchToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final Integer auftragsart = (Integer)cidsBean.getProperty("auftragsnummer");
        if (auftragsart != null) {
            return auftragsart.toString();
        } else {
            return "keine Auftragsnummer vergeben";
        }
    }
}
