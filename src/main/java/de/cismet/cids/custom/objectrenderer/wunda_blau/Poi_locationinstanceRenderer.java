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
 * Poi_locationinstanceRenderer.java
 *
 * Created on 21.08.2009, 12:02:33
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;

import lombok.Getter;

import org.apache.log4j.Logger;

import java.awt.Cursor;

import java.io.StringReader;

import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

import de.cismet.cids.custom.objectrenderer.converter.CollectionToStringConverter;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Poi_locationinstanceRenderer extends javax.swing.JPanel implements CidsBeanRenderer,
    TitleComponentProvider,
    FooterComponentProvider,
    BorderProvider,
    ConnectionContextStore {

    //~ Instance fields --------------------------------------------------------

    private final Logger log = Logger.getLogger(this.getClass());
    private CidsBean cidsBean;
    private String title;
    private ConnectionContext connectionContext;
    private PoiConfProperties properties;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXHyperlink jXHyperlinkImage;
    private org.jdesktop.swingx.JXHyperlink jXHyperlinkImage1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlinkWebsite;
    private javax.swing.JLabel lblAdresse;
    private javax.swing.JLabel lblAdresseDesc;
    private javax.swing.JLabel lblAlternativ;
    private javax.swing.JLabel lblAlternativnamenDesc;
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JLabel lblFarbe;
    private javax.swing.JLabel lblFarbeDesc;
    private javax.swing.JLabel lblFax;
    private javax.swing.JLabel lblFaxDesc;
    private javax.swing.JLabel lblHaupttyp;
    private javax.swing.JLabel lblHaupttypDesc;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblInfoDesc;
    private javax.swing.JLabel lblInfoDesc1;
    private javax.swing.JLabel lblMail;
    private javax.swing.JLabel lblSignatur;
    private javax.swing.JLabel lblSignatur1;
    private javax.swing.JLabel lblSignatur2;
    private javax.swing.JLabel lblSignatur3;
    private javax.swing.JLabel lblSignatur4;
    private javax.swing.JLabel lblSignaturIcon;
    private javax.swing.JLabel lblSignaturIcon1;
    private javax.swing.JLabel lblSonst;
    private javax.swing.JLabel lblSonstigeTypenDesc;
    private javax.swing.JLabel lblTel;
    private javax.swing.JLabel lblTelefonDesc;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel lblVeranstaltungsarten;
    private javax.swing.JLabel lblVeranstaltungsartenDesc;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panSpacing1;
    private javax.swing.JPanel panSpacing2;
    private javax.swing.JPanel panTitle;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Poi_locationinstanceRenderer.
     */
    public Poi_locationinstanceRenderer() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        this.properties = new PoiConfProperties(connectionContext);

        initComponents();
        ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
            lblMail,
            Cursor.HAND_CURSOR,
            Cursor.DEFAULT_CURSOR);
        ObjectRendererUtils.decorateComponentWithMouseOverCursorChange(
            lblUrl,
            Cursor.HAND_CURSOR,
            Cursor.DEFAULT_CURSOR);
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
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
        panFooter = new javax.swing.JPanel();
        panSpacing1 = new javax.swing.JPanel();
        lblMail = new javax.swing.JLabel();
        lblUrl = new javax.swing.JLabel();
        panSpacing2 = new javax.swing.JPanel();
        panContent = new javax.swing.JPanel();
        lblAdresseDesc = new javax.swing.JLabel();
        lblAdresse = new javax.swing.JLabel();
        lblTelefonDesc = new javax.swing.JLabel();
        lblTel = new javax.swing.JLabel();
        lblFaxDesc = new javax.swing.JLabel();
        lblFax = new javax.swing.JLabel();
        lblInfoDesc1 = new javax.swing.JLabel();
        jXHyperlinkImage1 = new org.jdesktop.swingx.JXHyperlink();
        lblInfoDesc = new javax.swing.JLabel();
        lblInfo = new javax.swing.JLabel();
        lblAlternativnamenDesc = new javax.swing.JLabel();
        lblAlternativ = new javax.swing.JLabel();
        lblHaupttypDesc = new javax.swing.JLabel();
        lblHaupttyp = new javax.swing.JLabel();
        lblSonstigeTypenDesc = new javax.swing.JLabel();
        lblSonst = new javax.swing.JLabel();
        lblVeranstaltungsartenDesc = new javax.swing.JLabel();
        lblVeranstaltungsarten = new javax.swing.JLabel();
        lblSignatur = new javax.swing.JLabel();
        lblSignaturIcon = new javax.swing.JLabel();
        lblFarbeDesc = new javax.swing.JLabel();
        lblFarbe = new javax.swing.JLabel();
        lblSignatur1 = new javax.swing.JLabel();
        jXHyperlinkImage = new org.jdesktop.swingx.JXHyperlink();
        lblSignatur2 = new javax.swing.JLabel();
        jXHyperlinkWebsite = new org.jdesktop.swingx.JXHyperlink();
        lblSignatur3 = new javax.swing.JLabel();
        lblAuthor = new javax.swing.JLabel();
        lblSignatur4 = new javax.swing.JLabel();
        lblSignaturIcon1 = new javax.swing.JLabel();

        panTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("TITLE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panTitle.add(lblTitle, gridBagConstraints);

        panFooter.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 15, 10));
        panFooter.setMinimumSize(new java.awt.Dimension(404, 0));
        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        panSpacing1.setMaximumSize(new java.awt.Dimension(22, 22));
        panSpacing1.setMinimumSize(new java.awt.Dimension(22, 22));
        panSpacing1.setOpaque(false);
        panSpacing1.setPreferredSize(new java.awt.Dimension(22, 22));
        panFooter.add(panSpacing1);

        lblMail.setForeground(new java.awt.Color(51, 153, 255));
        lblMail.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/mail_new.png"))); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.email}"),
                lblMail,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        lblMail.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblMailMouseClicked(evt);
                }
            });
        panFooter.add(lblMail);

        lblUrl.setForeground(new java.awt.Color(51, 153, 255));
        lblUrl.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/html.png"))); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.url}"),
                lblUrl,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        lblUrl.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblUrlMouseClicked(evt);
                }
            });
        panFooter.add(lblUrl);

        panSpacing2.setMaximumSize(new java.awt.Dimension(22, 22));
        panSpacing2.setMinimumSize(new java.awt.Dimension(22, 22));
        panSpacing2.setOpaque(false);
        panSpacing2.setPreferredSize(new java.awt.Dimension(22, 22));
        panFooter.add(panSpacing2);

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        panContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 20));
        panContent.setMinimumSize(new java.awt.Dimension(513, 100));
        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.GridBagLayout());

        lblAdresseDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAdresseDesc.setText("Adresse:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblAdresseDesc, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"),
                lblAdresse,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblAdresse, gridBagConstraints);

        lblTelefonDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTelefonDesc.setText("Telefon:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblTelefonDesc, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.telefon}"),
                lblTel,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblTel, gridBagConstraints);

        lblFaxDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFaxDesc.setText("Fax:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblFaxDesc, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fax}"),
                lblFax,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblFax, gridBagConstraints);

        lblInfoDesc1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblInfoDesc1.setText("Wuppertal-Live URL:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblInfoDesc1, gridBagConstraints);

        jXHyperlinkImage1.setText("-");
        jXHyperlinkImage1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlinkImage1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(jXHyperlinkImage1, gridBagConstraints);

        lblInfoDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.info_art.name}:"),
                lblInfoDesc,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("Info:");
        binding.setSourceUnreadableValue("Info:");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblInfoDesc, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.info}"),
                lblInfo,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblInfo, gridBagConstraints);

        lblAlternativnamenDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAlternativnamenDesc.setText("Zusätzliche Namen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblAlternativnamenDesc, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alternativegeographicidentifier}"),
                lblAlternativ,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        binding.setConverter(new CollectionToStringConverter(
                "alternativegeographicidentifier",
                "<br>",
                "<html>",
                "</html>"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblAlternativ, gridBagConstraints);

        lblHaupttypDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblHaupttypDesc.setText("Hauptthema:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblHaupttypDesc, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.mainlocationtype.identification}"),
                lblHaupttyp,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblHaupttyp, gridBagConstraints);

        lblSonstigeTypenDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSonstigeTypenDesc.setText("Themen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSonstigeTypenDesc, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.locationtypes}"),
                lblSonst,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        binding.setConverter(new CollectionToStringConverter("identification", "<br>", "<html>", "</html>"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSonst, gridBagConstraints);

        lblVeranstaltungsartenDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVeranstaltungsartenDesc.setText("Veranstaltungsarten:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblVeranstaltungsartenDesc, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_veranstaltungsarten}"),
                lblVeranstaltungsarten,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        binding.setConverter(new CollectionToStringConverter("name", "<br>", "<html>", "</html>"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblVeranstaltungsarten, gridBagConstraints);

        lblSignatur.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSignatur.setText("Signatur:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSignatur, gridBagConstraints);

        lblSignaturIcon.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSignaturIcon.setText("---");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSignaturIcon, gridBagConstraints);

        lblFarbeDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFarbeDesc.setText("Farbe:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblFarbeDesc, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.color}"),
                lblFarbe,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblFarbe, gridBagConstraints);

        lblSignatur1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSignatur1.setText("Bild URL:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSignatur1, gridBagConstraints);

        jXHyperlinkImage.setText("-");
        jXHyperlinkImage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlinkImageActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(jXHyperlinkImage, gridBagConstraints);

        lblSignatur2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSignatur2.setText("Webseite des Bildes:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSignatur2, gridBagConstraints);

        jXHyperlinkWebsite.setText("-");
        jXHyperlinkWebsite.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlinkWebsiteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(jXHyperlinkWebsite, gridBagConstraints);

        lblSignatur3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSignatur3.setText("Urheber:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSignatur3, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.urheber_foto}"),
                lblAuthor,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblAuthor, gridBagConstraints);

        lblSignatur4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSignatur4.setText("Signatur:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSignatur4, gridBagConstraints);

        lblSignaturIcon1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSignaturIcon1.setText("---");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSignaturIcon1, gridBagConstraints);

        add(panContent, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblMailMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblMailMouseClicked
        final String email = lblMail.getText();
        if (email.length() > 3) {
            try {
                BrowserLauncher.openURL("mailto:" + email);
            } catch (Exception ex) {
                log.error("Fehler beim \u00D6ffnen der URL: \"mailto:" + email + "\"", ex);
            }
        }
    }                                                                       //GEN-LAST:event_lblMailMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblUrlMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblUrlMouseClicked
        final String url = lblUrl.getText();
        if (url.length() > 3) {
            try {
                BrowserLauncher.openURL(url);
            } catch (Exception ex) {
                log.error("Fehler beim \u00D6ffnen der URL: " + url + "", ex);
            }
        }
    }                                                                      //GEN-LAST:event_lblUrlMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlinkWebsiteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlinkWebsiteActionPerformed
        final String site = (String)cidsBean.getProperty("fotostrecke");
        if (site != null) {
            try {
                BrowserLauncher.openURL(site);
            } catch (Exception ex) {
                final String message = "Fehler beim Öffnen der Fotostrecke.";
                log.error(message, ex);
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    message,
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                                                                      //GEN-LAST:event_jXHyperlinkWebsiteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlinkImageActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlinkImageActionPerformed
        final String foto = (String)cidsBean.getProperty("foto");
        if (foto != null) {
            try {
                BrowserLauncher.openURL(foto);
            } catch (Exception ex) {
                final String message = "Fehler beim Öffnen des Fotos.";
                log.error(message, ex);
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    message,
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                                                                    //GEN-LAST:event_jXHyperlinkImageActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlinkImage1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlinkImage1ActionPerformed
        final Integer wupLiveId = (Integer)cidsBean.getProperty("wup_live_id");
        if (wupLiveId != null) {
            try {
                BrowserLauncher.openURL(String.format(properties.getWupLiveIdUrlTemplate(), wupLiveId));
            } catch (Exception ex) {
                final String message = "Fehler beim Öffnen der Wuppertal-Live ID.";
                log.error(message, ex);
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    message,
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                                                                     //GEN-LAST:event_jXHyperlinkImage1ActionPerformed

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
     *
     * @param   property   DOCUMENT ME!
     * @param   maxLength  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getStartOfProperty(final String property, final int maxLength) {
        try {
            return getStartOfString((String)cidsBean.getProperty(property), maxLength);
        } catch (final Exception e) {
            return "-";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s          DOCUMENT ME!
     * @param   maxLength  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getStartOfString(final String s, final int maxLength) {
        if (s.length() > maxLength) {
            return s.substring(0, maxLength) + "...";
        } else {
            return s;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getImageUrlStart() {
        return getStartOfProperty("foto", 120);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getSiteUrlStart() {
        return getStartOfProperty("fotostrecke", 120);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            bindingGroup.bind();

            if (lblMail.getText().length() < 3) {
                lblMail.setVisible(false);
            }
            if (lblUrl.getText().length() < 3) {
                lblUrl.setVisible(false);
            }
            final Icon sig = PoiTools.getPoiSignatureIcon(cidsBean);
            if (sig != null) {
                lblSignaturIcon.setIcon(sig);
                lblSignaturIcon.setText("");
            }

            jXHyperlinkImage.setText(getImageUrlStart());
            jXHyperlinkWebsite.setText(getSiteUrlStart());

            final Integer wupLiveId = (Integer)cidsBean.getProperty("wup_live_id");
            final String wupLiveIdUrl;
            if (wupLiveId == null) {
                wupLiveIdUrl = "-";
            } else {
                wupLiveIdUrl = String.format(properties.getWupLiveIdUrlTemplate(), wupLiveId);
            }
            jXHyperlinkImage1.setText(wupLiveIdUrl);

            boolean hasVeranstaltungsortType = false;
            if ((cidsBean.getProperty("mainlocationtype.number") != null)
                        && ((Integer)cidsBean.getProperty("mainlocationtype.number") == 12)) {
                hasVeranstaltungsortType = true;
            } else {
                for (final CidsBean typeBean : cidsBean.getBeanCollectionProperty("locationtypes")) {
                    final Integer number = (Integer)typeBean.getProperty("number");
                    if ((number != null) && (number == 12)) {
                        hasVeranstaltungsortType = true;
                        break;
                    }
                }
            }
            lblVeranstaltungsarten.setVisible(hasVeranstaltungsortType);
            lblVeranstaltungsartenDesc.setVisible(hasVeranstaltungsortType);

            lblFarbeDesc.setVisible(cidsBean.getProperty("color") != null);
            lblFarbe.setVisible(cidsBean.getProperty("color") != null);
        }
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
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getTitleBorder() {
        return BorderFactory.createEmptyBorder();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getFooterBorder() {
        return BorderFactory.createEmptyBorder();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getCenterrBorder() {
        return BorderFactory.createEmptyBorder();
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    static class PoiConfProperties {

        //~ Instance fields ----------------------------------------------------

        private final String wupLiveIdUrlTemplate;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VermessungsunterlagenProperties object.
         *
         * @param  connectionContext  properties DOCUMENT ME!
         */
        public PoiConfProperties(final ConnectionContext connectionContext) {
            String wupLiveIdUrlTemplate = null;
            try {
                final Object ret = SessionManager.getSession()
                            .getConnection()
                            .executeTask(SessionManager.getSession().getUser(),
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.POI_CONF_PROPERTIES.getValue(),
                                connectionContext);
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }
                final Properties properties = new Properties();
                properties.load(new StringReader((String)ret));

                wupLiveIdUrlTemplate = properties.getProperty("WUP_LIVE_ID_URL_TEMPLATE", null);
            } catch (final Exception ex) {
            }
            this.wupLiveIdUrlTemplate = wupLiveIdUrlTemplate;
        }
    }
}
