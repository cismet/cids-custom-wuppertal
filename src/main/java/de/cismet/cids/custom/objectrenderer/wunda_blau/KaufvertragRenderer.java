/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import org.apache.commons.lang.StringUtils;

import org.jdesktop.beansbinding.Converter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.tools.gui.RoundedPanel;

/**
 * *
 *
 * <p>Renderer speziell fuer Kaufvertraege und deren Unterklassen.</p>
 *
 * @author   nhaffke
 * @version  $Revision$, $Date$
 */
public class KaufvertragRenderer extends JPanel implements CidsBeanRenderer, RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final String VERTRAG = "Vertrag";

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KaufvertragRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final Converter<Timestamp, Date> timeStampConverter = new Converter<Timestamp, Date>() {

            @Override
            public Date convertForward(final Timestamp value) {
                try {
                    if (value != null) {
                        return new java.util.Date(value.getTime());
                    } else {
                        return null;
                    }
                } catch (Exception ex) {
                    LOG.error("Problem during Timestamp vonversion. Will return now().", ex);
                    return new java.util.Date(System.currentTimeMillis());
                }
            }

            @Override
            public Timestamp convertReverse(final Date value) {
                try {
                    if (value != null) {
                        return new Timestamp(value.getTime());
                    } else {
                        return null;
                    }
                } catch (Exception ex) {
                    LOG.error("Problem during Timestamp vonversion. Will return now().", ex);
                    return new Timestamp(System.currentTimeMillis());
                }
            }
        };

    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.editors.DefaultBindableDateChooser dpVerkaufsdatum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAdresse;
    private javax.swing.JLabel lblAuswertung;
    private javax.swing.JLabel lblGesamtflaeche;
    private javax.swing.JLabel lblGesamtteilflaeche;
    private javax.swing.JLabel lblKaufpreisAbs;
    private javax.swing.JLabel lblKaufpreisQm;
    private javax.swing.JLabel lblObjektart;
    private javax.swing.JLabel lblRegBez;
    private javax.swing.JLabel lblRegJahr;
    private javax.swing.JLabel lblSachKaufpreis;
    private javax.swing.JLabel lblSachKaufpreisRoh;
    private javax.swing.JLabel lblSachMarktanp;
    private javax.swing.JLabel lblSachRohertrag;
    private javax.swing.JLabel lblSachZinssatz;
    private javax.swing.JLabel lblSachwerte;
    private javax.swing.JLabel lblTeilmarkt;
    private javax.swing.JPanel panFlurstuecke;
    private javax.swing.JPanel panGebaeude;
    private javax.swing.JPanel panMain;
    private de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel panPreviewMap;
    private javax.swing.JPanel panSachwerte;
    private javax.swing.JPanel panTeileigentum;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KaufvertragRenderer object.
     */
    public KaufvertragRenderer() {
        initComponents();
        RendererTools.jxDatePickerShouldLookLikeLabel(dpVerkaufsdatum);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void createSuppanels() {
        final List<CidsBean> flurstuecke = cidsBean.getBeanCollectionProperty("flurstuecke");
        if ((flurstuecke != null) && (!flurstuecke.isEmpty())) {
            final int anzahl = flurstuecke.size();
            if ((anzahl % 2) == 0) {
                panFlurstuecke.setLayout(new GridLayout(anzahl / 2, 2, 5, 5));
            } else {
                panFlurstuecke.setLayout(new GridLayout((anzahl + 1) / 2, 2, 5, 5));
            }
            for (final CidsBean flurstueck : flurstuecke) {
                panFlurstuecke.add(createFlurstueckPanel(flurstueck));
            }
        } else {
            panFlurstuecke.setVisible(false);
            jLabel18.setVisible(false);
        }

        final List<CidsBean> gebaeudeList = cidsBean.getBeanCollectionProperty("gebauede");
        if ((gebaeudeList != null) && (!gebaeudeList.isEmpty())) {
            final int anzahl = gebaeudeList.size();
            if ((anzahl % 2) == 0) {
                panGebaeude.setLayout(new GridLayout(anzahl / 2, 2, 5, 5));
            } else {
                panGebaeude.setLayout(new GridLayout((anzahl + 1) / 2, 2, 5, 5));
            }
            for (final CidsBean gebaeude : gebaeudeList) {
                panGebaeude.add(createGebaeudePanel(gebaeude));
            }
        } else {
            panGebaeude.setVisible(false);
            jLabel19.setVisible(false);
        }

        final List<CidsBean> teileigentume = cidsBean.getBeanCollectionProperty("teileigentum");
        if ((teileigentume != null) && (!teileigentume.isEmpty())) {
            final int anzahl = teileigentume.size();
            if ((anzahl % 2) == 0) {
                panTeileigentum.setLayout(new GridLayout(anzahl / 2, 2, 5, 5));
            } else {
                panTeileigentum.setLayout(new GridLayout((anzahl + 1) / 2, 2, 5, 5));
            }
            for (final CidsBean teileigentum : teileigentume) {
                panTeileigentum.add(createTeileigentumPanel(teileigentum));
            }
        } else {
            panTeileigentum.setVisible(false);
            jLabel20.setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        panPreviewMap = new de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel();
        panMain = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblSachwerte = new javax.swing.JLabel();
        lblRegBez = new javax.swing.JLabel();
        lblRegJahr = new javax.swing.JLabel();
        lblAdresse = new javax.swing.JLabel();
        lblObjektart = new javax.swing.JLabel();
        lblTeilmarkt = new javax.swing.JLabel();
        lblGesamtflaeche = new javax.swing.JLabel();
        lblGesamtteilflaeche = new javax.swing.JLabel();
        lblKaufpreisQm = new javax.swing.JLabel();
        lblKaufpreisAbs = new javax.swing.JLabel();
        lblAuswertung = new javax.swing.JLabel();
        panSachwerte = new RoundedPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblSachKaufpreis = new javax.swing.JLabel();
        lblSachRohertrag = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblSachKaufpreisRoh = new javax.swing.JLabel();
        lblSachMarktanp = new javax.swing.JLabel();
        lblSachZinssatz = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        panFlurstuecke = new javax.swing.JPanel();
        panGebaeude = new javax.swing.JPanel();
        panTeileigentum = new javax.swing.JPanel();
        dpVerkaufsdatum = new de.cismet.cids.editors.DefaultBindableDateChooser();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel1.add(panPreviewMap, gridBagConstraints);

        panMain.setOpaque(false);
        panMain.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Reg_Bez:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Reg_Jahr:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Verkaufsdatum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Adresse:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Objektart:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Teilmarkt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Gesamtfläche:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Gesamtteilfläche:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Kaufpreis pro m²:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel9, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Zur Auswertung geeignet:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel11, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Kaufpreis absolut:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel10, gridBagConstraints);

        lblSachwerte.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSachwerte.setText("Sachwerte:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(lblSachwerte, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.reg_bez}"),
                lblRegBez,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblRegBez, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.reg_jahr}"),
                lblRegJahr,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblRegJahr, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.adresse}"),
                lblAdresse,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblAdresse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.objektart}"),
                lblObjektart,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblObjektart, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.teilmarkt}"),
                lblTeilmarkt,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblTeilmarkt, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gesamtflaeche}"),
                lblGesamtflaeche,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblGesamtflaeche, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gesamtteilflaeche}"),
                lblGesamtteilflaeche,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblGesamtteilflaeche, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kaufpreis_pro_qm}"),
                lblKaufpreisQm,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblKaufpreisQm, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kaufpreis_absolut}"),
                lblKaufpreisAbs,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblKaufpreisAbs, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zur_auswertunng_geeignet}"),
                lblAuswertung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(lblAuswertung, gridBagConstraints);

        panSachwerte.setOpaque(false);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Rohertrag:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Bereinigter Kaufpreis:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sachwerte.bereinigter_kaufpreis_wf_nf}"),
                lblSachKaufpreis,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sachwerte.rohertrag}"),
                lblSachRohertrag,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Bereinigter Kaufpreis Rohertrag:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("Marktanpassungsfaktor:");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Liegenschaftszinssatz:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sachwerte.bereinigter_kaufpreis_rohertrag}"),
                lblSachKaufpreisRoh,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sachwerte.marktanpassungsfaktor}"),
                lblSachMarktanp,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sachwerte.liegenschaftszinssatz}"),
                lblSachZinssatz,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        final javax.swing.GroupLayout panSachwerteLayout = new javax.swing.GroupLayout(panSachwerte);
        panSachwerte.setLayout(panSachwerteLayout);
        panSachwerteLayout.setHorizontalGroup(
            panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panSachwerteLayout.createSequentialGroup().addContainerGap().addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel13).addComponent(jLabel14).addComponent(jLabel15).addComponent(jLabel16).addComponent(
                        jLabel17)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        lblSachKaufpreis).addComponent(lblSachRohertrag).addComponent(lblSachKaufpreisRoh).addComponent(
                        lblSachMarktanp).addComponent(lblSachZinssatz)).addContainerGap(105, Short.MAX_VALUE)));
        panSachwerteLayout.setVerticalGroup(
            panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panSachwerteLayout.createSequentialGroup().addContainerGap().addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel13).addComponent(lblSachRohertrag)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel14).addComponent(lblSachKaufpreis)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel15).addComponent(lblSachKaufpreisRoh)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel16).addComponent(lblSachMarktanp)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel17).addComponent(lblSachZinssatz)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 31;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 32);
        panMain.add(panSachwerte, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Flurstücke:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel18, gridBagConstraints);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Gebäude:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel19, gridBagConstraints);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Teileigentum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 20);
        panMain.add(jLabel20, gridBagConstraints);

        panFlurstuecke.setOpaque(false);

        final javax.swing.GroupLayout panFlurstueckeLayout = new javax.swing.GroupLayout(panFlurstuecke);
        panFlurstuecke.setLayout(panFlurstueckeLayout);
        panFlurstueckeLayout.setHorizontalGroup(
            panFlurstueckeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                234,
                Short.MAX_VALUE));
        panFlurstueckeLayout.setVerticalGroup(
            panFlurstueckeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 78;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        panMain.add(panFlurstuecke, gridBagConstraints);

        panGebaeude.setOpaque(false);

        final javax.swing.GroupLayout panGebaeudeLayout = new javax.swing.GroupLayout(panGebaeude);
        panGebaeude.setLayout(panGebaeudeLayout);
        panGebaeudeLayout.setHorizontalGroup(
            panGebaeudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                258,
                Short.MAX_VALUE));
        panGebaeudeLayout.setVerticalGroup(
            panGebaeudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 86;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        panMain.add(panGebaeude, gridBagConstraints);

        panTeileigentum.setOpaque(false);

        final javax.swing.GroupLayout panTeileigentumLayout = new javax.swing.GroupLayout(panTeileigentum);
        panTeileigentum.setLayout(panTeileigentumLayout);
        panTeileigentumLayout.setHorizontalGroup(
            panTeileigentumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                291,
                Short.MAX_VALUE));
        panTeileigentumLayout.setVerticalGroup(
            panTeileigentumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 97;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 17, 0);
        panMain.add(panTeileigentum, gridBagConstraints);

        dpVerkaufsdatum.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verkaufsdatum.fromts}"),
                dpVerkaufsdatum,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(timeStampConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panMain.add(dpVerkaufsdatum, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        jPanel1.add(panMain, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            panPreviewMap.initMap(cidsBean, "umschreibendes_rechteck.geo_field");
            bindingGroup.bind();
            createSuppanels();
        }
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
    }

    @Override
    public String getTitle() {
        String title = "";
        final String name = (String)cidsBean.getProperty("reg_bez");
        if (StringUtils.isNotBlank(name)) {
            final String vertragstyp = (String)cidsBean.getProperty("vertragstyp");
            if (!vertragstyp.equals("")) {
                title = vertragstyp + " - " + name;
            } else {
                title = VERTRAG + " - " + name;
            }
        } else {
            title = VERTRAG;
        }

        return title;
    }

    @Override
    public void setTitle(final String title) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  i DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private RoundedPanel createFlurstueckPanel(final CidsBean flurstueck) {
        int c = 6;
        final RoundedPanel round = new RoundedPanel();
        round.setLayout(new FlowLayout(FlowLayout.LEADING));
        round.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final JPanel pnlFlurstueck = new JPanel();
        pnlFlurstueck.setOpaque(false);
        pnlFlurstueck.setLayout(new GridLayout(6, 2, 20, 5));

        final JLabel gemName = new JLabel();
        gemName.setFont(new Font("Tahoma", 1, 11));
        gemName.setText("Gemarkungsname:");

        final JLabel gemNr = new JLabel();
        gemNr.setFont(new Font("Tahoma", 1, 11));
        gemNr.setText("Gemarkungsnr:");

        final JLabel lblFlur = new JLabel();
        lblFlur.setFont(new Font("Tahoma", 1, 11));
        lblFlur.setText("Flur:");

        final JLabel flurstZN = new JLabel();
        flurstZN.setFont(new Font("Tahoma", 1, 11));
        flurstZN.setText("Z\u00E4hler / Nenner:");

        final JLabel flaeche = new JLabel();
        flaeche.setFont(new Font("Tahoma", 1, 11));
        flaeche.setText("Fl\u00E4che:");

        final JLabel teilflaeche = new JLabel();
        teilflaeche.setFont(new Font("Tahoma", 1, 11));
        teilflaeche.setText("Teilfl\u00E4che:");

        final String gemarkungsname = (String)flurstueck.getProperty("gemarkungsname");
        if (StringUtils.isNotBlank(gemarkungsname)) {
            pnlFlurstueck.add(gemName);
            pnlFlurstueck.add(new JLabel(gemarkungsname));
        } else {
            ((GridLayout)pnlFlurstueck.getLayout()).setRows(--c);
        }

        final String gemarkungsNr = (String)flurstueck.getProperty("gemarkungs_nr");
        if ((StringUtils.isNotBlank(gemarkungsNr) && !"null".equals(gemarkungsNr))) {
            pnlFlurstueck.add(gemNr);
            pnlFlurstueck.add(new JLabel(gemarkungsNr));
        } else {
            ((GridLayout)pnlFlurstueck.getLayout()).setRows(--c);
        }

        final String flur = (String)flurstueck.getProperty("flur");
        if (StringUtils.isNotBlank(flur)) {
            pnlFlurstueck.add(lblFlur);
            pnlFlurstueck.add(new JLabel(flur));
        } else {
            ((GridLayout)pnlFlurstueck.getLayout()).setRows(--c);
        }

        final String fstnr_z = (String)flurstueck.getProperty("fstnr_z");
        final String fstnr_n = (String)flurstueck.getProperty("fstnr_n");
        if (StringUtils.isNotBlank(fstnr_z)) {
            if (StringUtils.isNotBlank(fstnr_n)) {
                pnlFlurstueck.add(flurstZN);
                pnlFlurstueck.add(new JLabel(fstnr_z + "/" + fstnr_n));
            } else {
                pnlFlurstueck.add(flurstZN);
                pnlFlurstueck.add(new JLabel(fstnr_z));
            }
        } else {
            ((GridLayout)pnlFlurstueck.getLayout()).setRows(--c);
        }

        final Float flaecheFlurstueck = (Float)flurstueck.getProperty("flaeche_flurstueck");
        if ((flaecheFlurstueck != null) && (flaecheFlurstueck > -1.0f)) {
            pnlFlurstueck.add(flaeche);
            pnlFlurstueck.add(new JLabel(flaecheFlurstueck.toString()));
        } else {
            ((GridLayout)pnlFlurstueck.getLayout()).setRows(--c);
        }

        final Float teilflaecheFlurstueck = (Float)flurstueck.getProperty("teilflaeche_flurstueck");
        if ((teilflaecheFlurstueck != null) && (teilflaecheFlurstueck > -1.0f)) {
            pnlFlurstueck.add(teilflaeche);
            pnlFlurstueck.add(new JLabel(teilflaecheFlurstueck.toString()));
        } else {
            ((GridLayout)pnlFlurstueck.getLayout()).setRows(--c);
        }

        round.add(pnlFlurstueck, BorderLayout.CENTER);
        return round;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gebaeude  i DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private RoundedPanel createGebaeudePanel(final CidsBean gebaeude) {
        int c = 5;
        final RoundedPanel round = new RoundedPanel();
        round.setLayout(new FlowLayout(FlowLayout.LEADING));
        round.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final JPanel geb = new JPanel();
        geb.setOpaque(false);
        geb.setLayout(new GridLayout(c, 2, 20, 5));

        final JLabel bez = new JLabel();
        bez.setFont(new Font("Tahoma", 1, 11));
        bez.setText("Bezeichnung:");

        final JLabel lblBaujahr = new JLabel();
        lblBaujahr.setFont(new Font("Tahoma", 1, 11));
        lblBaujahr.setText("Baujahr:");

        final JLabel wf = new JLabel();
        wf.setFont(new Font("Tahoma", 1, 11));
        wf.setText("Wohnfl\u00E4che:");

        final JLabel nf = new JLabel();
        nf.setFont(new Font("Tahoma", 1, 11));
        nf.setText("Nutzfl\u00E4che:");

        final JLabel wf_nf = new JLabel();
        wf_nf.setFont(new Font("Tahoma", 1, 11));
        wf_nf.setText("Wohnfl\u00E4che/Nutzfl\u00E4che:");

        final String bezeichnung = (String)gebaeude.getProperty("bezeichnung");
        if (StringUtils.isNotBlank(bezeichnung)) {
            geb.add(bez);
            geb.add(new JLabel(bezeichnung));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }

        final Integer baujahr = (Integer)gebaeude.getProperty("baujahr");
        if ((baujahr != null) && (baujahr > -1)) {
            geb.add(lblBaujahr);
            geb.add(new JLabel(baujahr.toString()));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }

        final Float wfGebaeudeQm = (Float)gebaeude.getProperty("wf_gebaeude_qm");
        if ((wfGebaeudeQm != null) && (wfGebaeudeQm > -1.0f)) {
            geb.add(wf);
            geb.add(new JLabel(wfGebaeudeQm.toString()));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }

        final Float nfGebaeudeQm = (Float)gebaeude.getProperty("nf_gebaeude_qm");
        if ((nfGebaeudeQm != null) && (nfGebaeudeQm > -1.0f)) {
            geb.add(nf);
            geb.add(new JLabel(nfGebaeudeQm.toString()));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }

        final Float wfNf = (Float)gebaeude.getProperty("wf_nf");
        if ((wfNf != null) && (wfNf > -1.0f)) {
            geb.add(wf_nf);
            geb.add(new JLabel(wfNf.toString()));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }

        round.add(geb, BorderLayout.CENTER);

        return round;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   teileigentum  i DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private RoundedPanel createTeileigentumPanel(final CidsBean teileigentum) {
        int c = 8;
        final RoundedPanel round = new RoundedPanel();
        round.setLayout(new FlowLayout(FlowLayout.LEADING));
        round.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final JPanel teil = new JPanel();
        teil.setOpaque(false);
        teil.setLayout(new GridLayout(c, 2, 20, 5));

        final JLabel geschosse = new JLabel();
        geschosse.setFont(new Font("Tahoma", 1, 11));
        geschosse.setText("Anzahl Geschosse:");

        final JLabel kaufpreis = new JLabel();
        kaufpreis.setFont(new Font("Tahoma", 1, 11));
        kaufpreis.setText("Rel. Kaufpreis:");

        final JLabel tmarkt = new JLabel();
        tmarkt.setFont(new Font("Tahoma", 1, 11));
        tmarkt.setText("Teilmarkt:");

        final JLabel vermietung = new JLabel();
        vermietung.setFont(new Font("Tahoma", 1, 11));
        vermietung.setText("Vermietungssituation:");

        final JLabel ausstattung = new JLabel();
        ausstattung.setFont(new Font("Tahoma", 1, 11));
        ausstattung.setText("Ausstattungsstandard:");

        final JLabel modernisierung = new JLabel();
        modernisierung.setFont(new Font("Tahoma", 1, 11));
        modernisierung.setText("Modernisierungsjahr:");

        final JLabel lblRaumzahl = new JLabel();
        lblRaumzahl.setFont(new Font("Tahoma", 1, 11));
        lblRaumzahl.setText("Raumanzahl:");

        final JLabel lblWohnlage = new JLabel();
        lblWohnlage.setFont(new Font("Tahoma", 1, 11));
        lblWohnlage.setText("Wohnlage:");

        final Integer anzahlGeschosse = (Integer)teileigentum.getProperty("anzahl_geschosse");
        if ((anzahlGeschosse != null) && (anzahlGeschosse > -1)) {
            teil.add(geschosse);
            teil.add(new JLabel(anzahlGeschosse.toString()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }

        final Float rel_kaufpreis = (Float)teileigentum.getProperty("rel_kaufpreis");
        if ((rel_kaufpreis != null) && (rel_kaufpreis > -1.0f)) {
            teil.add(kaufpreis);
            teil.add(new JLabel(rel_kaufpreis.toString()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }

        final String teilmarkt = (String)teileigentum.getProperty("teilmarkt");
        if (StringUtils.isNotBlank(teilmarkt)) {
            teil.add(tmarkt);
            teil.add(new JLabel(teilmarkt.trim()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }

        final String vermietungssituation = (String)teileigentum.getProperty("vermietungssituation");
        if (StringUtils.isNotBlank(vermietungssituation) && !"null".equals(vermietungssituation)) {
            teil.add(vermietung);
            teil.add(new JLabel(vermietungssituation));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }

        final String ausstattungsstandard = (String)teileigentum.getProperty("ausstattungstsandard");
        if (StringUtils.isNotBlank(ausstattungsstandard) && !"null".equals(ausstattungsstandard)) {
            teil.add(ausstattung);
            teil.add(new JLabel(ausstattungsstandard));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }

        final Integer modernisierungsjahr = (Integer)teileigentum.getProperty("modernisierungsjahr");
        if ((modernisierungsjahr != null) && (modernisierungsjahr > -1)) {
            teil.add(modernisierung);
            teil.add(new JLabel(modernisierungsjahr.toString()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }

        final Integer raumzahl = (Integer)teileigentum.getProperty("raumzahl");
        if ((raumzahl != null) && (raumzahl > -1)) {
            teil.add(lblRaumzahl);
            teil.add(new JLabel(raumzahl.toString()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }

        final String wohnlage = (String)teileigentum.getProperty("wohnlage");
        if (StringUtils.isNotBlank(wohnlage) && !"null".equals(wohnlage)) {
            teil.add(lblWohnlage);
            teil.add(new JLabel(wohnlage));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }

        round.add(teil, BorderLayout.CENTER);

        return round;
    }
}
