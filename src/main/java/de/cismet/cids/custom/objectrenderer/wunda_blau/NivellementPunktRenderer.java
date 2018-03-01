/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objecteditors.wunda_blau.NivellementPunktEditor;

import de.cismet.cids.server.connectioncontext.ClientConnectionContext;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class NivellementPunktRenderer extends NivellementPunktEditor implements CidsBeanRenderer {

    //~ Instance fields --------------------------------------------------------

    private String title;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NivellementPunktRenderer object.
     */
    public NivellementPunktRenderer() {
        this(ClientConnectionContext.createDeprecated());
    }

    /**
     * Creates a new NivellementPunktRenderer object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public NivellementPunktRenderer(final ClientConnectionContext connectionContext) {
        super(true, connectionContext);

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
