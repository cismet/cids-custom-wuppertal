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
 *test
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;
import Sirius.server.localserver.attribute.*;
import Sirius.server.middleware.types.MetaObject;

import de.cismet.cids.tools.tostring.ToStringConverter;
/**
 * DOCUMENT ME!
 *
 * @author   schlob
 * @version  $Revision$, $Date$
 */
public class AdresseToStringConverter extends ToStringConverter implements java.io.Serializable {

    //~ Instance fields --------------------------------------------------------

    private final transient org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of AdressStringConverter.
     */
    public AdresseToStringConverter() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String convert(final de.cismet.cids.tools.tostring.StringConvertable o) {
        final MetaObject mo = (MetaObject)o;

        String stringRepresentation = "";

        final ObjectAttribute[] attrs = mo.getAttribs();

        for (int i = 0; i < attrs.length; i++) {
            // besser getAttributeByname
            if (attrs[i].getName().equalsIgnoreCase("strasse") || attrs[i].getName().equalsIgnoreCase("hausnummer")) {
                stringRepresentation += (attrs[i].toString() + " ");
            }

//            else //surpress
//            {
//                stringRepresentation+=( attrs[i].toString() + "?");
//              logger.error("unerwartetes Attribut");
//            }

        }

        return stringRepresentation;
    }
}
