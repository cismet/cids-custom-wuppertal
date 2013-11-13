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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.Serializable;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
@XmlRootElement
@JsonIgnoreProperties(value = { "delimiter1AsString", "delimiter2AsString" })
public class ParcelInputFieldConfig extends AbstractInputFieldConfig implements Serializable {

    //~ Static fields/initializers ---------------------------------------------

    public static final ParcelInputFieldConfig FallbackConfig = new ParcelInputFieldConfig('-', '/', 4, 3, 5, 4);

    //~ Instance fields --------------------------------------------------------

    private int maxLenParcelNumberField;
    private int maxLenParcelNumeratorField;
    private int maxLenParcelDenominatorField;

    private char delimiter2;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ParcelInputFieldConfig object.
     */
    public ParcelInputFieldConfig() {
        super();
    }

    /**
     * Creates a new ParcelInputFieldConfig object.
     *
     * @param  delimiter1                 DOCUMENT ME!
     * @param  delimiter2                 DOCUMENT ME!
     * @param  lenDistrictField           DOCUMENT ME!
     * @param  lenParcelNumberField       DOCUMENT ME!
     * @param  lenParcelNumeratorField    DOCUMENT ME!
     * @param  lenParcelDenominatorField  DOCUMENT ME!
     */
    private ParcelInputFieldConfig(final char delimiter1,
            final char delimiter2,
            final int lenDistrictField,
            final int lenParcelNumberField,
            final int lenParcelNumeratorField,
            final int lenParcelDenominatorField) {
        super(lenDistrictField, delimiter1);
        maxLenParcelNumberField = lenParcelNumberField;
        maxLenParcelNumeratorField = lenParcelNumeratorField;
        maxLenParcelDenominatorField = lenParcelDenominatorField;
        this.delimiter2 = delimiter2;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMaxLenParcelNumberField() {
        return maxLenParcelNumberField;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxLenParcelNumberField  DOCUMENT ME!
     */
    public void setMaxLenParcelNumberField(final int maxLenParcelNumberField) {
        this.maxLenParcelNumberField = maxLenParcelNumberField;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMaxLenParcelNumeratorField() {
        return maxLenParcelNumeratorField;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxLenParcelNumeratorField  DOCUMENT ME!
     */
    public void setMaxLenParcelNumeratorField(final int maxLenParcelNumeratorField) {
        this.maxLenParcelNumeratorField = maxLenParcelNumeratorField;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMaxLenParcelDenominatorField() {
        return maxLenParcelDenominatorField;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxLenParcelDenominatorField  DOCUMENT ME!
     */
    public void setMaxLenParcelDenominatorField(final int maxLenParcelDenominatorField) {
        this.maxLenParcelDenominatorField = maxLenParcelDenominatorField;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public char getDelimiter2() {
        return delimiter2;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDelimiter2AsString() {
        return String.valueOf(delimiter2);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  delimiter2  DOCUMENT ME!
     */
    public void setDelimiter2(final char delimiter2) {
        this.delimiter2 = delimiter2;
    }
}
