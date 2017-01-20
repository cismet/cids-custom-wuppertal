/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingBillingToStringConverter extends CustomToStringConverter {

    //~ Static fields/initializers ---------------------------------------------

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        String geschaeftsbuchnummer = (String)cidsBean.getProperty("geschaeftsbuchnummer");
        if (geschaeftsbuchnummer == null) {
            geschaeftsbuchnummer = "keine Geschäftsbuchnummer angegeben";
        }

        String kundenname = (String)cidsBean.getProperty("angelegt_durch.kunde.name");
        if (kundenname == null) {
            kundenname = "";
        }

        String username = (String)cidsBean.getProperty("username");
        if (username == null) {
            username = "kein Benutzername";
        }

        final Date angelegt = (Date)cidsBean.getProperty("ts");

        final StringBuilder name = new StringBuilder();
        if (!kundenname.equals("")) {
            name.append(kundenname);
        } else {
            name.append(username);
        }
        name.append(" - ");
        name.append(geschaeftsbuchnummer);
        name.append(" - ");
        name.append(DATE_FORMAT.format(angelegt));

        return name.toString();
    }
}
