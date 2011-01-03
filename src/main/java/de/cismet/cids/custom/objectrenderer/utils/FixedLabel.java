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
package de.cismet.cids.custom.objectrenderer.utils;

import javax.swing.JLabel;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class FixedLabel extends JLabel {

    //~ Instance fields --------------------------------------------------------

    private int size;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FixedLabel object.
     */
    public FixedLabel() {
        super();
        this.size = 100;
    }

    /**
     * Creates a new FixedLabel object.
     *
     * @param  size  DOCUMENT ME!
     */
    public FixedLabel(final int size) {
        super();
        this.size = size;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setText(String text) {
        if (text != null) {
            text = text.replace("\n", "<br>");
        }
        text = "<html><table width=\"" + size + "\" border=\"0\"><tr><td>" + text + "</tr></table></html>";
        super.setText(text);
    }
}
