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
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.connection.SessionManager;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import de.cismet.cids.custom.wunda_blau.search.server.Sb_minAufnahmedatumYearFetcherServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_StadtbildTimeTabs extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_StadtbildTimeTabs.class);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboYear;
    private org.jdesktop.swingx.JXDatePicker dpDay;
    private org.jdesktop.swingx.JXDatePicker dpFTFrom;
    private org.jdesktop.swingx.JXDatePicker dpFTTill;
    private org.jdesktop.swingx.JXDatePicker dpFrom;
    private org.jdesktop.swingx.JXDatePicker dpTo;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel tabDay;
    private javax.swing.JPanel tabFrom;
    private javax.swing.JPanel tabFromTill;
    private javax.swing.JPanel tabTo;
    private javax.swing.JPanel tabYear;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Sb_StadtbildTimeTabs.
     */
    public Sb_StadtbildTimeTabs() {
        initComponents();
        new MinYearFetcherWorker().execute();

        // set Zoomable has to be set to activate the SpinningCalendarHeaderHandler. for more information see
        // JXDatePickerHeaderTakeoff or http://stackoverflow.com/questions/16111943/java-swing-jxdatepicker
        dpDay.getMonthView().setZoomable(true);
        dpFTFrom.getMonthView().setZoomable(true);
        dpFTTill.getMonthView().setZoomable(true);
        dpFrom.getMonthView().setZoomable(true);
        dpTo.getMonthView().setZoomable(true);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  minYear  DOCUMENT ME!
     */
    private void setTimeRelatedModels(final int minYear) {
        // put the current year to the minimum year into the combobox
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        final Integer[] years = new Integer[currentYear - minYear + 2];
        years[0] = null;
        int year = currentYear;
        int i = 1;
        while (year >= minYear) {
            years[i] = year;
            year--;
            i++;
        }
        cboYear.setModel(new javax.swing.DefaultComboBoxModel<Integer>(years));
    }

    /**
     * a Date array with two elements representing a time span (the two dates are not null, an open-ended timespan (till
     * date or from date is null)), a single day (the second dates are the same) or nothing (both dates are null).
     * date[0] = from Date date[1] = till Date
     *
     * @return  DOCUMENT ME!
     */
    public Date[] chooseDates() {
        final Component selectedComponent = tabbedPane.getSelectedComponent();
        // default value is nothing
        final Date[] fromDate_tillDate = new Date[] { null, null };
        if (tabDay.equals(selectedComponent)) {
            fromDate_tillDate[0] = dpDay.getDate();
            fromDate_tillDate[1] = dpDay.getDate();
        } else if (tabYear.equals(selectedComponent)) {
            final Integer selectedYear = (Integer)cboYear.getSelectedItem();
            if (selectedYear != null) {
                final Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.DATE, 1);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                calendar.set(Calendar.YEAR, selectedYear);
                fromDate_tillDate[0] = calendar.getTime();
                calendar.set(Calendar.DATE, 31);
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                calendar.set(Calendar.YEAR, selectedYear);
                fromDate_tillDate[1] = calendar.getTime();
            }
        } else if (tabFrom.equals(selectedComponent)) {
            fromDate_tillDate[0] = dpFrom.getDate();
        } else if (tabTo.equals(selectedComponent)) {
            fromDate_tillDate[1] = dpTo.getDate();
        } else if (tabFromTill.equals(selectedComponent)) {
            fromDate_tillDate[0] = dpFTFrom.getDate();
            fromDate_tillDate[1] = dpFTTill.getDate();
        }
        return fromDate_tillDate;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = new javax.swing.JTabbedPane();
        tabDay = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dpDay = new org.jdesktop.swingx.JXDatePicker();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        tabYear = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        cboYear = new javax.swing.JComboBox();
        tabFrom = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        dpFrom = new org.jdesktop.swingx.JXDatePicker();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        tabTo = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        dpTo = new org.jdesktop.swingx.JXDatePicker();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        tabFromTill = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        dpFTFrom = new org.jdesktop.swingx.JXDatePicker();
        jLabel6 = new javax.swing.JLabel();
        dpFTTill = new org.jdesktop.swingx.JXDatePicker();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));

        setLayout(new java.awt.BorderLayout());

        tabDay.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(Sb_StadtbildTimeTabs.class, "Sb_StadtbildTimeTabs.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 5, 5);
        tabDay.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 5);
        tabDay.add(dpDay, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        tabDay.add(filler1, gridBagConstraints);

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildTimeTabs.class,
                "Sb_StadtbildTimeTabs.tabDay.TabConstraints.tabTitle"),
            tabDay); // NOI18N

        tabYear.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(Sb_StadtbildTimeTabs.class, "Sb_StadtbildTimeTabs.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 5, 5);
        tabYear.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        tabYear.add(filler2, gridBagConstraints);

        cboYear.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2013" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 5);
        tabYear.add(cboYear, gridBagConstraints);

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildTimeTabs.class,
                "Sb_StadtbildTimeTabs.tabYear.TabConstraints.tabTitle"),
            tabYear); // NOI18N

        tabFrom.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(Sb_StadtbildTimeTabs.class, "Sb_StadtbildTimeTabs.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 5, 5);
        tabFrom.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 5);
        tabFrom.add(dpFrom, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        tabFrom.add(filler3, gridBagConstraints);

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildTimeTabs.class,
                "Sb_StadtbildTimeTabs.tabFrom.TabConstraints.tabTitle"),
            tabFrom); // NOI18N

        tabTo.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(Sb_StadtbildTimeTabs.class, "Sb_StadtbildTimeTabs.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 5, 5);
        tabTo.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 5);
        tabTo.add(dpTo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        tabTo.add(filler4, gridBagConstraints);

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildTimeTabs.class,
                "Sb_StadtbildTimeTabs.tabTo.TabConstraints.tabTitle"),
            tabTo); // NOI18N

        tabFromTill.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(Sb_StadtbildTimeTabs.class, "Sb_StadtbildTimeTabs.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 5, 5);
        tabFromTill.add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 5);
        tabFromTill.add(dpFTFrom, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(Sb_StadtbildTimeTabs.class, "Sb_StadtbildTimeTabs.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 5, 5);
        tabFromTill.add(jLabel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 5, 5);
        tabFromTill.add(dpFTTill, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        tabFromTill.add(filler5, gridBagConstraints);

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildTimeTabs.class,
                "Sb_StadtbildTimeTabs.tabFromTill.TabConstraints.tabTitle"),
            tabFromTill); // NOI18N

        add(tabbedPane, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    void clear() {
        dpDay.setDate(null);
        cboYear.setSelectedItem(null);
        dpFrom.setDate(null);
        dpTo.setDate(null);
        dpFTFrom.setDate(null);
        dpFTTill.setDate(null);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MinYearFetcherWorker extends SwingWorker<Integer, Void> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MinYearFetcherWorker object.
         */
        public MinYearFetcherWorker() {
            cboYear.setEnabled(false);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Integer doInBackground() throws Exception {
            final Sb_minAufnahmedatumYearFetcherServerSearch minYearFetcher =
                new Sb_minAufnahmedatumYearFetcherServerSearch();
            final Collection minYearCollection = SessionManager.getConnection()
                        .customServerSearch(SessionManager.getSession().getUser(), minYearFetcher);
            if ((minYearCollection != null) && !minYearCollection.isEmpty()) {
                final ArrayList firstColumnObject = (ArrayList)minYearCollection.toArray(new Object[1])[0];
                final Object firstRowObject = firstColumnObject.get(0);
                return (Integer)firstRowObject;
            }
            return null;
        }

        @Override
        protected void done() {
            Integer minYear = null;
            try {
                minYear = get();
            } catch (InterruptedException ex) {
                LOG.warn(ex, ex);
            } catch (ExecutionException ex) {
                LOG.warn(ex, ex);
            }

            if (minYear == null) {
                minYear = 0;
            }

            setTimeRelatedModels(minYear);
            cboYear.setEnabled(true);
        }
    }
}
