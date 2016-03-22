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
package de.cismet.cids.custom.navigatorstartuphooks;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.wunda_blau.BerechtigungspruefungRenderer;
import de.cismet.cids.custom.utils.BaulastBescheinigungDialog;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungHandler;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungProperties;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungFreigabeServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

import de.cismet.tools.configuration.StartupHook;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public class BescheinigungspruefungMessageStartUpHook implements StartupHook {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BescheinigungspruefungMessageStartUpHook.class);

    //~ Methods ----------------------------------------------------------------

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

    @Override
    public void applicationStarted() {
        new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (SessionManager.getConnection().hasConfigAttr(
                                        SessionManager.getSession().getUser(),
                                        "csm://"
                                        + BerechtigungspruefungProperties.CSM_ANFRAGE)) {
                            CidsServerMessageNotifier.getInstance()
                                    .subscribe(
                                        new CidsServerMessageNotifierListener() {

                                            @Override
                                            public void messageRetrieved(
                                                    final CidsServerMessageNotifierListenerEvent event) {
                                                final String schluessel = (String)event.getMessage().getContent();

                                                final String[] options = {
                                                        "Ja, Prüfung übernehmen.",
                                                        "Nein, Dialog schließen."
                                                    };
                                                final int optionIndex = JOptionPane.showOptionDialog(
                                                        StaticSwingTools.getParentFrame(
                                                            ComponentRegistry.getRegistry().getMainWindow()),
                                                        "<html>Es ist eine neue Berechtigungsprüfungs-Anfrage eingegangen.<br/>"
                                                        + "<br/>Möchten Sie die Prüfung übernehmen, und diese Anfrage damit"
                                                        + "<br/>für weitere Prüfer sperren ?",
                                                        "Neue Berechtigungsprüfungs-Anfrage",
                                                        JOptionPane.YES_NO_OPTION,
                                                        JOptionPane.QUESTION_MESSAGE,
                                                        null,
                                                        options,
                                                        options[0]);
                                                switch (optionIndex) {
                                                    case 0: {
                                                        new SwingWorker<BerechtigungspruefungFreigabeServerAction.ReturnType, Void>() {

                                                            @Override
                                                            protected BerechtigungspruefungFreigabeServerAction.ReturnType
                                                            doInBackground() throws Exception {
                                                                try {
                                                                    return
                                                                        (BerechtigungspruefungFreigabeServerAction.ReturnType)
                                                                        SessionManager.getSession().getConnection()
                                                                            .executeTask(
                                                                                    SessionManager.getSession()
                                                                                        .getUser(),
                                                                                    BerechtigungspruefungFreigabeServerAction.TASK_NAME,
                                                                                    SessionManager.getSession()
                                                                                        .getUser().getDomain(),
                                                                                    schluessel,
                                                                                    new ServerActionParameter<String>(
                                                                                        BerechtigungspruefungFreigabeServerAction
                                                                                            .ParameterType.MODUS
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
                                                                            BerechtigungspruefungFreigabeServerAction
                                                                                .ReturnType.OK)) {
                                                                        gotoPruefung(schluessel);
                                                                    } else {
                                                                        final String title = "Fehler beim Sperren.";
                                                                        final String message =
                                                                            "<html>Die Berechtigungs-Anfrage wird bereits von einem anderen Prüfer bearbeitet.";
                                                                        JOptionPane.showMessageDialog(
                                                                            StaticSwingTools.getParentFrame(
                                                                                ComponentRegistry.getRegistry()
                                                                                    .getMainWindow()),
                                                                            message,
                                                                            title,
                                                                            JOptionPane.ERROR_MESSAGE);
                                                                    }
                                                                } catch (final Exception ex) {
                                                                    final String title = "Fehler beim Sperren.";
                                                                    final String message =
                                                                        "Beim Sperren ist es zu unerwartetem einem Fehler gekommen.";
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
                                                    break;
                                                    case 1: {
                                                    }
                                                    break;
                                                }
                                            }
                                        },
                                        BerechtigungspruefungProperties.CSM_ANFRAGE);
                        }
                    } catch (ConnectionException ex) {
                        LOG.warn(
                            "Konnte Rechte an csm://"
                                    + BerechtigungspruefungProperties.CSM_ANFRAGE
                                    + " nicht abfragen. Keine Benachrichtung bei neuen Prüfungsanfragen!",
                            ex);
                    }

                    CidsServerMessageNotifier.getInstance()
                            .subscribe(
                                BaulastBescheinigungDialog.getInstance(),
                                BerechtigungspruefungProperties.CSM_FREIGABE);
                }
            }).start();
    }
}
