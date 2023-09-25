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
package de.cismet.cids.custom.formsolutions;

import Sirius.navigator.types.treenode.PureTreeNode;
import Sirius.navigator.types.treenode.RootTreeNode;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.tree.TreeNode;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.interfaces.CidsBeanAction;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.ext.CExtContext;
import de.cismet.ext.CExtProvider;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CExtProvider.class)
public class FsStatusCExtProvider implements CExtProvider<CidsBeanAction>, ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FsStatusCExtProvider.class);

    //~ Instance fields --------------------------------------------------------

    private final String ifaceClass;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KundengruppeCExtProvider object.
     */
    public FsStatusCExtProvider() {
        ifaceClass = "de.cismet.cids.utils.interfaces.CidsBeanAction";
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    @Override
    public Collection<? extends CidsBeanAction> provideExtensions(final CExtContext context) {
        final List<CidsBeanAction> actions = new ArrayList<CidsBeanAction>(1);

        if (context != null) {
            final Object ctxReference = context.getProperty(CExtContext.CTX_REFERENCE);

            final Object ctxObject;
            if (ctxReference instanceof Collection) {
                final Collection ctxCollection = (Collection)ctxReference;

                if (ctxCollection.size() == 1) {
                    ctxObject = ctxCollection.iterator().next();
                } else {
                    ctxObject = null;
                }
            } else if (ctxReference instanceof Object[]) {
                final Object[] ctxArray = (Object[])ctxReference;

                if (ctxArray.length == 1) {
                    ctxObject = ctxArray[0];
                } else {
                    ctxObject = null;
                }
            } else {
                ctxObject = ctxReference;
            }

            if (ctxObject instanceof PureTreeNode) {
                final PureTreeNode ptn = (PureTreeNode)ctxObject;
                final TreeNode parent = ptn.getParent();
                if ((parent instanceof RootTreeNode) && (ptn.getMetaNode() != null)
                            && (ptn.getMetaNode().getName() != null)
                            && ptn.getMetaNode().getName().equalsIgnoreCase("produkt-bestellungen")) {
                    actions.add(new FsReloadBestellungenAction(ptn, getConnectionContext()));
                }
            } else {
                final MetaClass mc;
                final CidsBean ctxBean;
                if (ctxObject instanceof CidsBean) {
                    ctxBean = (CidsBean)ctxObject;
                    mc = ctxBean.getMetaObject().getMetaClass();
                } else if (ctxObject instanceof MetaObject) {
                    final MetaObject mo = (MetaObject)ctxObject;
                    ctxBean = mo.getBean();
                    mc = mo.getMetaClass();
                } else {
                    ctxBean = null;
                    mc = null;
                }

                if (((mc != null) && (ctxBean != null)) && ("fs_bestellung".equalsIgnoreCase(mc.getTableName()))
                            && (ctxBean.getProperty("fehler") == null)
                            && ((Boolean)ctxBean.getProperty("postweg"))
                            && !((Boolean)ctxBean.getProperty("erledigt"))) {
                    final CidsBeanAction action1 = new FsStatusBearbeitetAction(getConnectionContext());
                    action1.setCidsBean(ctxBean);
                    actions.add(action1);
                }
            }
        }
        return actions;
    }

    @Override
    public Class<? extends CidsBeanAction> getType() {
        return CidsBeanAction.class;
    }

    @Override
    public boolean canProvide(final Class<?> c) {
        final String cName = c.getCanonicalName();

        return (cName == null) ? false : (ifaceClass.equals(cName));
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
