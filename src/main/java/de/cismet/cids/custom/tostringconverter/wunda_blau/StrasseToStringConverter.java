/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * StrassStringConverter.java
 *
 * Created on 11. Mai 2004, 13:44
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;
import Sirius.server.localserver.attribute.*;
import Sirius.server.middleware.types.MetaObject;
import de.cismet.cids.tools.tostring.GeometryStringConverter;
import de.cismet.cids.tools.tostring.ToStringConverter;

import java.util.Collection;
/**
 * DOCUMENT ME!
 *
 * @author   schlob
 * @version  $Revision$, $Date$
 */
public class StrasseToStringConverter extends ToStringConverter implements java.io.Serializable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(
            GeometryStringConverter.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of StrassStringConverter.
     */
    public StrasseToStringConverter() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    public String convert(de.cismet.cids.tools.tostring.StringConvertable o) {
        MetaObject mo = (MetaObject)o;

        String stringRepresentation = "";

        Collection<Attribute> attrs = mo.getAttributeByName("NAME", 1);

        if (!attrs.isEmpty()) {
            Attribute attr = attrs.iterator().next();

            stringRepresentation += (attr.toString() + " ");
        }

        return stringRepresentation;
    }
}
