/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObjectNode;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.net.URL;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.vzkat.VzkatSchildBeschreibungPanel;
import de.cismet.cids.custom.objecteditors.utils.vzkat.VzkatStandortKartePanel;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VzkatSchildEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    TitleComponentProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VzkatSchildEditor.class);

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler fillerMainBottom;
    private javax.swing.JLabel jLabel1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private javax.swing.JLabel lblBeschreibungTitle;
    private javax.swing.JLabel lblBildTitle;
    private de.cismet.tools.gui.RoundedPanel panBeschreibung;
    private javax.swing.JPanel panBeschreibungBody;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private de.cismet.tools.gui.RoundedPanel panBild;
    private de.cismet.tools.gui.SemiRoundedPanel panBildTitle;
    private javax.swing.JPanel panLageBody1;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle;
    private de.cismet.tools.gui.RoundedPanel panStandortKarte;
    private javax.swing.JPanel panStandortKarteBody;
    private javax.swing.JPanel panTitle;
    private de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel rasterfariDocumentLoaderPanel1;
    private javax.swing.JLabel txtTitle;
    private de.cismet.cids.custom.objecteditors.utils.vzkat.VzkatSchildBeschreibungPanel vzkatSchildPanel;
    private de.cismet.cids.custom.objecteditors.utils.vzkat.VzkatStandortKartePanel vzkatStandortKartePanel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VzkatSchildEditor object.
     */
    public VzkatSchildEditor() {
        this(true);
    }

    /**
     * Creates a new VzkatSchildEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public VzkatSchildEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            "vzkat_schild",
            1,
            800,
            600);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        vzkatStandortKartePanel.initWithConnectionContext(getConnectionContext());
        vzkatSchildPanel.initWithConnectionContext(getConnectionContext());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTitle = new javax.swing.JPanel();
        txtTitle = new javax.swing.JLabel();
        fillerMainBottom = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        panStandortKarte = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        panStandortKarteBody = new javax.swing.JPanel();
        vzkatStandortKartePanel = new VzkatStandortKartePanel(isEditable());
        panBild = new de.cismet.tools.gui.RoundedPanel();
        panBildTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBildTitle = new javax.swing.JLabel();
        panLageBody1 = new javax.swing.JPanel();
        rasterfariDocumentLoaderPanel1 = new RasterfariDocumentLoaderPanel(
                ClientAlkisConf.getInstance().getRasterfariUrl(),
                this,
                getConnectionContext());
        jLabel1 = new javax.swing.JLabel();
        panBeschreibung = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle = new javax.swing.JLabel();
        panBeschreibungBody = new javax.swing.JPanel();
        vzkatSchildPanel = new VzkatSchildBeschreibungPanel(isEditable());

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        txtTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panTitle.add(txtTitle, gridBagConstraints);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(fillerMainBottom, gridBagConstraints);

        panStandortKarte.setLayout(new java.awt.GridBagLayout());

        panLageTitle.setBackground(java.awt.Color.darkGray);
        panLageTitle.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink1,
            org.openide.util.NbBundle.getMessage(VzkatSchildEditor.class, "VzkatSchildEditor.jXHyperlink1.text")); // NOI18N
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle.add(jXHyperlink1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panStandortKarte.add(panLageTitle, gridBagConstraints);

        panStandortKarteBody.setOpaque(false);
        panStandortKarteBody.setLayout(new java.awt.GridBagLayout());

        final javax.swing.GroupLayout vzkatStandortKartePanelLayout = new javax.swing.GroupLayout(
                vzkatStandortKartePanel);
        vzkatStandortKartePanel.setLayout(vzkatStandortKartePanelLayout);
        vzkatStandortKartePanelLayout.setHorizontalGroup(
            vzkatStandortKartePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        vzkatStandortKartePanelLayout.setVerticalGroup(
            vzkatStandortKartePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panStandortKarteBody.add(vzkatStandortKartePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panStandortKarte.add(panStandortKarteBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panStandortKarte, gridBagConstraints);

        panBild.setLayout(new java.awt.GridBagLayout());

        panBildTitle.setBackground(java.awt.Color.darkGray);
        panBildTitle.setLayout(new java.awt.GridBagLayout());

        lblBildTitle.setFont(lblBildTitle.getFont());
        lblBildTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBildTitle,
            org.openide.util.NbBundle.getMessage(VzkatSchildEditor.class, "VzkatSchildEditor.lblBildTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBildTitle.add(lblBildTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBild.add(panBildTitle, gridBagConstraints);

        panLageBody1.setMinimumSize(new java.awt.Dimension(320, 320));
        panLageBody1.setOpaque(false);
        panLageBody1.setPreferredSize(new java.awt.Dimension(320, 320));
        panLageBody1.setLayout(new java.awt.GridBagLayout());

        rasterfariDocumentLoaderPanel1.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody1.add(rasterfariDocumentLoaderPanel1, gridBagConstraints);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(VzkatSchildEditor.class, "VzkatSchildEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLageBody1.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panBild.add(panLageBody1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panBild, gridBagConstraints);

        panBeschreibung.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle.setFont(lblBeschreibungTitle.getFont());
        lblBeschreibungTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBeschreibungTitle,
            org.openide.util.NbBundle.getMessage(
                VzkatSchildEditor.class,
                "VzkatSchildEditor.lblBeschreibungTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle.add(lblBeschreibungTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBeschreibung.add(panBeschreibungTitle, gridBagConstraints);

        panBeschreibungBody.setOpaque(false);
        panBeschreibungBody.setLayout(new java.awt.GridBagLayout());

        vzkatSchildPanel.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibungBody.add(vzkatSchildPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibung.add(panBeschreibungBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panBeschreibung, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        ComponentRegistry.getRegistry()
                .getDescriptionPane()
                .gotoMetaObjectNode(new MetaObjectNode((CidsBean)cidsBean.getProperty("fk_standort")));
    }                                                                                //GEN-LAST:event_jXHyperlink1ActionPerformed
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            txtTitle.setText(getTitle());
        } else {
            txtTitle.setText(null);
        }
        this.cidsBean = cidsBean;
        txtTitle.setText((cidsBean != null) ? getTitle() : null);
        // rasterfariDocumentLoaderPanel1.setDocument(null);

        vzkatStandortKartePanel.setCidsBean((cidsBean != null) ? (CidsBean)cidsBean.getProperty("fk_standort") : null);
        vzkatSchildPanel.setCidsBean(cidsBean);

        try {
            final URL bildURL = new URL("https://i.pinimg.com/236x/b7/95/67/b79567b505f3101a8a58f1d0f6d10687.jpg");
            final BufferedImage originalBild = ImageIO.read(WebAccessManager.getInstance().doRequest(bildURL));
            final int bildZielBreite = (originalBild.getWidth() > originalBild.getHeight()) ? 320 : -1;
            final int bildZielHoehe = (originalBild.getWidth() > originalBild.getHeight()) ? -1 : 320;
            final Image skaliertesBild = originalBild.getScaledInstance(
                    bildZielBreite,
                    bildZielHoehe,
                    Image.SCALE_SMOOTH);
            jLabel1.setIcon(new ImageIcon(skaliertesBild));
        } catch (final Exception ex) {
            LOG.error("Bild konnte nicht geladen werden", ex);
        }
    }

    @Override
    public String getTitle() {
        final String standort = String.valueOf(cidsBean.getProperty("fk_standort"));
        final String position = String.valueOf(cidsBean.getProperty("position"));
        return String.format("<html>Schild in Position <i>%s</i> an Standort <i>%s</i>", position, standort);
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public void dispose() {
        vzkatSchildPanel.dispose();
        vzkatStandortKartePanel.dispose();
        rasterfariDocumentLoaderPanel1.dispose();
    }

    @Override
    public void showMeasureIsLoading() {
    }

    @Override
    public void showMeasurePanel() {
    }
}
