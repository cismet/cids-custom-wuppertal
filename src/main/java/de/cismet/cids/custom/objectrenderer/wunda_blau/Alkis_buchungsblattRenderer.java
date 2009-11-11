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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.aedsicad.aaaweb.service.alkis.info.ALKISInfoServices;
import de.aedsicad.aaaweb.service.util.Buchungsblatt;
import de.aedsicad.aaaweb.service.util.Offices;
import de.aedsicad.aaaweb.service.util.Owner;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUIUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisCommons;
import de.cismet.cids.custom.objectrenderer.utils.alkis.SOAPAccessProvider;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;
import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.featureservice.DefaultFeatureServiceFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;
import de.cismet.tools.CismetThreadPool;
import de.cismet.tools.collections.TypeSafeCollections;
import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.TitleComponentProvider;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.jdesktop.swingbinding.JListBinding;

/**
 *
 * @author srichter
 */
public class Alkis_buchungsblattRenderer extends javax.swing.JPanel implements CidsBeanRenderer, BorderProvider, TitleComponentProvider, FooterComponentProvider {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Alkis_buchungsblattRenderer.class);
    private final List<LightweightLandParcel> landParcelList;
    private final MappingComponent map;
    private RetrieveWorker retrieveWorker;
    private SOAPAccessProvider soapProvider;
    private ALKISInfoServices infoService;
    private Buchungsblatt buchungsblatt;
    private CidsBean cidsBean;
    private String title;
    private final JListBinding landparcelListBinding;
    private Map<LightweightLandParcel, DefaultStyledFeature> landParcelFeatureMap;

    /** Creates new form Alkis_pointRenderer */
    public Alkis_buchungsblattRenderer() {
        map = new MappingComponent();
        landParcelFeatureMap = TypeSafeCollections.newHashMap();
        map.setOpaque(false);
        landParcelList = TypeSafeCollections.newArrayList();
        try {
            soapProvider = new SOAPAccessProvider();
            infoService = soapProvider.getAlkisInfoService();
        } catch (Exception ex) {
            log.fatal(ex, ex);
        }
        initComponents();
        jScrollPane1.getViewport().setOpaque(false);
        jScrollPane2.getViewport().setOpaque(false);
        panKarte.add(map, BorderLayout.CENTER);
        initEditorPanes();
        lstLandparcels.setCellRenderer(new FancyListCellRenderer());
        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${landParcelList}");
        landparcelListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, eLProperty, lstLandparcels);
        landparcelListBinding.setSourceNullValue(null);
        landparcelListBinding.setSourceUnreadableValue(null);

    }

    private final void initEditorPanes() {
        //Font and Layout
        final Font font = UIManager.getFont("Label.font");
        final String bodyRule = "body { font-family: " + font.getFamily() + "; " +
                "font-size: " + font.getSize() + "pt; }";
        final String tableRule = "td { padding-right : 15px; }";
        final String tableHeadRule = "th { padding-right : 15px; }";
        final StyleSheet css = ((HTMLEditorKit) epOwner.getEditorKit()).getStyleSheet();

        css.addRule(bodyRule);
        css.addRule(tableRule);
        css.addRule(tableHeadRule);

        //Change scroll behaviour: avoid autoscrolls on setText(...)
        final Caret caret = epOwner.getCaret();
        if (caret instanceof DefaultCaret) {
            final DefaultCaret dCaret = (DefaultCaret) caret;
            dCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        blWait = new org.jdesktop.swingx.JXBusyLabel();
        panFooter = new javax.swing.JPanel();
        panContent = new RoundedPanel();
        jPanel1 = new javax.swing.JPanel();
        lblDescKatasteramt = new javax.swing.JLabel();
        lblDescAmtsgericht = new javax.swing.JLabel();
        lblDescGrundbuchbezirk = new javax.swing.JLabel();
        lblAmtgericht = new javax.swing.JLabel();
        lblGrundbuchbezirk = new javax.swing.JLabel();
        lblKatasteramt = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeadMainInfo = new javax.swing.JLabel();
        panProdukte = new RoundedPanel();
        jPanel2 = new javax.swing.JPanel();
        hlBestandsnachweisPdf = new org.jdesktop.swingx.JXHyperlink();
        hlBestandsnachweisHtml = new org.jdesktop.swingx.JXHyperlink();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeadProdukte = new javax.swing.JLabel();
        panEigentuemer = new RoundedPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        epOwner = new javax.swing.JEditorPane();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeadEigentuemer = new javax.swing.JLabel();
        panGrundstuecke = new RoundedPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstLandparcels = new javax.swing.JList();
        semiRoundedPanel4 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeadFlurstuecke = new javax.swing.JLabel();
        panKarte = new javax.swing.JPanel();

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

        setLayout(new java.awt.GridBagLayout());

        panContent.setLayout(new java.awt.BorderLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblDescKatasteramt.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescKatasteramt.setText("Katasteramt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanel1.add(lblDescKatasteramt, gridBagConstraints);

        lblDescAmtsgericht.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescAmtsgericht.setText("Amtsgericht:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        jPanel1.add(lblDescAmtsgericht, gridBagConstraints);

        lblDescGrundbuchbezirk.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescGrundbuchbezirk.setText("Grundbuchbezirk:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanel1.add(lblDescGrundbuchbezirk, gridBagConstraints);

        lblAmtgericht.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanel1.add(lblAmtgericht, gridBagConstraints);

        lblGrundbuchbezirk.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblGrundbuchbezirk, gridBagConstraints);

        lblKatasteramt.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblKatasteramt, gridBagConstraints);

        jPanel5.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel6.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jPanel6, gridBagConstraints);

        panContent.add(jPanel1, java.awt.BorderLayout.CENTER);

        semiRoundedPanel2.setBackground(Color.DARK_GRAY);
        semiRoundedPanel2.setLayout(new java.awt.GridBagLayout());

        lblHeadMainInfo.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadMainInfo.setText("Buchungsblatt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel2.add(lblHeadMainInfo, gridBagConstraints);

        panContent.add(semiRoundedPanel2, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panContent, gridBagConstraints);

        panProdukte.setLayout(new java.awt.BorderLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        hlBestandsnachweisPdf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        hlBestandsnachweisPdf.setText("Bestandsnachweis PDF");
        hlBestandsnachweisPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hlBestandsnachweisPdfActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        jPanel2.add(hlBestandsnachweisPdf, gridBagConstraints);

        hlBestandsnachweisHtml.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/text-html.png"))); // NOI18N
        hlBestandsnachweisHtml.setText("Bestandsnachweis HTML");
        hlBestandsnachweisHtml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hlBestandsnachweisHtmlActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanel2.add(hlBestandsnachweisHtml, gridBagConstraints);

        jPanel7.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jPanel7, gridBagConstraints);

        jPanel8.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel8, gridBagConstraints);

        panProdukte.add(jPanel2, java.awt.BorderLayout.CENTER);

        semiRoundedPanel3.setBackground(Color.DARK_GRAY);
        semiRoundedPanel3.setLayout(new java.awt.GridBagLayout());

        lblHeadProdukte.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadProdukte.setText("Produkte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel3.add(lblHeadProdukte, gridBagConstraints);

        panProdukte.add(semiRoundedPanel3, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panProdukte, gridBagConstraints);

        panEigentuemer.setLayout(new java.awt.BorderLayout());

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jScrollPane1.setOpaque(false);

        epOwner.setBorder(null);
        epOwner.setContentType("text/html");
        epOwner.setOpaque(false);
        jScrollPane1.setViewportView(epOwner);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panEigentuemer.add(jPanel3, java.awt.BorderLayout.CENTER);

        semiRoundedPanel1.setBackground(Color.DARK_GRAY);
        semiRoundedPanel1.setLayout(new java.awt.GridBagLayout());

        lblHeadEigentuemer.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadEigentuemer.setText("Eigentümer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel1.add(lblHeadEigentuemer, gridBagConstraints);

        panEigentuemer.add(semiRoundedPanel1, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panEigentuemer, gridBagConstraints);

        panGrundstuecke.setLayout(new java.awt.BorderLayout());

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jScrollPane2.setOpaque(false);

        lstLandparcels.setOpaque(false);
        lstLandparcels.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstLandparcelsMouseClicked(evt);
            }
        });
        lstLandparcels.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstLandparcelsValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lstLandparcels);

        jPanel4.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        panGrundstuecke.add(jPanel4, java.awt.BorderLayout.CENTER);

        semiRoundedPanel4.setBackground(Color.DARK_GRAY);
        semiRoundedPanel4.setLayout(new java.awt.GridBagLayout());

        lblHeadFlurstuecke.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadFlurstuecke.setText("Flurstücke");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel4.add(lblHeadFlurstuecke, gridBagConstraints);

        panGrundstuecke.add(semiRoundedPanel4, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panGrundstuecke, gridBagConstraints);

        panKarte.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panKarte.setOpaque(false);
        panKarte.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panKarte, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void hlBestandsnachweisHtmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hlBestandsnachweisHtmlActionPerformed
        try {
            String buchungsblattCode = getCompleteBuchungsblattCode();
            if (buchungsblattCode.length() > 0) {
                buchungsblattCode = AlkisCommons.escapeHtmlSpaces(buchungsblattCode);
                String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISBuchNachweis?user=3atest&password=3atest&service=wuppertal&product=LB.A.B.G.NRW&id=" + buchungsblattCode + "&contentType=HTML&certificationType=9701";
                ObjectRendererUIUtils.openURL(url);
            }
        } catch (Exception ex) {
            ObjectRendererUIUtils.showExceptionWindowToUser("Fehler beim Aufruf des Produkts", ex, Alkis_buchungsblattRenderer.this);
            log.error(ex);
        }
    }//GEN-LAST:event_hlBestandsnachweisHtmlActionPerformed

    private void hlBestandsnachweisPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hlBestandsnachweisPdfActionPerformed
        try {
            String buchungsblattCode = getCompleteBuchungsblattCode();
            if (buchungsblattCode.length() > 0) {
                buchungsblattCode = AlkisCommons.escapeHtmlSpaces(buchungsblattCode);
                String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISBuchNachweis?user=3atest&password=3atest&service=wuppertal&product=LB.A.B.G.NRW&id=" + buchungsblattCode + "&contentType=PDF&certificationType=9701";
                ObjectRendererUIUtils.openURL(url);
            }
        } catch (Exception ex) {
            ObjectRendererUIUtils.showExceptionWindowToUser("Fehler beim Aufruf des Produkts", ex, Alkis_buchungsblattRenderer.this);
            log.error(ex);
        }
    }//GEN-LAST:event_hlBestandsnachweisPdfActionPerformed

    private void lstLandparcelsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstLandparcelsMouseClicked
    }//GEN-LAST:event_lstLandparcelsMouseClicked

    private void lstLandparcelsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstLandparcelsValueChanged
        if (!evt.getValueIsAdjusting()) {
            final Object[] selObjs = lstLandparcels.getSelectedValues();
            final List<Geometry> allSelectedGeoms = TypeSafeCollections.newArrayList();
            for (final Object obj : selObjs) {
                if (obj instanceof LightweightLandParcel) {
                    final LightweightLandParcel lwlp = (LightweightLandParcel) obj;
                    if (lwlp.getGeometry() != null) {
                        allSelectedGeoms.add(lwlp.getGeometry());
                    }
//                    final DefaultStyledFeature dsFeature = landParcelFeatureMap.get(lwlp);
//                    if (dsFeature != null) {
//                        if (lwlp.getColor().equals(dsFeature.getFillingPaint())) {
//
//                        }
//                    }
                }
            }
            final GeometryCollection geoCollection = new GeometryCollection((Geometry[]) allSelectedGeoms.toArray(), new GeometryFactory());
            log.fatal(geoCollection);
            map.gotoBoundingBoxWithoutHistory(new BoundingBox(geoCollection));
        }
    }//GEN-LAST:event_lstLandparcelsValueChanged

    public static final String fixBuchungslattCode(String buchungsblattCode) {
        if (buchungsblattCode != null) {
            final StringBuffer buchungsblattCodeSB = new StringBuffer(buchungsblattCode);
            //Fix SICAD-API-strangeness...
            while (buchungsblattCodeSB.length() < 14) {
                buchungsblattCodeSB.append(" ");
            }
            return buchungsblattCodeSB.toString();
        } else {
            return "";
        }
    }

    private final String getCompleteBuchungsblattCode() {
        if (cidsBean != null) {
            final Object buchungsblattCodeObj = cidsBean.getProperty("buchungsblattcode");
            if (buchungsblattCodeObj != null) {
                return fixBuchungslattCode(buchungsblattCodeObj.toString());
            }
        }
        return "";
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    public final Object getLandParcelList() {
        return landParcelList;
    }

    @Override
    public void setCidsBean(CidsBean cb) {
        if (cb != null) {
            cidsBean = cb;
            retrieveWorker = new RetrieveWorker(cidsBean);
            final Object buchungsblattLandparcelListObj = cidsBean.getProperty("landparcels");
            if (buchungsblattLandparcelListObj instanceof List) {
                final List<CidsBean> buchungsblattLandparcelList = (List<CidsBean>) buchungsblattLandparcelListObj;
                for (final CidsBean buchungsblattLandparcelBean : buchungsblattLandparcelList) {
                    landParcelList.add(new LightweightLandParcel(buchungsblattLandparcelBean));
                }
            }
            final Runnable edtRunner = new Runnable() {

                @Override
                public void run() {
                    if (landparcelListBinding.isBound()) {
                        landparcelListBinding.unbind();
                    }
                    CismetThreadPool.execute(retrieveWorker);
                    landparcelListBinding.bind();
                    initMap();
                }
            };
            if (EventQueue.isDispatchThread()) {
                edtRunner.run();
            } else {
                EventQueue.invokeLater(edtRunner);
            }


//            log.fatal(Arrays.deepToString(buchungsblatt.getBuchungsstellen()[0].getLandParcel()[0].getAdministrativeDistricts()));
//            log.fatal(Arrays.deepToString(buchungsblatt.getOffices().getDistrictCourtName()));//amtsgericht
//            log.fatal(Arrays.deepToString(buchungsblatt.getOffices().getLandRegistryOfficeName()));//kataster

//            bindingGroup.unbind();
//            bindingGroup.bind();
        }
    }

    private final void initMap() {
        if (landParcelList.size() > 0) {
            final BoundingBox box = new BoundingBox(landParcelList.get(0).getGeometry().getEnvelope().buffer(AlkisCommons.MAP_CONSTANTS.GEO_BUFFER));

            try {
                final ActiveLayerModel mappingModel = new ActiveLayerModel();
                mappingModel.setSrs(AlkisCommons.MAP_CONSTANTS.SRS);
                //TODO: do we need an swsw for every class?
                mappingModel.addHome(new XBoundingBox(box.getX1(), box.getY1(), box.getX2(), box.getY2(), AlkisCommons.MAP_CONSTANTS.SRS, true));
                SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(AlkisCommons.MAP_CONSTANTS.CALL_STRING));
                swms.setName("Buchungsblatt");
                mappingModel.addLayer(swms);
                map.setMappingModel(mappingModel);
                for (final LightweightLandParcel lwLandparcel : landParcelList) {
                    final DefaultStyledFeature dsf = new DefaultFeatureServiceFeature();
                    dsf.setGeometry(lwLandparcel.getGeometry());
                    dsf.setFillingPaint(lwLandparcel.getColor());
                    map.getFeatureCollection().addFeature(dsf);

                    landParcelFeatureMap.put(lwLandparcel, dsf);
                }
                map.unlock();
                final int duration = map.getAnimationDuration();
                map.setAnimationDuration(0);
                map.gotoInitialBoundingBox();
//                map.zoomToFeatureCollection();
                map.setInteractionMode(MappingComponent.ZOOM);
                //finally when all configurations are done ...
                map.addCustomInputListener("MUTE", new PBasicInputEventHandler() {

                    @Override
                    public void mouseClicked(PInputEvent arg0) {
                        log.fatal("TODO!");

                    }
                });
                map.setInteractionMode("MUTE");
                map.setAnimationDuration(duration);
            } catch (Throwable t) {
                log.fatal(t, t);
            }
        } else {
            panKarte.setVisible(false);
        }
    }

    private final void displayBuchungsblattInfos(Buchungsblatt buchungsblatt) {
        if (buchungsblatt != null) {
            final Offices offices = buchungsblatt.getOffices();
            if (offices != null) {
                lblAmtgericht.setText(surroundWithHTMLTags(AlkisCommons.arrayToSeparatedString(offices.getDistrictCourtName(), "<br>")));
                lblKatasteramt.setText(surroundWithHTMLTags(AlkisCommons.arrayToSeparatedString(offices.getLandRegistryOfficeName(), "<br>")));
            }
        }
    }

    private final String surroundWithHTMLTags(String in) {
        final StringBuilder result = new StringBuilder("<html>");
        result.append(in);
        result.append("</html>");
        return result.toString();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        } else {
            title = "Buchungsblatt " + title;
        }
        this.title = title;
        lblTitle.setText(this.title);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel blWait;
    private javax.swing.JEditorPane epOwner;
    private org.jdesktop.swingx.JXHyperlink hlBestandsnachweisHtml;
    private org.jdesktop.swingx.JXHyperlink hlBestandsnachweisPdf;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAmtgericht;
    private javax.swing.JLabel lblDescAmtsgericht;
    private javax.swing.JLabel lblDescGrundbuchbezirk;
    private javax.swing.JLabel lblDescKatasteramt;
    private javax.swing.JLabel lblGrundbuchbezirk;
    private javax.swing.JLabel lblHeadEigentuemer;
    private javax.swing.JLabel lblHeadFlurstuecke;
    private javax.swing.JLabel lblHeadMainInfo;
    private javax.swing.JLabel lblHeadProdukte;
    private javax.swing.JLabel lblKatasteramt;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstLandparcels;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panEigentuemer;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panGrundstuecke;
    private javax.swing.JPanel panKarte;
    private javax.swing.JPanel panProdukte;
    private javax.swing.JPanel panTitle;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel4;
    // End of variables declaration//GEN-END:variables

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    /**
     * @return the buchungsblatt
     */
    public Object getBuchungsblatt() {
        return buchungsblatt;
    }

    /**
     * @param buchungsblatt the buchungsblatt to set
     */
    public void setBuchungsblatt(Buchungsblatt buchungsblatt) {
        this.buchungsblatt = buchungsblatt;
    }

    private final void setWaiting(boolean waiting) {
        blWait.setVisible(waiting);
        blWait.setBusy(waiting);
    }

    private final boolean isWaiting() {
        return blWait.isBusy();
    }

    final class RetrieveWorker extends SwingWorker<Buchungsblatt, Void> {

        private final CidsBean bean;

        public RetrieveWorker(CidsBean bean) {
            this.bean = bean;
            setWaiting(true);
        }

        @Override
        protected Buchungsblatt doInBackground() throws Exception {
            return infoService.getBuchungsblatt(soapProvider.getIdentityCard(), soapProvider.getService(), fixBuchungslattCode(String.valueOf(bean.getProperty("buchungsblattcode"))));
        }

        @Override
        protected void done() {
            try {
                if (!isCancelled()) {
                    buchungsblatt = get();
                    if (buchungsblatt != null) {
                        displayBuchungsblattInfos(buchungsblatt);
                        final Owner[] owners = buchungsblatt.getOwners();
//                    final StringBuilder ownerBuilder = new StringBuilder("<html>");
                        final StringBuilder ownerBuilder = new StringBuilder("<html>");
                        for (final Owner owner : owners) {
                            ownerBuilder.append(AlkisCommons.ownerToString(owner, ""));
                        }
                        ownerBuilder.append("</html>");
                        epOwner.setText(ownerBuilder.toString());
                    }
                }
            } catch (InterruptedException ex) {
                log.warn(ex, ex);
            } catch (ExecutionException ex) {
                ObjectRendererUIUtils.showExceptionWindowToUser("Fehler beim Retrieve", ex, Alkis_buchungsblattRenderer.this);
                log.error(ex, ex);
            } finally {
                setWaiting(false);
            }
        }
    }
