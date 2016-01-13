/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.formsolutions;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.wunda_blau.search.actions.FormSolutionBestellungChangeStatusServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

import static javax.swing.Action.NAME;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FsStatusBearbeitetAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FsStatusBearbeitetAction.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FsStatusBearbeitetAction object.
     */
    public FsStatusBearbeitetAction() {
        putValue(NAME, "Als abgearbeitet markieren.");
        final ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(
                    "/res/16/FsBestellung.png"));
        putValue(SMALL_ICON, icon);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        try {
            final MetaObject mo = getCidsBean().getMetaObject();
            final MetaObjectNode mon = new MetaObjectNode(mo.getDomain(), mo.getId(), mo.getClassID());
            final ServerActionParameter<Boolean> paramErledigt = new ServerActionParameter<Boolean>(
                    FormSolutionBestellungChangeStatusServerAction.PARAMETER_TYPE.ERLEDIGT.toString(),
                    true);

            SessionManager.getConnection()
                    .executeTask(SessionManager.getSession().getUser(),
                        FormSolutionBestellungChangeStatusServerAction.TASK_NAME,
                        "WUNDA_BLAU",
                        mon,
                        paramErledigt);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }
}
