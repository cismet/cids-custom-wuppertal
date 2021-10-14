/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.BaumSorteEditor;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumSorteRenderer extends BaumSorteEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumSorteRenderer object.
     */
    public BaumSorteRenderer() {
        super(false);
    }

    //~ Methods ----------------------------------------------------------------

     /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            false,
            "baum_sorte",
            1,
            1280,
            1024);
    }
}
