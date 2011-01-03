/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * AdressStringConverter.java
 *
 * Created on 11. Mai 2004, 13:31
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;
import Sirius.server.localserver.attribute.*;

import de.cismet.cids.tools.tostring.ToStringConverter;
/**
 * DOCUMENT ME!
 *
 * @author   schlob
 * @version  $Revision$, $Date$
 */
public class AltstandortbetriebeToStringConverter extends ToStringConverter implements java.io.Serializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of AdressStringConverter.
     */
    public AltstandortbetriebeToStringConverter() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   o  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String convert(final Sirius.server.localserver.object.Object o) {
        String stringRepresentation = "";

        final ObjectAttribute[] attrs = o.getAttribs();

        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i].getName().equalsIgnoreCase("Betrieb") || attrs[i].getName().equalsIgnoreCase("Betriebe")) {
                stringRepresentation += (attrs[i].toString() + " ");
            } else // surpress
            {
                // stringRepresentation+=( attrs[i].toString() + "?");
                // System.err.println("unerwartetes Attribut implements StringConverter");
            }
        }

        return stringRepresentation;
    }
}
