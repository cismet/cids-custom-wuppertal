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
package de.cismet.cids.custom.wunda_blau;

import Sirius.navigator.types.treenode.DefaultMetaTreeNode;

import Sirius.server.middleware.types.MetaNode;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;

import de.cismet.cids.custom.objectrenderer.utils.BillingRestrictedReportJButton;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.ext.CExtContext;
import de.cismet.ext.CExtProvider;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CExtProvider.class)
public class JahresberichtCExtProvider implements CExtProvider<AbstractAction> {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(JahresberichtCExtProvider.class);

    //~ Instance fields --------------------------------------------------------

    private final String ifaceClass;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KundengruppeCExtProvider object.
     */
    public JahresberichtCExtProvider() {
        ifaceClass = "de.cismet.cids.utils.interfaces.CidsBeanAction";
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<? extends AbstractAction> provideExtensions(final CExtContext context) {
        final List<AbstractAction> actions = new ArrayList<AbstractAction>(1);

        if (ObjectRendererUtils.checkActionTag(BillingRestrictedReportJButton.BILLING_ACTION_TAG_REPORT)) {
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

                DefaultMetaTreeNode moNode = null;
                if (ctxObject instanceof DefaultMetaTreeNode) {
                    moNode = (DefaultMetaTreeNode)ctxObject;
                }

                if ((moNode != null)
                            && (moNode.getUserObject() != null)
                            && (moNode.getUserObject() instanceof MetaNode)
                            && "de.cismet.custom.wunda_blau.nodes.Buchungen".equals(
                                ((MetaNode)moNode.getUserObject()).getArtificialId())) {
                    final AbstractAction action = new JahresberichtAction();
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    public Class<? extends AbstractAction> getType() {
        return AbstractAction.class;
    }

    @Override
    public boolean canProvide(final Class<?> c) {
        final String cName = c.getCanonicalName();

        return (cName == null) ? false : (ifaceClass.equals(cName)); // || concreteClass1.equals(cName) ||
                                                                     // concreteClass2.equals(cName));
    }
}
