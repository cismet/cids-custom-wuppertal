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

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingBillingToStringConverter extends CustomToStringConverter {

    //~ Static fields/initializers ---------------------------------------------

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("geschaeftsbuchnummer")
    public String geschaeftsbuchnummer = "keine Geschäftsbuchnummer angegeben";

    @CidsAttribute("angelegt_durch.kunde.name")
    public String kundenname = "";

    @CidsAttribute("username")
    public String username = "kein Benutzername";

    @CidsAttribute("ts")
    public Date angelegt;

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
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
