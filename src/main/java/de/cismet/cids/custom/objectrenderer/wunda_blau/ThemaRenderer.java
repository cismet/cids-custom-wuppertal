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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objecteditors.wunda_blau.ThemaEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class ThemaRenderer extends ThemaEditor implements CidsBeanRenderer {

    //~ Instance fields --------------------------------------------------------

// private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private String title = "";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ThemaRenderer object.
     */
    public ThemaRenderer() {
        super(false);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }
}
