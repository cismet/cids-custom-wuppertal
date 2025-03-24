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
package de.cismet.cids.custom.wupp.client.alkis;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public class GrundbuchblattInputFieldConfig extends AbstractInputFieldConfig implements Serializable {

    //~ Static fields/initializers ---------------------------------------------

    public static final GrundbuchblattInputFieldConfig FallbackConfig = new GrundbuchblattInputFieldConfig('-', 4, 7);

    //~ Instance fields --------------------------------------------------------

    private int maxBuchungsblattnummerField;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GrundbuchblattInputFieldConfig object.
     */
    public GrundbuchblattInputFieldConfig() {
    }

    /**
     * Creates a new GrundbuchblattInputFieldConfig object.
     *
     * @param  delimiter1                   DOCUMENT ME!
     * @param  lenDistrictField             DOCUMENT ME!
     * @param  maxBuchungsblattnummerField  DOCUMENT ME!
     */
    public GrundbuchblattInputFieldConfig(final char delimiter1,
            final int lenDistrictField,
            final int maxBuchungsblattnummerField) {
        super(lenDistrictField, delimiter1);
        this.maxBuchungsblattnummerField = maxBuchungsblattnummerField;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMaxBuchungsblattnummerField() {
        return maxBuchungsblattnummerField;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxBuchungsblattnummerField  DOCUMENT ME!
     */
    public void setMaxBuchungsblattnummerField(final int maxBuchungsblattnummerField) {
        this.maxBuchungsblattnummerField = maxBuchungsblattnummerField;
    }
}
