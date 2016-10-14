/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.toolbaritem;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.event.ActionEvent;

import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.berechtigungspruefung.BerechtigungspruefungMessageNotifier;
import de.cismet.cids.custom.berechtigungspruefung.BerechtigungspruefungMessageNotifierListener;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungFreigabeServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.CidsClientToolbarItem;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsClientToolbarItem.class)
public class BerechtigungspruefungToolbarWidget extends AbstractAction implements CidsClientToolbarItem {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BerechtigungspruefungToolbarWidget.class);

    private static final boolean VISIBLE = true; // TODO chech for actionattribut
    private static final String TEXT_TEMPLATE = "%s offene Berechtigungsprüfungs-Anfragen";
    private static final String TEXT_TEMPLATE_NONE = "keine offene Berechtigungsprüfungs-Anfragen";
    private static final String TEXT_TEMPLATE_ONE = "Eine offene Berechtigungsprüfungs-Anfrage";

    //~ Instance fields --------------------------------------------------------

    private final BerechtigungspruefungMessageNotifierListener notifierListener = new MessageListener();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MemoryToolbar object.
     */
    public BerechtigungspruefungToolbarWidget() {
        updateAnfragen();

        BerechtigungspruefungMessageNotifier.getInstance().addListener(notifierListener);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        new SwingWorker<BerechtigungspruefungFreigabeServerAction.ReturnType, Void>() {

                @Override
                protected BerechtigungspruefungFreigabeServerAction.ReturnType doInBackground() throws Exception {
                    try {
                        return (BerechtigungspruefungFreigabeServerAction.ReturnType)SessionManager
                                    .getSession().getConnection()
                                    .executeTask(
                                            SessionManager.getSession().getUser(),
                                            BerechtigungspruefungFreigabeServerAction.TASK_NAME,
                                            SessionManager.getSession().getUser().getDomain(),
                                            BerechtigungspruefungMessageNotifier.getInstance()
                                                .getAeltesteOffeneAnfrage(),
                                            new ServerActionParameter<String>(
                                                BerechtigungspruefungFreigabeServerAction.ParameterType.MODUS
                                                    .toString(),
                                                BerechtigungspruefungFreigabeServerAction.MODUS_PRUEFUNG));
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                        return null;
                    }
                }

                @Override
                protected void done() {
                    final BerechtigungspruefungFreigabeServerAction.ReturnType ret;
                    try {
                        ret = get();
                        if (ret.equals(
                                        BerechtigungspruefungFreigabeServerAction.ReturnType.OK)) {
                            gotoPruefung(BerechtigungspruefungMessageNotifier.getInstance().getAeltesteOffeneAnfrage());
                        } else {
                            final String title = "Fehler beim Sperren.";
                            final String message =
                                "<html>Die Berechtigungs-Anfrage wird bereits von einem anderen Prüfer bearbeitet.";
                            JOptionPane.showMessageDialog(
                                StaticSwingTools.getParentFrame(
                                    ComponentRegistry.getRegistry().getMainWindow()),
                                message,
                                title,
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (final Exception ex) {
                        final String title = "Fehler beim Sperren.";
                        final String message = "Beim Sperren ist es zu unerwartetem einem Fehler gekommen.";
                        final ErrorInfo info = new ErrorInfo(
                                title,
                                message,
                                null,
                                null,
                                ex,
                                Level.SEVERE,
                                null);
                        JXErrorPane.showDialog(
                            ComponentRegistry.getRegistry().getMainWindow(),
                            info);

                        LOG.error("Fehler beim Freigeben", ex);
                    }
                }
            }.execute();
    }

    @Override
    public String getSorterString() {
        return "XXX";
    }

    @Override
    public final boolean isVisible() {
        try {
            return (SessionManager.getConnection().getConfigAttr(
                        SessionManager.getSession().getUser(),
                        "csa://"
                                + BerechtigungspruefungFreigabeServerAction.TASK_NAME)
                            != null);
        } catch (final Exception ex) {
            LOG.warn("could not check for csa://" + BerechtigungspruefungFreigabeServerAction.TASK_NAME, ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  schluessel  DOCUMENT ME!
     */
    private void gotoPruefung(final String schluessel) {
        new SwingWorker<MetaObjectNode, Object>() {

                @Override
                protected MetaObjectNode doInBackground() throws Exception {
                    final MetaClass mcBerechtigungspruefung = CidsBean.getMetaClassFromTableName(
                            "WUNDA_BLAU",
                            "berechtigungspruefung");

                    final String pruefungQuery = "SELECT DISTINCT " + mcBerechtigungspruefung.getID() + ", "
                                + mcBerechtigungspruefung.getTableName() + "." + mcBerechtigungspruefung.getPrimaryKey()
                                + " "
                                + "FROM " + mcBerechtigungspruefung.getTableName() + " "
                                + "WHERE " + mcBerechtigungspruefung.getTableName() + ".schluessel LIKE '" + schluessel
                                + "' "
                                + "LIMIT 1;";

                    final MetaObject[] mos = SessionManager.getProxy().getMetaObjectByQuery(pruefungQuery, 0);
                    final CidsBean cidsBean = mos[0].getBean();
                    return new MetaObjectNode(cidsBean);
                }

                @Override
                protected void done() {
                    try {
                        final MetaObjectNode mon = get();
                        ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObjectNode(mon);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void updateAnfragen() {
        int anzahlAnfragen;

        try {
            anzahlAnfragen = BerechtigungspruefungMessageNotifier.getInstance().getOffeneAnfragen().size();
        } catch (final Exception ex) {
            LOG.error("Fehler bei der Abfrage der offenen Anfragen", ex);
            anzahlAnfragen = -1;
        }

        final String name;
        switch (anzahlAnfragen) {
            case 0: {
                name = TEXT_TEMPLATE_NONE;
                setEnabled(false);
            }
            break;
            case 1: {
                name = TEXT_TEMPLATE_ONE;
                setEnabled(true);
            }
            break;
            default: {
                name = String.format(TEXT_TEMPLATE, String.valueOf(anzahlAnfragen));
                setEnabled(true);
            }
        }

        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, name);
        putValue(Action.SMALL_ICON, null);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class MessageListener implements BerechtigungspruefungMessageNotifierListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void anfrageAdded(final String key) {
            updateAnfragen();
        }

        @Override
        public void anfrageRemoved(final String key) {
            updateAnfragen();
        }
    }
}
