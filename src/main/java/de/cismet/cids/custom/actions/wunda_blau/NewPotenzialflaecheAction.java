/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.actions.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.types.treenode.DefaultMetaTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.permission.Policy;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.objecteditors.wunda_blau.PfPotenzialflaecheEditor;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import static javax.swing.Action.NAME;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class NewPotenzialflaecheAction extends AbstractCidsBeanAction implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            NewPotenzialflaecheAction.class);

    //~ Instance fields --------------------------------------------------------

    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FsStatusBearbeitetAction object.
     *
     * @param  cidsBean           ptn DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public NewPotenzialflaecheAction(final CidsBean cidsBean, final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        setCidsBean(cidsBean);
        putValue(
            NAME,
            String.format(
                "Neue Potenzialfläche der Kategorie \"%s\"",
                (String)cidsBean.getProperty("bezeichnung")));
        final ImageIcon icon = new ImageIcon(cidsBean.getMetaObject().getMetaClass().getIcon().getImageData());
        putValue(SMALL_ICON, icon);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        try {
            createAndGotoPotenzialflaeche(null);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  origPfBean  DOCUMENT ME!
     */
    private void createAndGotoPotenzialflaeche(final CidsBean origPfBean) {
        try {
            final CidsBean copyBean = PfPotenzialflaecheEditor.createCopyOf(origPfBean, getConnectionContext());
            copyBean.setProperty("kampagne", getCidsBean());
            final MetaObjectNode metaObjectNode = new MetaObjectNode(
                    -1,
                    SessionManager.getSession().getUser().getDomain(),
                    copyBean.getMetaObject(),
                    null,
                    null,
                    true,
                    Policy.createWIKIPolicy(),
                    -1,
                    null,
                    false);
            final DefaultMetaTreeNode metaTreeNode = new ObjectTreeNode(metaObjectNode, getConnectionContext());
            ComponentRegistry.getRegistry().showComponent(ComponentRegistry.ATTRIBUTE_EDITOR);
            ComponentRegistry.getRegistry().getAttributeEditor().setTreeNode(metaTreeNode);
        } catch (Exception e) {
            LOG.error("Error while creating a new object", e);
        }
    }
}
