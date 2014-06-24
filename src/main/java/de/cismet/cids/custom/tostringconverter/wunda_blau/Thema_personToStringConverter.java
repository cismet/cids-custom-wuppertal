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
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Thema_personToStringConverter extends CustomToStringConverter {

    //~ Static fields/initializers ---------------------------------------------

    private static final String NULL = "null";

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String anrede = (String)cidsBean.getProperty("anrede");
        final String vorname = (String)cidsBean.getProperty("vorname");
        final String name = (String)cidsBean.getProperty("name");

        String ret = "";
        if ((anrede != null) && !anrede.equalsIgnoreCase(NULL)) {
            ret += anrede;
        }
        if ((vorname != null) && !vorname.equalsIgnoreCase(NULL)) {
            ret += " " + vorname;
        }
        if ((name != null) && !name.equalsIgnoreCase(NULL)) {
            ret += " " + name;
        }
        return ret;
    }
}
