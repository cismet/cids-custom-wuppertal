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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.StaticSwingTools;

import static java.awt.image.ImageObserver.WIDTH;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class KlimaortAussichtspunktEditor extends javax.swing.JPanel implements CidsBeanRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KlimaortAussichtspunktEditor.class);

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;

    private ConnectionContext connectionContext = ConnectionContext.create(
            AbstractConnectionContext.Category.EDITOR,
            "AussichtspunktEditor");

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemStandort;
    private javax.swing.JComboBox cbGeom;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList jlStandort;
    private javax.swing.JLabel lblBeschr;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblStandort;
    private javax.swing.JPanel panIndikator;
    private javax.swing.JTextField txtBeschr;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboWirtschaftszweigEditor object.
     */
    public KlimaortAussichtspunktEditor() {
        this(true);
    }

    /**
     * Creates a new AlboWirtschaftszweigEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public KlimaortAussichtspunktEditor(final boolean editable) {
        this.editable = editable;
        initComponents();
        jlStandort.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList<?> list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final Component c = super.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);

                    if ((c instanceof JLabel) && (value instanceof CidsBean)) {
                        final String name = (String)((CidsBean)value).getProperty("klima_standort.name");
                        ((JLabel)c).setText(name);
                        setText(name);
                    }

                    return c;
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblBeschr = new javax.swing.JLabel();
        txtBeschr = new javax.swing.JTextField();
        if (editable) {
            lblGeom = new javax.swing.JLabel();
        }
        if (editable) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        lblStandort = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlStandort = new ImplementationList();
        panIndikator = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        if (editable) {
            btnRemStandort = new javax.swing.JButton();
        }

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblName.setText(org.openide.util.NbBundle.getMessage(
                KlimaortAussichtspunktEditor.class,
                "KlimaortAussichtspunktEditor.lblName.text",
                new Object[] {}));  // NOI18N
        lblName.setName("lblName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPanel1.add(lblName, gridBagConstraints);

        txtName.setName("txtName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 10);
        jPanel1.add(txtName, gridBagConstraints);

        lblBeschr.setText(org.openide.util.NbBundle.getMessage(
                KlimaortAussichtspunktEditor.class,
                "KlimaortAussichtspunktEditor.lblBeschr.text",
                new Object[] {}));      // NOI18N
        lblBeschr.setName("lblBeschr"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPanel1.add(lblBeschr, gridBagConstraints);

        txtBeschr.setName("txtBeschr"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.description}"),
                txtBeschr,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 10);
        jPanel1.add(txtBeschr, gridBagConstraints);

        if (editable) {
            lblGeom.setText(org.openide.util.NbBundle.getMessage(
                    KlimaortAussichtspunktEditor.class,
                    "KlimaortAussichtspunktEditor.lblGeom.text",
                    new Object[] {}));  // NOI18N
            lblGeom.setName("lblGeom"); // NOI18N
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
            jPanel1.add(lblGeom, gridBagConstraints);
        }

        if (editable) {
            cbGeom.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            cbGeom.setName("cbGeom");                           // NOI18N

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 10);
            jPanel1.add(cbGeom, gridBagConstraints);
        }

        lblStandort.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStandort.setText(org.openide.util.NbBundle.getMessage(
                KlimaortAussichtspunktEditor.class,
                "KlimaortAussichtspunktEditor.lblStandort.text",
                new Object[] {}));          // NOI18N
        lblStandort.setName("lblStandort"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 2, 10);
        jPanel1.add(lblStandort, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(290, 100));
        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setPreferredSize(new java.awt.Dimension(490, 100));

        jlStandort.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jlStandort.setName("jlStandort"); // NOI18N
        jScrollPane2.setViewportView(jlStandort);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 15, 0);
        add(jScrollPane2, gridBagConstraints);

        panIndikator.setMinimumSize(new java.awt.Dimension(35, 100));
        panIndikator.setName("panIndikator"); // NOI18N
        panIndikator.setOpaque(false);
        panIndikator.setPreferredSize(new java.awt.Dimension(35, 100));

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/up.png"))); // NOI18N
        jButton1.setText(org.openide.util.NbBundle.getMessage(
                KlimaortAussichtspunktEditor.class,
                "KlimaortAussichtspunktEditor.jButton1.text"));                        // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton1.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton1.setName("jButton1");                                                  // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        panIndikator.add(jButton1);
        jButton1.setVisible(isEditable());

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/down.png"))); // NOI18N
        jButton2.setText(org.openide.util.NbBundle.getMessage(
                KlimaortAussichtspunktEditor.class,
                "KlimaortAussichtspunktEditor.jButton2.text"));                          // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton2.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton2.setName("jButton2");                                                    // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton2.setRequestFocusEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        panIndikator.add(jButton2);
        jButton2.setVisible(isEditable());

        if (editable) {
            btnRemStandort.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
            btnRemStandort.setName("btnRemStandort");                                                                 // NOI18N
            btnRemStandort.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        btnRemStandortActionPerformed(evt);
                    }
                });
        }
        if (editable) {
            panIndikator.add(btnRemStandort);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        add(panIndikator, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemStandortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemStandortActionPerformed
        final Object selection = jlStandort.getSelectedValue();

        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll der ausgewählte Standort wirklich gelöscht werden?",
                    "Standort entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    final CidsBean beanToDelete = (CidsBean)selection;
                    final List<CidsBean> standortList = CidsBeanSupport.getBeanCollectionFromProperty(
                            cidsBean,
                            "klima_standort");

                    if (standortList != null) {
                        beanToDelete.delete();
                        standortList.remove(beanToDelete);

                        final int pos = (Integer)beanToDelete.getProperty("reihenfolge");

                        for (final CidsBean b : standortList) {
                            final int reihenfolge = (Integer)b.getProperty("reihenfolge");

                            if (reihenfolge > pos) {
                                b.setProperty("reihenfolge", reihenfolge - 1);
                            }
                        }

                        ((CustomListModel)jlStandort.getModel()).init(cidsBean);
                    }
                } catch (final Exception e) {
                    LOG.error("Cannot remove klima_standort");
                }
            }
        }
    } //GEN-LAST:event_btnRemStandortActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        final Object selection = jlStandort.getSelectedValue();

        if (selection instanceof CidsBean) {
            final Integer pos = (Integer)((CidsBean)selection).getProperty("reihenfolge");
            final List<CidsBean> standortList = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    "klima_standort");

            if (pos >= (standortList.size() - 1)) {
                return;
            }

            for (final CidsBean bean : standortList) {
                final int reihenfolge = (Integer)bean.getProperty("reihenfolge");

                if (reihenfolge == pos) {
                    try {
                        bean.setProperty("reihenfolge", pos + 1);
                    } catch (Exception ex) {
                        LOG.error("Error while changing position", ex);
                    }
                } else if (reihenfolge == (pos + 1)) {
                    try {
                        bean.setProperty("reihenfolge", pos);
                    } catch (Exception ex) {
                        LOG.error("Error while changing position", ex);
                    }
                }
            }
            ((CustomListModel)jlStandort.getModel()).init(cidsBean);
            jlStandort.setSelectedValue(selection, true);
        }
    } //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        final Object selection = jlStandort.getSelectedValue();

        if (selection instanceof CidsBean) {
            final Integer pos = (Integer)((CidsBean)selection).getProperty("reihenfolge");

            if (pos == 0) {
                return;
            }

            final List<CidsBean> standortList = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    "klima_standort");

            for (final CidsBean bean : standortList) {
                final int reihenfolge = (Integer)bean.getProperty("reihenfolge");

                if (reihenfolge == pos) {
                    try {
                        bean.setProperty("reihenfolge", pos - 1);
                    } catch (Exception ex) {
                        LOG.error("Error while changing position", ex);
                    }
                } else if (reihenfolge == (pos - 1)) {
                    try {
                        bean.setProperty("reihenfolge", pos);
                    } catch (Exception ex) {
                        LOG.error("Error while changing position", ex);
                    }
                }
            }
            ((CustomListModel)jlStandort.getModel()).init(cidsBean);
            jlStandort.setSelectedValue(selection, true);
        }
    } //GEN-LAST:event_jButton1ActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;

        if (!editable) {
            RendererTools.makeReadOnly(bindingGroup, "cidsBean");
        }

        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                connectionContext);
            bindingGroup.bind();

            jlStandort.setModel(new CustomListModel(cidsBean));
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        String name = (cidsBean != null) ? (String)cidsBean.getProperty("name") : null;

        if (name == null) {
            name = "unbenannt";
        }

        return "Aussichtspunkt: " + name;
    }

    @Override
    public void setTitle(final String string) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * a JList that implements the CidsBeanDropListener to catch objects of the type klima_standort.
     *
     * @version  $Revision$, $Date$
     */
    private class ImplementationList extends JList implements CidsBeanDropListener {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImplementationList object.
         */
        public ImplementationList() {
            try {
                new CidsBeanDropTarget(this);
            } catch (final Exception ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("error while creating CidsBeanDropTarget", ex); // NOI18N
                }
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void beansDropped(final ArrayList<CidsBean> beans) {
            if (cidsBean != null) {
                for (final CidsBean bean : beans) {
                    if (bean.getClass().getName().equals("de.cismet.cids.dynamics.Klima_standort")) { // NOI18N
                        final List<CidsBean> standortList = CidsBeanSupport.getBeanCollectionFromProperty(
                                cidsBean,
                                "klima_standort");                                                    // NOI18N
                        try {
                            final CidsBean newBean = CidsBeanSupport.createNewCidsBeanFromTableName(
                                    "KLIMAORT_AUSSICHTSPUNKT_KLIMA_STANDORT",
                                    connectionContext);

                            newBean.setProperty("klima_standort", bean);
                            newBean.setProperty("reihenfolge", standortList.size());

                            standortList.add(newBean);
                            ((CustomListModel)jlStandort.getModel()).init(cidsBean);
                        } catch (Exception e) {
                            LOG.error("Cannot create neu KLIMAORT_AUSSICHTSPUNKT_KLIMA_STANDORT", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class CustomListModel implements ListModel<CidsBean> {

        //~ Instance fields ----------------------------------------------------

        private Map<Integer, CidsBean> positionMap = new HashMap<Integer, CidsBean>();
        private List<ListDataListener> listeners = new ArrayList<>();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CustomListModel object.
         *
         * @param  aussichtspunktBean  DOCUMENT ME!
         */
        public CustomListModel(final CidsBean aussichtspunktBean) {
            init(aussichtspunktBean);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  aussichtspunktBean  DOCUMENT ME!
         */
        public void init(final CidsBean aussichtspunktBean) {
            positionMap = new HashMap<Integer, CidsBean>();
            final List<CidsBean> standortList = CidsBeanSupport.getBeanCollectionFromProperty(
                    aussichtspunktBean,
                    "klima_standort");

            for (final CidsBean bean : standortList) {
                positionMap.put((Integer)bean.getProperty("reihenfolge"), bean);
            }

            final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0);

            for (final ListDataListener l : listeners) {
                l.contentsChanged(event);
            }
        }

        @Override
        public int getSize() {
            return positionMap.keySet().size();
        }

        @Override
        public CidsBean getElementAt(final int index) {
            final Integer[] keys = positionMap.keySet().toArray(new Integer[positionMap.keySet().size()]);

            return positionMap.get(keys[index]);
        }

        @Override
        public void addListDataListener(final ListDataListener l) {
            listeners.add(l);
        }

        @Override
        public void removeListDataListener(final ListDataListener l) {
            listeners.remove(l);
        }
    }
}
