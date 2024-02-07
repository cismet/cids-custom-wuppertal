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
/*
 * Alkis_pointRenderer.java
 *
 * Created on 10.09.2009, 15:52:16
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.AbstractAttributeRepresentationFormater;
import Sirius.server.middleware.types.LightweightMetaObject;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import de.aedsicad.aaaweb.rest.model.Buchungsblatt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.AdresseGebaeudeLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class AlkisAdresseRenderer extends javax.swing.JPanel implements CidsBeanRenderer,
    BorderProvider,
    TitleComponentProvider,
    FooterComponentProvider,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlkisAdresseRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private Buchungsblatt buchungsblatt;
    private CidsBean cidsBean;
    private String title;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel blWait;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblBauweise;
    private javax.swing.JLabel lblDescBauweise;
    private javax.swing.JLabel lblDescFlurstuecke;
    private javax.swing.JLabel lblDescFunktion;
    private javax.swing.JLabel lblDescHausnummer;
    private javax.swing.JLabel lblDescLage;
    private javax.swing.JLabel lblDescStockwerkeOber;
    private javax.swing.JLabel lblDescStockwerkeUnter;
    private javax.swing.JLabel lblDescStrasse;
    private javax.swing.JLabel lblFunktion;
    private javax.swing.JLabel lblHausnummer;
    private javax.swing.JLabel lblLage;
    private javax.swing.JLabel lblStockwerkeOber;
    private javax.swing.JLabel lblStockwerkeUnter;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstLandparcels;
    private javax.swing.JPanel panAdresse;
    private javax.swing.JPanel panAdresseContent;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panGebaeude;
    private javax.swing.JPanel panGebaeudeContent;
    private javax.swing.JPanel panTitle;
    private javax.swing.JScrollPane scpLandparcels;
    private de.cismet.tools.gui.SemiRoundedPanel srpHeadAdresse;
    private de.cismet.tools.gui.SemiRoundedPanel srpHeadGebaeude;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Alkis_pointRenderer.
     */
    public AlkisAdresseRenderer() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        blWait.setVisible(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gebaeudeBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MetaObject[] getAllAdressesForSameBuilding(final CidsBean gebaeudeBean) {
        if (gebaeudeBean != null) {
            final Object idObj = gebaeudeBean.getProperty("id");
            if (idObj instanceof Integer) {
                final Integer gebaeudeId = (Integer)idObj;
                try {
                    final AdresseGebaeudeLightweightSearch search = new AdresseGebaeudeLightweightSearch();
                    search.setRepresentationFields(new String[] { "id", "strasse", "nummer" });
                    search.setGebaudeId(gebaeudeId);
                    final Collection<LightweightMetaObject> lwmos = SessionManager.getProxy()
                                .customServerSearch(search, getConnectionContext());
                    for (final LightweightMetaObject lwmo : lwmos) {
                        lwmo.setFormater(new AbstractAttributeRepresentationFormater() {

                                @Override
                                public String getRepresentation() {
                                    final StringBuilder result = new StringBuilder();
                                    result.append(getAttribute("strasse")).append(" ").append(getAttribute("nummer"));
                                    return result.toString();
                                }
                            });
                    }
                    return lwmos.toArray(new MetaObject[0]);
                } catch (final ConnectionException ex) {
                    LOG.error(ex, ex);
                    return null;
                }
            }
        }
        return new MetaObject[0];
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        blWait = new org.jdesktop.swingx.JXBusyLabel();
        panFooter = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        panAdresse = new RoundedPanel();
        srpHeadAdresse = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        panAdresseContent = new RoundedPanel();
        lblDescStrasse = new javax.swing.JLabel();
        lblDescHausnummer = new javax.swing.JLabel();
        lblStrasse = new javax.swing.JLabel();
        lblHausnummer = new javax.swing.JLabel();
        panGebaeude = new RoundedPanel();
        srpHeadGebaeude = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        panGebaeudeContent = new javax.swing.JPanel();
        lblDescFunktion = new javax.swing.JLabel();
        lblDescStockwerkeOber = new javax.swing.JLabel();
        lblFunktion = new javax.swing.JLabel();
        lblStockwerkeOber = new javax.swing.JLabel();
        lblDescBauweise = new javax.swing.JLabel();
        lblBauweise = new javax.swing.JLabel();
        lblDescStockwerkeUnter = new javax.swing.JLabel();
        lblStockwerkeUnter = new javax.swing.JLabel();
        scpLandparcels = new javax.swing.JScrollPane();
        lstLandparcels = new javax.swing.JList();
        lblDescFlurstuecke = new javax.swing.JLabel();
        lblDescLage = new javax.swing.JLabel();
        lblLage = new javax.swing.JLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("TITLE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panTitle.add(lblTitle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        panTitle.add(blWait, gridBagConstraints);

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        jPanel9.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanel9.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanel9.setOpaque(false);
        jPanel9.setPreferredSize(new java.awt.Dimension(40, 40));
        panFooter.add(jPanel9, new java.awt.GridBagConstraints());

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        panAdresse.setOpaque(false);
        panAdresse.setLayout(new java.awt.BorderLayout());

        srpHeadAdresse.setBackground(java.awt.Color.darkGray);
        srpHeadAdresse.setLayout(new java.awt.GridBagLayout());

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Adresse");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpHeadAdresse.add(jLabel1, gridBagConstraints);

        panAdresse.add(srpHeadAdresse, java.awt.BorderLayout.NORTH);

        panAdresseContent.setOpaque(false);
        panAdresseContent.setLayout(new java.awt.GridBagLayout());

        lblDescStrasse.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescStrasse.setText("Straße:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panAdresseContent.add(lblDescStrasse, gridBagConstraints);

        lblDescHausnummer.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescHausnummer.setText("Hausnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panAdresseContent.add(lblDescHausnummer, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"),
                lblStrasse,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAdresseContent.add(lblStrasse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"),
                lblHausnummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAdresseContent.add(lblHausnummer, gridBagConstraints);

        panAdresse.add(panAdresseContent, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panAdresse, gridBagConstraints);

        panGebaeude.setOpaque(false);
        panGebaeude.setLayout(new java.awt.BorderLayout());

        srpHeadGebaeude.setBackground(java.awt.Color.darkGray);
        srpHeadGebaeude.setLayout(new java.awt.GridBagLayout());

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Gebäude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpHeadGebaeude.add(jLabel2, gridBagConstraints);

        panGebaeude.add(srpHeadGebaeude, java.awt.BorderLayout.NORTH);

        panGebaeudeContent.setOpaque(false);
        panGebaeudeContent.setLayout(new java.awt.GridBagLayout());

        lblDescFunktion.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescFunktion.setText("Funktion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panGebaeudeContent.add(lblDescFunktion, gridBagConstraints);

        lblDescStockwerkeOber.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescStockwerkeOber.setText("Oberirdische Geschosse:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panGebaeudeContent.add(lblDescStockwerkeOber, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gebaeude.funktion}"),
                lblFunktion,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeudeContent.add(lblFunktion, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gebaeude.geschosse_oberirdisch}"),
                lblStockwerkeOber,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeudeContent.add(lblStockwerkeOber, gridBagConstraints);

        lblDescBauweise.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescBauweise.setText("Bauweise:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panGebaeudeContent.add(lblDescBauweise, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gebaeude.bauweise}"),
                lblBauweise,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeudeContent.add(lblBauweise, gridBagConstraints);

        lblDescStockwerkeUnter.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescStockwerkeUnter.setText("Unterirdische Geschosse:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panGebaeudeContent.add(lblDescStockwerkeUnter, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gebaeude.geschosse_unterirdisch}"),
                lblStockwerkeUnter,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeudeContent.add(lblStockwerkeUnter, gridBagConstraints);

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.gebaeude.landparcels}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                        this,
                        eLProperty,
                        lstLandparcels);
        jListBinding.setSourceNullValue(null);
        jListBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jListBinding);

        lstLandparcels.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstLandparcelsMouseClicked(evt);
                }
            });
        scpLandparcels.setViewportView(lstLandparcels);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panGebaeudeContent.add(scpLandparcels, gridBagConstraints);

        lblDescFlurstuecke.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescFlurstuecke.setText("Das Gebäude ist auf folgendem Flurstück errichtet:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panGebaeudeContent.add(lblDescFlurstuecke, gridBagConstraints);

        lblDescLage.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescLage.setText("Lage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panGebaeudeContent.add(lblDescLage, gridBagConstraints);

        lblLage.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeudeContent.add(lblLage, gridBagConstraints);

        panGebaeude.add(panGebaeudeContent, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panGebaeude, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstLandparcelsMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstLandparcelsMouseClicked
        if (evt.getClickCount() > 1) {
            final Object selection = lstLandparcels.getSelectedValue();
            if (selection instanceof CidsBean) {
                final CidsBean selBean = (CidsBean)selection;
                final Object jumpID = selBean.getProperty("fullobjectid");
                if (jumpID instanceof Integer) {
                    final String tabname = "alkis_landparcel";
                    final MetaClass mc = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            tabname,
                            getConnectionContext());
                    if (mc != null) {
                        ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObject(mc, (Integer)jumpID, "");
                    } else {
                        LOG.error("Could not find MetaClass for " + tabname);
                    }
                }
            }
        }
    }                                                                              //GEN-LAST:event_lstLandparcelsMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  cb  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cb) {
        bindingGroup.unbind();
        if (cb != null) {
            cidsBean = cb;
            final Object gebaeudeObj = cidsBean.getProperty("gebaeude");
            if (gebaeudeObj instanceof CidsBean) {
                panGebaeude.setVisible(true);
                initLageLabel(getAllAdressesForSameBuilding((CidsBean)gebaeudeObj));
            } else {
                panGebaeude.setVisible(false);
            }
            bindingGroup.bind();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sameBuildingAdresses  DOCUMENT ME!
     */
    private void initLageLabel(final MetaObject[] sameBuildingAdresses) {
        final Map<String, List<String>> multiMap = TypeSafeCollections.newLinkedHashMap();
        for (final MetaObject adressMO : sameBuildingAdresses) {
            if (adressMO instanceof LightweightMetaObject) {
                final LightweightMetaObject lwmo = (LightweightMetaObject)adressMO;
                final Object strasse = lwmo.getLWAttribute("strasse");
                final Object nummer = lwmo.getLWAttribute("nummer");
                if (strasse != null) {
                    List<String> bucket = multiMap.get(strasse.toString());
                    if (bucket == null) {
                        bucket = TypeSafeCollections.newArrayList();
                        multiMap.put(strasse.toString(), bucket);
                    }
                    if (nummer != null) {
                        bucket.add(nummer.toString());
                    }
                }
            }
        }
        final StringBuilder lageTxt = new StringBuilder("<html>");
        int size = multiMap.size();
        for (final Entry<String, List<String>> entry : multiMap.entrySet()) {
            final String strasse = entry.getKey();
            final List<String> nummern = entry.getValue();
            lageTxt.append(strasse);
            if (nummern.size() > 0) {
                lageTxt.append(" ");
                for (int i = 0; i < nummern.size(); ++i) {
                    lageTxt.append(nummern.get(i));
                    if (i < (nummern.size() - 1)) {
                        lageTxt.append(", ");
                    }
                }
            }
            if (--size > 0) {
                lageTxt.append("<br>");
            }
        }
        lageTxt.append("</html>");
        lblLage.setText(lageTxt.toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        } else {
            title = "Adresse " + title;
        }
        this.title = title;
        lblTitle.setText(this.title);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the buchungsblatt
     */
    public Object getBuchungsblatt() {
        return buchungsblatt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  buchungsblatt  the buchungsblatt to set
     */
    public void setBuchungsblatt(final Buchungsblatt buchungsblatt) {
        this.buchungsblatt = buchungsblatt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  waiting  DOCUMENT ME!
     */
    private void setWaiting(final boolean waiting) {
        blWait.setVisible(waiting);
        blWait.setBusy(waiting);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isWaiting() {
        return blWait.isBusy();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getTitleBorder() {
        return new EmptyBorder(10, 10, 10, 10);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getFooterBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getCenterrBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
