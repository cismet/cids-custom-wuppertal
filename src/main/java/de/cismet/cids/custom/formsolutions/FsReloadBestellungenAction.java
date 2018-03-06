/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.formsolutions;

import Sirius.navigator.types.treenode.PureTreeNode;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

import de.cismet.connectioncontext.ClientConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;

import static javax.swing.Action.NAME;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FsReloadBestellungenAction extends AbstractCidsBeanAction implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FsReloadBestellungenAction.class);

    //~ Instance fields --------------------------------------------------------

    private final PureTreeNode ptn;
    private final ClientConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FsStatusBearbeitetAction object.
     *
     * @param  ptn                DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public FsReloadBestellungenAction(final PureTreeNode ptn, final ClientConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        putValue(NAME, "Offene Bestellungen vom Formularserver abholen");
        final ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(
                    "/res/16/FsBestellungReload.png"));
        putValue(SMALL_ICON, icon);

        this.ptn = ptn;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        try {
            StaticSwingTools.showDialog(new FSReloadBestellungenDialog(getConnectionContext()));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    @Override
    public ClientConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
