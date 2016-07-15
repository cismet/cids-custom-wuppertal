/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.berechtigungspruefung;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungBearbeitungInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungProperties;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungFreigabeServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BerechtigungspruefungBerebeitungDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final Map<String, BerechtigungspruefungBerebeitungDialog> DIALOG_MAP =
        new HashMap<String, BerechtigungspruefungBerebeitungDialog>();
    private static final Logger LOG = Logger.getLogger(BerechtigungspruefungBerebeitungDialog.class);

    private static final CidsServerMessageNotifierListener NOTIFIER_LISTENER = new CidsServerMessageNotifierListener() {

            @Override
            public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
                final String category = event.getMessage().getCategory();
                if (BerechtigungspruefungProperties.CSM_ANFRAGE.equals(category)) {
                    final String schluessel = (String)event.getMessage().getContent();
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                showDialog(schluessel);
                            }
                        });
                } else if (BerechtigungspruefungProperties.CSM_BEARBEITUNG.equals(category)) {
                    final BerechtigungspruefungBearbeitungInfo info = (BerechtigungspruefungBearbeitungInfo)
                        event.getMessage().getContent();
                    final BerechtigungspruefungBerebeitungDialog dialog = DIALOG_MAP.get(info.getSchluessel());
                    if (dialog != null) {
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.close();
                                }
                            });
                    }
                }
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final String schluessel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BerechtigungspruefungBerebeitungDialog.
     *
     * @param  parent      DOCUMENT ME!
     * @param  modal       DOCUMENT ME!
     * @param  schluessel  DOCUMENT ME!
     */
    private BerechtigungspruefungBerebeitungDialog(final java.awt.Frame parent,
            final boolean modal,
            final String schluessel) {
        super(parent, modal);
        initComponents();
        this.schluessel = schluessel;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsServerMessageNotifierListener getCidsServerMessageNotifierListener() {
        return NOTIFIER_LISTENER;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  schluessel  DOCUMENT ME!
     */
    public static void showDialog(final String schluessel) {
        final BerechtigungspruefungBerebeitungDialog dialog = new BerechtigungspruefungBerebeitungDialog(
                ComponentRegistry.getRegistry().getMainWindow(),
                true,
                schluessel);
        dialog.pack();
        DIALOG_MAP.put(schluessel, dialog);
        StaticSwingTools.showDialog(dialog);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungBerebeitungDialog.class,
                "BerechtigungspruefungBerebeitungDialog.jLabel1.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungBerebeitungDialog.class,
                "BerechtigungspruefungBerebeitungDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungBerebeitungDialog.class,
                "BerechtigungspruefungBerebeitungDialog.jLabel2.text")); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungBerebeitungDialog.class,
                "BerechtigungspruefungBerebeitungDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jButton1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungBerebeitungDialog.class,
                "BerechtigungspruefungBerebeitungDialog.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    public void close() {
        dispose();
        DIALOG_MAP.remove(schluessel);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        close();
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        close();

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
                                            schluessel,
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
                            gotoPruefung(schluessel);
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
    } //GEN-LAST:event_jButton1ActionPerformed

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
}
