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
package de.cismet.cids.custom.objectrenderer.utils.billing;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.newuser.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import static de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup.ALLOWED_USAGE_CONFIG_ATTR;
import static de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup.RESTRICTED_USAGE_CONFIG_ATTR;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class VerwendungszweckPanel extends javax.swing.JPanel implements FilterSettingChangedTrigger {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VerwendungszweckPanel.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HashMap<String, Usage> USAGES = new HashMap<String, Usage>();

    static {
        try {
            final BillingInfo billingInfo = MAPPER.readValue(BillingInfo.class.getResourceAsStream(
                        "/de/cismet/cids/custom/billing/billing.json"),
                    BillingInfo.class);

            final ArrayList<Usage> lu = billingInfo.getUsages();
            for (final Usage u : lu) {
                USAGES.put(u.getKey(), u);
            }
        } catch (IOException ioException) {
            LOG.error("Error when trying to read the billingInfo.json", ioException);
        }
    }

    //~ Instance fields --------------------------------------------------------

    private final HashMap<JCheckBox, Usage> mappingJCheckboxToUsages = new HashMap<JCheckBox, Usage>();
    private Action filterAction;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlVerwendungszweckCheckBoxes;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form VerwendungszweckPanel.
     */
    public VerwendungszweckPanel() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user     DOCUMENT ME!
     * @param   product  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private static String[] getAllowedUsages(final User user, final String product) throws ConnectionException {
        final Set<String> allowedUsages = new LinkedHashSet<String>();

        final String rawAllowedUsageLines = SessionManager.getConnection()
                    .getConfigAttr(user, ALLOWED_USAGE_CONFIG_ATTR);
        if (rawAllowedUsageLines != null) {
            for (final String rawAllowedUsageLine : rawAllowedUsageLines.split("\n")) {
                final int indexOfAllowed = rawAllowedUsageLine.indexOf(":");
                final String allowedProduct = (indexOfAllowed > -1) ? rawAllowedUsageLine.substring(0, indexOfAllowed)
                                                                    : null;
                if ((allowedProduct == null) || (product == null) || allowedProduct.equals(product)) {
                    allowedUsages.addAll(Arrays.asList(rawAllowedUsageLine.substring(indexOfAllowed + 1).split(",")));
                }
            }
        }
//        if (!allowedUsages.isEmpty()) {
//            final String rawRestrcitedUsageLines = SessionManager.getConnection()
//                        .getConfigAttr(user, RESTRICTED_USAGE_CONFIG_ATTR);
//            if (rawRestrcitedUsageLines != null) {
//                for (final String rawRestrcitedUsageLine : rawRestrcitedUsageLines.split("\n")) {
//                    final int indexOfRestricted = rawRestrcitedUsageLine.indexOf(":");
//                    final String restrictedProduct = (indexOfRestricted > -1)
//                        ? rawRestrcitedUsageLine.substring(0, indexOfRestricted) : null;
//                    if ((restrictedProduct == null) || (product == null) || restrictedProduct.equals(product)) {
//                        allowedUsages.removeAll(Arrays.asList(
//                                rawRestrcitedUsageLine.substring(indexOfRestricted + 1).split(",")));
//                    }
//                }
//            }
//        }

        return allowedUsages.toArray(new String[0]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  filterUsages  DOCUMENT ME!
     */
    public void initVerwendungszweckCheckBoxes(final boolean filterUsages) {
        mappingJCheckboxToUsages.clear();
        pnlVerwendungszweckCheckBoxes.removeAll();

        Set<String> allowedUsages = null;
        if (filterUsages) {
            try {
                allowedUsages = new HashSet<String>(Arrays.asList(
                            getAllowedUsages(SessionManager.getSession().getUser(), null)));
            } catch (final Exception ex) {
                allowedUsages = new HashSet<String>();
            }
        }

        final List<JCheckBox> checkboxes = new ArrayList<JCheckBox>(USAGES.values().size());
        for (final Usage usage : USAGES.values()) {
            if ((allowedUsages == null) || allowedUsages.contains(usage.getKey())) {
                final JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected(true);
                checkBox.setText(usage.getName());
                checkBox.setToolTipText(usage.getKey());
                checkBox.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            boolean noneSelected = true;
                            for (final JCheckBox cb : mappingJCheckboxToUsages.keySet()) {
                                if (cb.isSelected()) {
                                    noneSelected = false;
                                    break;
                                }
                            }
                            if (noneSelected) {
                                ((JCheckBox)e.getSource()).setSelected(true);
                                final String title = NbBundle.getMessage(
                                        VerwendungszweckPanel.class,
                                        "VerwendungszweckPanel.initVerwendungszweckCheckBoxes().actionPerformed().dialog.title");
                                final String message = NbBundle.getMessage(
                                        VerwendungszweckPanel.class,
                                        "VerwendungszweckPanel.initVerwendungszweckCheckBoxes().actionPerformed().dialog.message");
                                JOptionPane.showMessageDialog(
                                    VerwendungszweckPanel.this.getTopLevelAncestor(),
                                    message,
                                    title,
                                    JOptionPane.INFORMATION_MESSAGE);
                            } else if (filterAction != null) {
                                filterAction.actionPerformed(null);
                            }
                        }
                    });

                mappingJCheckboxToUsages.put(checkBox, usage);
                checkboxes.add(checkBox);
            }
        }

        final Collator deCollator = Collator.getInstance(Locale.GERMANY);
        Collections.sort(checkboxes, new Comparator<JCheckBox>() {

                @Override
                public int compare(final JCheckBox o1, final JCheckBox o2) {
                    return deCollator.compare(o1.getText(), o2.getText());
                }
            });

        final List<JCheckBox> leftCol = checkboxes.subList(0, (int)Math.ceil(checkboxes.size() / 2f));
        final List<JCheckBox> rightCol = checkboxes.subList((int)Math.ceil(checkboxes.size() / 2f), checkboxes.size());

        final Iterator<JCheckBox> itLeft = leftCol.iterator();
        final Iterator<JCheckBox> itRight = rightCol.iterator();
        while (itLeft.hasNext()) {
            pnlVerwendungszweckCheckBoxes.add(itLeft.next());
            if (itRight.hasNext()) {
                pnlVerwendungszweckCheckBoxes.add(itRight.next());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        pnlVerwendungszweckCheckBoxes = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    VerwendungszweckPanel.class,
                    "VerwendungszweckPanel.border.title"))); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        pnlVerwendungszweckCheckBoxes.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 6, 3, 3));
        pnlVerwendungszweckCheckBoxes.setLayout(new java.awt.GridLayout(0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlVerwendungszweckCheckBoxes, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public Action getFilterSettingChangedAction() {
        return this.filterAction;
    }

    @Override
    public void setFilterSettingChangedAction(final Action filterAction) {
        this.filterAction = filterAction;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<String> createSelectedVerwendungszweckKeysStringArray() {
        final ArrayList<String> ret = new ArrayList<String>();
        for (final JCheckBox jCheckBox : mappingJCheckboxToUsages.keySet()) {
            if (jCheckBox.isSelected()) {
                final Usage usage = mappingJCheckboxToUsages.get(jCheckBox);
                ret.add(usage.getKey());
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static HashMap<String, Usage> getUsages() {
        return USAGES;
    }
}
