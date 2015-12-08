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
public class BodenTeilungsgenehmigungEditor extends BodenAbstractEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BodenTeilungsgenehmigungEditor object.
     */
    public BodenTeilungsgenehmigungEditor() {
        super();
    }

    /**
     * Creates a new BodenTeilungsgenehmigungEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public BodenTeilungsgenehmigungEditor(final boolean editable) {
        super(editable);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getTitleDefaultValue() {
        return "Teilungsgenehmigung";
    }
}
