/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class BodenVorkaufsrechtEditor extends BodenAbstractEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BodenVorkaufsrechtEditor object.
     */
    public BodenVorkaufsrechtEditor() {
        super();
    }

    /**
     * Creates a new BodenVorkaufsrechtEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public BodenVorkaufsrechtEditor(final boolean editable) {
        super(editable);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getTitleDefaultValue() {
        return "Vorkaufsrecht";
    }
}