//    final class RetrieveWorker extends SwingWorker<Buchungsblatt, Void> {
//
//        public RetrieveWorker(String buchungsblattCode) {
//            this.buchungsBlattCode = buchungsblattCode;
//            timer = new Timer(250, new ActionListener() {
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    if (waitStat.length() < 11) {
//                        waitStat += ".";
//                    } else {
//                        waitStat = ".";
//                    }
//                    for (final JLabel label : retrieveableLabels) {
//                        label.setText(waitStat);
//                    }
//                }
//            });
//            btnRetrieve.setVisible(false);
//            timer.start();
//        }
//        private final String buchungsBlattCode;
//        private String waitStat = "";
//        private final Timer timer;
//
//        @Override
//        protected Buchungsblatt doInBackground() throws Exception {
//
//            return infoService.getBuchungsblatt(soapProvider.getIdentityCard(), soapProvider.getService(), buchungsBlattCode);
//        }
//
//        private final void restoreOnException() {
//            btnRetrieve.setVisible(true);
//            for (JLabel label : retrieveableLabels) {
//                label.setText("...");
//            }
//        }
//
//        @Override
//        protected void done() {
//            timer.stop();
//            try {
//                final Buchungsblatt buchungsblatt = get();
//                if (buchungsblatt != null) {
//                    Alkis_buchungsblattRenderer.this.setBuchungsblatt(buchungsblatt);
////                    Alkis_buchungsblattRenderer.this.bindingGroup.unbind();
////                    Alkis_buchungsblattRenderer.this.bindingGroup.bind();
//
//                    //TODO this is quick and dirty for tesing only!
////                    lblTxtEigentuemerNachname.setText(buchungsblatt.getOwners()[0].getSurName());
//
////                    lblTxtModellart.setText(point.getModellArt());
////                    lblTxtDienststelle.setText(point.getZustaendigeStelleStelle());
////                    lblTxtLand.setText(point.getZustaendigeStelleLand());
////                    lblTxtBeginn.setText(point.getLebenszeitIntervallBeginnt());
////                    lblTxtEnde.setText(point.getLebenszeitIntervallEndet());
////                    lblTxtAnlass.setText(point.getAnlass());
////                    lblTxtBemerkungAbmarkung.setText(point.getBemerkungZurAbmarkungName());
//                }
//            } catch (InterruptedException ex) {
//                restoreOnException();
//                log.warn(ex, ex);
//            } catch (Exception ex) {
//                //TODO show error message to user?
//                restoreOnException();
//                org.jdesktop.swingx.error.ErrorInfo ei = new ErrorInfo("Fehler beim Retrieve", ex.getMessage(), null, null, ex, Level.ALL, null);
//                org.jdesktop.swingx.JXErrorPane.showDialog(StaticSwingTools.getParentFrame(Alkis_buchungsblattRenderer.this), ei);
//                log.error(ex, ex);
//            }
//        }
//    }
//-------------------------------
//        private void hlGrundstuecksnachweisPdfActionPerformed(java.awt.event.ActionEvent evt) {
//        try {
//            String buchungsblattCode = getCompleteBuchungsblattCode();
//            if (buchungsblattCode.length() > 0) {
//                buchungsblattCode = escapeHtmlSpaces(buchungsblattCode);
//                String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISBuchNachweis?user=3atest&password=3atest&service=wuppertal&product=LB.A.G.G.NRW&id=" + buchungsblattCode + "&contentType=PDF&certificationType=9601";
//                ObjectRendererUIUtils.openURL(url);
//            }
//        } catch (Exception ex) {
//            log.error(ex);
//        }
//    }
//
//    private void hlGrundstuecksnachweisHtmlActionPerformed(java.awt.event.ActionEvent evt) {
//        try {
//            String buchungsblattCode = getCompleteBuchungsblattCode();
//            if (buchungsblattCode.length() > 0) {
//                buchungsblattCode = escapeHtmlSpaces(buchungsblattCode);
//                String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISBuchNachweis?user=3atest&password=3atest&service=wuppertal&product=LB.A.G.G.NRW&id=" + buchungsblattCode + "&contentType=HTML&certificationType=9601";
//                ObjectRendererUIUtils.openURL(url);
//            }
//        } catch (Exception ex) {
//            log.error(ex);
//        }
//    }
//
//    private void hlBestandsnachweisPdfActionPerformed(java.awt.event.ActionEvent evt) {
//        try {
//            String buchungsblattCode = getCompleteBuchungsblattCode();
//            if (buchungsblattCode.length() > 0) {
//                buchungsblattCode = escapeHtmlSpaces(buchungsblattCode);
//                String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISBuchNachweis?user=3atest&password=3atest&service=wuppertal&product=LB.A.B.G.NRW&id=" + buchungsblattCode + "&contentType=PDF&certificationType=9701";
//                ObjectRendererUIUtils.openURL(url);
//            }
//        } catch (Exception ex) {
//            log.error(ex);
//        }
//    }
//
//    private void hlBestandsnachweisHtmlActionPerformed(java.awt.event.ActionEvent evt) {
//        try {
//            String buchungsblattCode = getCompleteBuchungsblattCode();
//            if (buchungsblattCode.length() > 0) {
//                buchungsblattCode = escapeHtmlSpaces(buchungsblattCode);
//                String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISBuchNachweis?user=3atest&password=3atest&service=wuppertal&product=LB.A.B.G.NRW&id=" + buchungsblattCode + "&contentType=HTML&certificationType=9701";
//                ObjectRendererUIUtils.openURL(url);
//            }
//        } catch (Exception ex) {
//            log.error(ex);
//        }
//    }

    @Override
    public Border getTitleBorder() {
        return new EmptyBorder(10, 10, 10, 10);
    }

    @Override
    public Border getFooterBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    @Override
    public Border getCenterrBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    private static final class LightweightLandParcel {

        public LightweightLandParcel(CidsBean buchungsBlattLandparcelBean) {
            this.landparcelCode = String.valueOf(buchungsBlattLandparcelBean.getProperty("landparcelcode"));
            final Object geoObj = buchungsBlattLandparcelBean.getProperty("geometrie.geo_field");
            if (geoObj instanceof Geometry) {
                this.geometry = (Geometry) geoObj;
            } else {
                this.geometry = null;
            }
            final Object fullObjIDObj = buchungsBlattLandparcelBean.getProperty("fullobjectid");
            int tmpFullObjID = -1;
            if (fullObjIDObj != null) {
                try {
                    tmpFullObjID = Integer.parseInt(fullObjIDObj.toString());
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            }
            this.fullObjectID = tmpFullObjID;
            nextColor = (nextColor + 1) % COLORS.length;
            this.color = COLORS[nextColor];
//            final Color baseColor = COLORS[nextColor];
//            this.color = new Color(baseColor.get, baseColor.getGreen(), baseColor.getBlue(), 0.5f);
        }
        private static final Color[] COLORS = new Color[]{
            Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE,
            Color.PINK, Color.RED, Color.YELLOW
        };
        private final String landparcelCode;
        private final Color color;
        private final Geometry geometry;
        private final int fullObjectID;
        private static int nextColor = 0;

        /**
         * @return the landparcelCode
         */
        public String getLandparcelCode() {
            return landparcelCode;
        }

        /**
         * @return the geometry
         */
        public Geometry getGeometry() {
            return geometry;
        }

        /**
         * @return the fullObjectID
         */
        public int getFullObjectID() {
            return fullObjectID;
        }

        @Override
        public String toString() {
            return String.valueOf(landparcelCode);
        }

        /**
         * @return the color
         */
        public Color getColor() {
            return color;
        }
    }

    private static final class FancyListCellRenderer extends DefaultListCellRenderer {

        private boolean drawMySelection = false;
        private boolean drawMyFocus = false;
        private static final int SPACING = 5;
        private static final int MARKER_WIDTH = 4;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            drawMySelection = isSelected;
            drawMyFocus = cellHasFocus;
            final Component comp = super.getListCellRendererComponent(list, value, index, false, false);
            if (value instanceof LightweightLandParcel) {
                final LightweightLandParcel lwlp = (LightweightLandParcel) value;
                setBackground(lwlp.getColor());
            }

            setBorder(BorderFactory.createEmptyBorder(1, 2 * SPACING + MARKER_WIDTH, 1, 0));
            return comp;
        }

        public FancyListCellRenderer() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            final Graphics2D g2d = (Graphics2D) g;
//            final Color col = g2d.getColor();
            final Paint backup = g2d.getPaint();
            if (drawMySelection) {
                g2d.setColor(new Color(0, 0, 255, 25));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            g2d.setColor(getBackground());
            g2d.fillRect(SPACING, 2, MARKER_WIDTH, getHeight() - 4);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(SPACING, 2, MARKER_WIDTH, getHeight() - 4);

            if (drawMyFocus) {
                g2d.setColor(new Color(0, 0, 255, 75));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
            g2d.setPaint(backup);
            super.paintComponent(g);
        }
//        @Override
//        protected void paintComponent(Graphics g) {
//            final Graphics2D g2d = (Graphics2D) g;
////            final Color col = g2d.getColor();
//            final Paint backup = g2d.getPaint();
//            if (drawMySelection) {
//                g2d.setColor(new Color(0, 0, 255, 25));
//                g2d.fillRect(0, 0, getWidth(), getHeight());
//            }
//            final int gradientOffset = (getWidth() * 3) / 4;
////            final int gradientOffset = (getWidth()*2)/3;
//            final GradientPaint gp = new GradientPaint(gradientOffset, 0.f, TRANSPARENT, getWidth(), 0.f, getBackground());
////            g2d.setColor(getBackground());
//            g2d.setPaint(gp);
//            g2d.fillRect(gradientOffset, 0, getWidth() - gradientOffset, getHeight());
////            g2d.fillRect(getWidth() / 2, 0, getWidth() / 2, getHeight());
////            g2d.setColor(col);
//            g2d.setPaint(backup);
//            final Color colorBackup = g2d.getColor();
//            if (drawMySelection) {
//                final Polygon poly = new Polygon();
//                poly.addPoint(ARROW_SPACING, ARROW_SPACING);
//                poly.addPoint(ARROW_WIDTH, getHeight() / 2);
//                poly.addPoint(ARROW_SPACING, getHeight() - ARROW_SPACING);
//                g2d.setColor(ARROW_FILL);
//                g2d.fillPolygon(poly);
//                g2d.setColor(ARROW_BORDER);
//                g2d.draw(poly);
//            }
//            if (drawMyFocus) {
//                g2d.setColor(new Color(0, 0, 255, 75));
//                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//            }
//            g2d.setColor(colorBackup);
//            super.paintComponent(g);
//        }
    }
}
