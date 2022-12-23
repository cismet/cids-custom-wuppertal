/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objecteditors.wunda_blau.VermessungGewannenEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class VermessungGewannenRenderer extends VermessungGewannenEditor implements CidsBeanRenderer {

    //~ Instance fields --------------------------------------------------------

    private String title;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VermessungRissRenderer object.
     */
    public VermessungGewannenRenderer() {
        super(true);

        title = "";
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        if (title != null) {
            this.title = title;
        }
    }
}
