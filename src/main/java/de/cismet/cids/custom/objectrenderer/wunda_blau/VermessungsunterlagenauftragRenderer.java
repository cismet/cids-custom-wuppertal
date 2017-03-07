/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.Exceptions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import java.io.IOException;
import java.io.StringReader;

import java.net.URL;

import java.text.DateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.converter.SQLTimestampToStringConverter;
import de.cismet.cids.custom.objectrenderer.utils.FlurstueckFinder;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.WundaBlauServerResources;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;
import de.cismet.cids.custom.utils.pointnumberreservation.VermessungsStellenSearchResult;
import de.cismet.cids.custom.utils.vermessungsunterlagen.VermessungsunterlagenHelper;
import de.cismet.cids.custom.utils.vermessungsunterlagen.VermessungsunterlagenProperties;
import de.cismet.cids.custom.utils.vermessungsunterlagen.VermessungsunterlagenUtils;
import de.cismet.cids.custom.utils.vermessungsunterlagen.exceptions.VermessungsunterlagenException;
import de.cismet.cids.custom.utils.vermessungsunterlagen.exceptions.VermessungsunterlagenJobException;
import de.cismet.cids.custom.utils.vermessungsunterlagen.exceptions.VermessungsunterlagenTaskException;
import de.cismet.cids.custom.utils.vermessungsunterlagen.exceptions.VermessungsunterlagenTaskRetryException;
import de.cismet.cids.custom.utils.vermessungsunterlagen.exceptions.VermessungsunterlagenValidatorException;
import de.cismet.cids.custom.wunda_blau.search.server.KundeByVermessungsStellenNummerSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.converters.BooleanToStringConverter;

import de.cismet.cids.server.actions.GetServerResourceServerAction;
import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpOrFtpDownload;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class VermessungsunterlagenauftragRenderer extends JPanel implements CidsBeanRenderer, TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VermessungsunterlagenauftragRenderer.class);
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT);

    //~ Instance fields --------------------------------------------------------

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** DOCUMENT ME! */
    private CidsBean cidsBean;
    private String title;

    private final MappingComponent mappingComponent;
    private StyledFeature geometrieFeature;
    private StyledFeature geometrieSaumFeature;
    private StyledFeature flurstueckeFeature;
    private String vermStelle;

    private final Map<String, CidsBean> fsMap = new HashMap<String, CidsBean>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JDialog exceptionDialog;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<VermessungsunterlagenException> jList4;
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
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private javax.swing.JLabel lblEingangsdatum;
    private javax.swing.JLabel lblEingangsdatum1;
    private javax.swing.JLabel lblErstellungsdatumZip;
    private javax.swing.JLabel lblErstellungsdatumZip1;
    private javax.swing.JLabel lblGeschBuchNummer;
    private javax.swing.JLabel lblGeschBuchNummer1;
    private javax.swing.JLabel lblKatasterId;
    private javax.swing.JLabel lblKatasterId1;
    private javax.swing.JLabel lblMitGrenzniederschriften;
    private javax.swing.JLabel lblMitGrenzniederschriften1;
    private javax.swing.JLabel lblMitPunktnummernreservierung;
    private javax.swing.JLabel lblMitPunktnummernreservierung1;
    private javax.swing.JLabel lblSaumAP;
    private javax.swing.JLabel lblSaumAP1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVermStelle;
    private javax.swing.JLabel lblVermStelle1;
    private javax.swing.JLabel lblVermessungsarten;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panContent1;
    private javax.swing.JPanel panContent2;
    private javax.swing.JPanel panDetails4;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panTitleString;
    private javax.swing.JPanel pnlMap;
    private de.cismet.tools.gui.RoundedPanel roundedPanel2;
    private de.cismet.tools.gui.RoundedPanel roundedPanel3;
    private de.cismet.tools.gui.RoundedPanel roundedPanel4;
    private de.cismet.tools.gui.RoundedPanel roundedPanel7;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel4;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel5;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel8;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_stadtbildserieEditor object.
     */
    public VermessungsunterlagenauftragRenderer() {
        initComponents();

        title = "";
        mappingComponent = new MappingComponent();
        pnlMap.setLayout(new BorderLayout());
        pnlMap.add(mappingComponent, BorderLayout.CENTER);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(
            VermessungsunterlagenException.class,
            new VermessungsunterlagenExceptionJsonDeserializer());
        module.addDeserializer(
            VermessungsunterlagenTaskException.class,
            new VermessungsunterlagenTaskExceptionJsonDeserializer());
        objectMapper.registerModule(module);
        exceptionDialog.pack();
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

        panTitle = new javax.swing.JPanel();
        panTitleString = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        buttonGroup1 = new javax.swing.ButtonGroup();
        exceptionDialog = new javax.swing.JDialog();
        jPanel8 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<VermessungsunterlagenException>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        roundedPanel2 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        panContent = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblGeschBuchNummer = new javax.swing.JLabel();
        lblVermStelle = new javax.swing.JLabel();
        lblKatasterId = new javax.swing.JLabel();
        lblEingangsdatum = new javax.swing.JLabel();
        lblErstellungsdatumZip = new javax.swing.JLabel();
        lblMitGrenzniederschriften = new javax.swing.JLabel();
        lblMitPunktnummernreservierung = new javax.swing.JLabel();
        lblSaumAP = new javax.swing.JLabel();
        lblVermessungsarten = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        lblGeschBuchNummer1 = new javax.swing.JLabel();
        lblVermStelle1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblKatasterId1 = new javax.swing.JLabel();
        lblEingangsdatum1 = new javax.swing.JLabel();
        lblErstellungsdatumZip1 = new javax.swing.JLabel();
        lblMitGrenzniederschriften1 = new javax.swing.JLabel();
        lblMitPunktnummernreservierung1 = new javax.swing.JLabel();
        lblSaumAP1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<String>();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        roundedPanel3 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel4 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        panContent1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<String>();
        roundedPanel4 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel5 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        panContent2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<String>();
        jPanel2 = new javax.swing.JPanel();
        roundedPanel7 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel8 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel6 = new javax.swing.JLabel();
        panDetails4 = new javax.swing.JPanel();
        pnlMap = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.BorderLayout());

        panTitleString.setOpaque(false);
        panTitleString.setLayout(new java.awt.GridBagLayout());

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("TITLE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(lblTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panTitleString.add(jPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panTitleString.add(filler1, gridBagConstraints);

        panTitle.add(panTitleString, java.awt.BorderLayout.CENTER);

        exceptionDialog.setTitle("Fehler bei der Erzeugung des Auftrags.");
        exceptionDialog.setModal(true);
        exceptionDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText("Während der Verarbeitung des Auftrags kam es zu einem Fehler.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel8.add(jLabel7, gridBagConstraints);

        jButton1.setText("Schließen");
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        jPanel8.add(jButton1, gridBagConstraints);

        jList4.setModel(new DefaultListModel<VermessungsunterlagenException>());
        jList4.setToolTipText("");
        jList4.setCellRenderer(new VermessungsunterlagenExceptionListCellRenderer());
        jList4.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList4MouseClicked(evt);
                }
            });
        jScrollPane4.setViewportView(jList4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel8.add(jScrollPane4, gridBagConstraints);

        jLabel8.setText("Fehler-Typ: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel8.add(jLabel8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel8.add(jLabel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        exceptionDialog.getContentPane().add(jPanel8, gridBagConstraints);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        roundedPanel2.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel3.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel3.setLayout(new java.awt.FlowLayout());

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Allgemeine Angaben zum Auftrag");
        semiRoundedPanel3.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        roundedPanel2.add(semiRoundedPanel3, gridBagConstraints);

        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Vorgangsnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(jLabel4, gridBagConstraints);

        lblStatus.setText("Status des Auftrags:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblStatus, gridBagConstraints);

        lblGeschBuchNummer.setText("Geschäftsbuchnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblGeschBuchNummer, gridBagConstraints);

        lblVermStelle.setText("Vermessungsstelle:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblVermStelle, gridBagConstraints);

        lblKatasterId.setText("Katasteramts ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblKatasterId, gridBagConstraints);

        lblEingangsdatum.setText("Eingangsdatum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblEingangsdatum, gridBagConstraints);

        lblErstellungsdatumZip.setText("Erstellungsdatum Zip:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblErstellungsdatumZip, gridBagConstraints);

        lblMitGrenzniederschriften.setText("Mit Grenzniederschriften:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblMitGrenzniederschriften, gridBagConstraints);

        lblMitPunktnummernreservierung.setText("Nur Punktnummernreservierung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblMitPunktnummernreservierung, gridBagConstraints);

        lblSaumAP.setText("Saum AP-Suche:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSaumAP, gridBagConstraints);

        lblVermessungsarten.setText("Vermessungsarten:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblVermessungsarten, gridBagConstraints);

        jTextField1.setEditable(false);
        jTextField1.setBackground(null);
        jTextField1.setBorder(null);
        jTextField1.setOpaque(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schluessel}"),
                jTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(jTextField1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.status != null}"),
                jXHyperlink1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.status}"),
                jXHyperlink1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "status");
        binding.setSourceNullValue("in Bearbeitung");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(new BooleanToStringConverter("OK - Download", "Fehler", "in Bearbeitung"));
        bindingGroup.addBinding(binding);

        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(jXHyperlink1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geschaeftsbuchnummer}"),
                lblGeschBuchNummer1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "geschaeftsbuchnummer");
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblGeschBuchNummer1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.vermessungsstelle}"),
                lblVermStelle1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "vermessungsstelle");
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblVermStelle1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("(${vermStelle})"),
                jLabel5,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "vermStelle");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(jLabel5, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.katasteramtsid}"),
                lblKatasterId1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "katasteramtsid");
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblKatasterId1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.timestamp}"),
                lblEingangsdatum1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "timestamp");
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(new SQLTimestampToStringConverter(DATE_FORMAT));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblEingangsdatum1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zip_timestamp}"),
                lblErstellungsdatumZip1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "zip_timestamp");
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(new SQLTimestampToStringConverter(DATE_FORMAT));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblErstellungsdatumZip1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.mit_grenzniederschriften}"),
                lblMitGrenzniederschriften1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "mit_grenzniederschriften");
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(new BooleanToStringConverter("ja", "nein", "-"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblMitGrenzniederschriften1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nur_punktnummernreservierung}"),
                lblMitPunktnummernreservierung1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "nur_punktnummernreservierung");
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(new BooleanToStringConverter("ja", "nein", "-"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblMitPunktnummernreservierung1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.saumap} m"),
                lblSaumAP1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "saumap");
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(lblSaumAP1, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(200, 80));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 80));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.vermessungsarten}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        jList1,
                        "vermessungsarten");
        bindingGroup.addBinding(jListBinding);

        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(jScrollPane1, gridBagConstraints);

        jPanel6.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedPanel2.add(panContent, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(roundedPanel2, gridBagConstraints);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        roundedPanel3.setMaximumSize(new java.awt.Dimension(268, 165));
        roundedPanel3.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel4.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel4.setMaximumSize(new java.awt.Dimension(258, 25));
        semiRoundedPanel4.setMinimumSize(new java.awt.Dimension(258, 25));
        semiRoundedPanel4.setPreferredSize(new java.awt.Dimension(258, 25));
        semiRoundedPanel4.setLayout(new java.awt.FlowLayout());

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Antragsflurstücke");
        semiRoundedPanel4.add(jLabel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        roundedPanel3.add(semiRoundedPanel4, gridBagConstraints);

        panContent1.setOpaque(false);
        panContent1.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setMaximumSize(new java.awt.Dimension(258, 130));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(258, 130));

        jList2.setModel(new DefaultListModel<String>());
        jList2.setCellRenderer(new AlkisFlurstueckInfoListCellRenderer());
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList2MouseClicked(evt);
                }
            });
        jScrollPane2.setViewportView(jList2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent1.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedPanel3.add(panContent1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(roundedPanel3, gridBagConstraints);

        roundedPanel4.setMaximumSize(new java.awt.Dimension(268, 165));
        roundedPanel4.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel5.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel5.setMaximumSize(new java.awt.Dimension(258, 25));
        semiRoundedPanel5.setMinimumSize(new java.awt.Dimension(258, 25));
        semiRoundedPanel5.setPreferredSize(new java.awt.Dimension(258, 25));
        semiRoundedPanel5.setLayout(new java.awt.FlowLayout());

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Punktnummernreservierungen");
        semiRoundedPanel5.add(jLabel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        roundedPanel4.add(semiRoundedPanel5, gridBagConstraints);

        panContent2.setOpaque(false);
        panContent2.setLayout(new java.awt.GridBagLayout());

        jScrollPane3.setMaximumSize(new java.awt.Dimension(258, 130));
        jScrollPane3.setMinimumSize(new java.awt.Dimension(258, 130));

        jList3.setCellRenderer(new AlkisFlurstueckInfoListCellRenderer());

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.punktnummern}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                jList3);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create(
                "${kilometerquadrat} - ${katasteramt} : ${anzahl}"));
        bindingGroup.addBinding(jListBinding);

        jScrollPane3.setViewportView(jList3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent2.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedPanel4.add(panContent2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(roundedPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(jPanel1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        roundedPanel7.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel8.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel8.setLayout(new java.awt.FlowLayout());

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Karte");
        semiRoundedPanel8.add(jLabel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        roundedPanel7.add(semiRoundedPanel8, gridBagConstraints);

        panDetails4.setOpaque(false);
        panDetails4.setLayout(new java.awt.GridBagLayout());

        pnlMap.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panDetails4.add(pnlMap, gridBagConstraints);

        jPanel5.setOpaque(false);

        buttonGroup1.add(jToggleButton1);
        jToggleButton1.setText("Antragsflurstücke");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton1ActionPerformed(evt);
                }
            });
        jPanel5.add(jToggleButton1);

        buttonGroup1.add(jToggleButton2);
        jToggleButton2.setText("Verm.-Gebiet ohne Saum");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton2ActionPerformed(evt);
                }
            });
        jPanel5.add(jToggleButton2);

        buttonGroup1.add(jToggleButton3);
        jToggleButton3.setText("Verm.-Gebiet mit Saum");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.saum > 0}"),
                jToggleButton3,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton3ActionPerformed(evt);
                }
            });
        jPanel5.add(jToggleButton3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panDetails4.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        roundedPanel7.add(panDetails4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(roundedPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel3, gridBagConstraints);

        jCheckBox1.setText("Dieser Auftrag ist ein Testfall");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.test}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jCheckBox1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        add(jCheckBox1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton1ActionPerformed
        mappingComponent.getFeatureCollection().removeFeature(geometrieFeature);
        mappingComponent.getFeatureCollection().removeFeature(geometrieSaumFeature);
        mappingComponent.getFeatureCollection().addFeature(flurstueckeFeature);
    }                                                                                  //GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton2ActionPerformed
        mappingComponent.getFeatureCollection().addFeature(geometrieFeature);
        mappingComponent.getFeatureCollection().removeFeature(geometrieSaumFeature);
        mappingComponent.getFeatureCollection().removeFeature(flurstueckeFeature);
    }                                                                                  //GEN-LAST:event_jToggleButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton3ActionPerformed
        mappingComponent.getFeatureCollection().removeFeature(geometrieFeature);
        mappingComponent.getFeatureCollection().addFeature(geometrieSaumFeature);
        mappingComponent.getFeatureCollection().removeFeature(flurstueckeFeature);
    }                                                                                  //GEN-LAST:event_jToggleButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */

    public String getVermStelle() {
        return vermStelle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        if (Boolean.TRUE.equals(cidsBean.getProperty("status"))) {
            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(this)) {
                final String jobname = DownloadManagerDialog.getInstance().getJobName();
                final String schluessel = (String)cidsBean.getProperty("schluessel");

                try {
                    final Object ret = SessionManager.getSession()
                                .getConnection()
                                .executeTask(SessionManager.getSession().getUser(),
                                    GetServerResourceServerAction.TASK_NAME,
                                    "WUNDA_BLAU",
                                    WundaBlauServerResources.VERMESSUNGSUNTERLAGENPORTAL_PROPERTIES.getValue());
                    if (ret instanceof Exception) {
                        throw (Exception)ret;
                    }
                    final Properties properties = new Properties();
                    properties.load(new StringReader((String)ret));

                    final VermessungsunterlagenProperties props = new VermessungsunterlagenProperties(properties);
                    // VermUnterlagen = alter prefix. wird bei neuen Anträgen nicht mehr verwendet.
                    final String filename = ((schluessel.contains("_")) ? VermessungsunterlagenHelper.DIR_PREFIX
                                                                        : "VermUnterlagen") + "_"
                                + schluessel
                                + ".zip";
                    final URL url = new URL("ftp://" + props.getFtpLogin() + ":" + props.getFtpPass() + "@"
                                    + props.getFtpHost()
                                    + props.getFtpPath() + "/" + filename);
                    final Download download = new HttpOrFtpDownload(
                            url,
                            "",
                            jobname,
                            "Vermessungsunterlagen",
                            filename,
                            ".zip");
                    DownloadManager.instance().add(download);
                } catch (final Exception ex) {
                    final ErrorInfo errorInfo = new ErrorInfo(
                            "Fehler",
                            "Der Download konnte nicht gestartet werden.",
                            null,
                            null,
                            ex,
                            Level.ALL,
                            null);
                    JXErrorPane.showDialog(this, errorInfo);
                }
            }
        } else if (Boolean.FALSE.equals(cidsBean.getProperty("status"))) {
            final String exception_json = (String)cidsBean.getProperty("exception_json");
            Exception exception;
            try {
                try {
                    // new way
                    exception = objectMapper.readValue(exception_json, VermessungsunterlagenException.class);
                } catch (final Exception ex) {
                    // old way
                    exception = objectMapper.readValue(exception_json, Exception.class);
                }

                if (exception instanceof VermessungsunterlagenException) {
                    // new way
                    final VermessungsunterlagenException ex = (VermessungsunterlagenException)exception;
                    if (ex instanceof VermessungsunterlagenJobException) {
                        final VermessungsunterlagenJobException jobEx = (VermessungsunterlagenJobException)ex;
                        final VermessungsunterlagenTaskException taskEx = jobEx.getCause();

                        jLabel9.setText("Task " + taskEx.getTask());
                        if (taskEx instanceof VermessungsunterlagenTaskRetryException) {
                            final VermessungsunterlagenTaskRetryException retryEx =
                                (VermessungsunterlagenTaskRetryException)taskEx;
                            for (final VermessungsunterlagenException retry : retryEx.getExceptions()) {
                                ((DefaultListModel<VermessungsunterlagenException>)jList4.getModel()).addElement(retry);
                            }
                        } else {
                            ((DefaultListModel<VermessungsunterlagenException>)jList4.getModel()).addElement(taskEx);
                        }
                    } else if (ex instanceof VermessungsunterlagenValidatorException) {
                        jLabel9.setText("Validierung");
                        ((DefaultListModel<VermessungsunterlagenException>)jList4.getModel()).addElement(ex);
                    } else {
                        jLabel9.setText("Job (kritisch)");
                        ((DefaultListModel<VermessungsunterlagenException>)jList4.getModel()).addElement(ex);
                    }
                    StaticSwingTools.showDialog(exceptionDialog);
                } else {
                    // old way
                    final ErrorInfo errorInfo = new ErrorInfo(
                            "Fehler bei der Erzeugung des Auftrags.",
                            "Fehlermeldung:\n\n"
                                    + exception.getMessage()
                                    + "\n\nSiehe Details.",
                            null,
                            null,
                            exception,
                            Level.ALL,
                            null);
                    JXErrorPane.showDialog(this, errorInfo);
                }
            } catch (final IOException ex) {
                // fallback
                exception = ex;
                final ErrorInfo errorInfo = new ErrorInfo(
                        "Fehler",
                        "Die Fehlermeldung konnte nicht geparst werden.",
                        null,
                        null,
                        exception,
                        Level.ALL,
                        null);
                JXErrorPane.showDialog(this, errorInfo);
            }
        }
    } //GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList2MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList2MouseClicked
        if (evt.getClickCount() > 1) {
            final Object selObject = jList2.getSelectedValue();
            if (selObject instanceof String) {
                final CidsBean cidsBean = fsMap.get((String)selObject);

                if (cidsBean != null) {
                    new SwingWorker<CidsBean, Object>() {

                            @Override
                            protected CidsBean doInBackground() throws Exception {
                                if (cidsBean.getProperty("historisch") != null) {
                                    return cidsBean;
                                } else {
                                    final MetaObjectNode mon = FlurstueckRenderer.searchAlkisLandparcel(cidsBean);
                                    final MetaObject mo = SessionManager.getProxy()
                                                .getMetaObject(mon.getObjectId(), mon.getClassId(), mon.getDomain());
                                    return mo.getBean();
                                }
                            }

                            @Override
                            protected void done() {
                                CidsBean cidsBean = null;
                                try {
                                    cidsBean = get();
                                } catch (InterruptedException ex) {
                                    Exceptions.printStackTrace(ex);
                                } catch (ExecutionException ex) {
                                    Exceptions.printStackTrace(ex);
                                }

                                if (cidsBean != null) {
                                    ComponentRegistry.getRegistry()
                                            .getDescriptionPane()
                                            .gotoMetaObjectNode(new MetaObjectNode(cidsBean), false);
                                    ObjectRendererUtils.switchToCismapMap();
                                    ObjectRendererUtils.addBeanGeomAsFeatureToCismapMap(cidsBean, true);
                                }
                            }
                        }.execute();
                }
            }
        }
    } //GEN-LAST:event_jList2MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jCheckBox1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jCheckBox1ActionPerformed
        jCheckBox1.setEnabled(false);
        new SwingWorker<CidsBean, Object>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    cidsBean.setProperty("test", jCheckBox1.isSelected());
                    return cidsBean.persist();
                }

                @Override
                protected void done() {
                    try {
                        final CidsBean cidsBean = get();
                        setCidsBean(cidsBean);
                    } catch (final Exception ex) {
                        final ErrorInfo errorInfo = new ErrorInfo(
                                "Fehler",
                                "Der Auftrag konnte nicht als abgearbeitet markiert werden.",
                                null,
                                null,
                                ex,
                                Level.ALL,
                                null);
                        JXErrorPane.showDialog(VermessungsunterlagenauftragRenderer.this, errorInfo);
                    } finally {
                        jCheckBox1.setEnabled(true);
                    }
                }
            }.execute();
    } //GEN-LAST:event_jCheckBox1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList4MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList4MouseClicked
        if (evt.getClickCount() > 1) {
            final int index = jList4.getSelectedIndex();
            final VermessungsunterlagenException ex = (VermessungsunterlagenException)
                ((DefaultListModel<VermessungsunterlagenException>)jList4.getModel()).getElementAt(index);
            final ErrorInfo errorInfo = new ErrorInfo(
                    "Fehler",
                    ex.getMessage()
                            + "\n\nSiehe Details.",
                    null,
                    null,
                    ex,
                    Level.ALL,
                    null);
            JXErrorPane.showDialog(this, errorInfo);
        }
    }                                                                      //GEN-LAST:event_jList4MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        exceptionDialog.setVisible(false);
        ((DefaultListModel<VermessungsunterlagenException>)jList4.getModel()).clear();
        jLabel9.setText("");
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

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
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        fsMap.clear();
        bindingGroup.unbind();
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean);
            initFeatures();
            initMap();

            bindingGroup.bind();
            setTitle(null);
            updateVermessungsstelle();
            if (jToggleButton1.isEnabled()) {
                buttonGroup1.setSelected(jToggleButton1.getModel(), true);
                jToggleButton1ActionPerformed(null);
            } else {
                buttonGroup1.setSelected(jToggleButton2.getModel(), true);
                jToggleButton2ActionPerformed(null);
            }

            for (final CidsBean flurstueck : cidsBean.getBeanCollectionProperty("flurstuecke")) {
                final String gemarkung = (String)flurstueck.getProperty("gemarkung");
                final String flur = (String)flurstueck.getProperty("flur");
                final String zaehlernenner = (String)flurstueck.getProperty("flurstueck");
                final String zaehler;
                final String nenner;
                if (zaehlernenner.contains("/")) {
                    final String[] split = zaehlernenner.split("/");
                    if (split.length != 2) {
                        continue;
                    }
                    zaehler = split[0];
                    nenner = split[1];
                } else {
                    zaehler = zaehlernenner;
                    nenner = null;
                }

                final String[] parts = VermessungsunterlagenUtils.createFlurstueckParts(
                        gemarkung,
                        flur,
                        zaehler,
                        nenner);
                if ((parts == null) || (parts.length != 4)) {
                    continue;
                }
                final String gemarkungPart = parts[0];
                final String flurPart = parts[1];
                final String zaehlerPart = parts[2];
                final String nennerPart = parts[3];

                final String name = gemarkungPart + "-" + flurPart + "-" + zaehlerPart + "/" + nennerPart;

                new SwingWorker<CidsBean, Object>() {

                        @Override
                        protected CidsBean doInBackground() throws Exception {
                            final CidsBean selBean = loadFsObject(
                                    gemarkungPart,
                                    flurPart,
                                    zaehlerPart,
                                    nennerPart);
                            return selBean;
                        }

                        @Override
                        protected void done() {
                            try {
                                final CidsBean cidsBean = (CidsBean)get();
                                if (cidsBean != null) {
                                    fsMap.put(name, cidsBean);
                                }
                            } catch (final Exception ex) {
                                LOG.warn(ex, ex);
                            }
                            jList2.revalidate();
                            jList2.repaint();
                        }
                    }.execute();
                ((DefaultListModel<String>)jList2.getModel()).addElement(name);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gemarkung  alkisId DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   zaehler    DOCUMENT ME!
     * @param   nenner     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean loadFsObject(final String gemarkung,
            final String flur,
            final String zaehler,
            final String nenner) throws Exception {
        final MetaObject[] mos = FlurstueckFinder.getLWLandparcel(gemarkung, flur, zaehler, nenner);

        if ((mos != null) && (mos.length > 0)) {
            final MetaObject mo = mos[0];
            final CidsBean kickerBean = SessionManager.getProxy()
                        .getMetaObject(mo.getId(), mo.getClassID(), "WUNDA_BLAU")
                        .getBean();

            return (CidsBean)kickerBean.getProperty("fs_referenz");
        } else {
            return null;
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
    public void setTitle(final String title) {
        if (cidsBean == null) {
            this.title = "<Error>";
        } else {
            // DATE_FORMAT.format((Timestamp)cidsBean.getProperty("timestamp"))
            this.title = "Bestellung Vermessungsunterlagen: "
                        + (String)cidsBean.getProperty("schluessel") + " - " + vermStelle;
        }
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
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
    }

    /**
     * DOCUMENT ME!
     */
    private void initFeatures() {
        final Integer saum = (Integer)cidsBean.getProperty("saumap");
        if (cidsBean != null) {
            final Geometry geometrie = CrsTransformer.transformToGivenCrs((Geometry)cidsBean.getProperty(
                        "geometrie.geo_field"),
                    AlkisConstants.COMMONS.SRS_SERVICE);
            final Geometry geometrieSaum = CrsTransformer.transformToGivenCrs(
                    ((Geometry)cidsBean.getProperty("geometrie.geo_field")).buffer((saum != null) ? saum : 0),
                    AlkisConstants.COMMONS.SRS_SERVICE);
            final Geometry geometrieFlurstuecke = CrsTransformer.transformToGivenCrs((Geometry)cidsBean.getProperty(
                        "geometrie_flurstuecke.geo_field"),
                    AlkisConstants.COMMONS.SRS_SERVICE);

            final StyledFeature geometrieFeature = new DefaultStyledFeature();
            geometrieFeature.setGeometry(geometrie);
            geometrieFeature.setFillingPaint(new Color(1, 0, 0, 0.5f));
            geometrieFeature.setLineWidth(1);
            geometrieFeature.setLinePaint(new Color(1, 0, 0, 1f));

            final StyledFeature geometrieSaumFeature = new DefaultStyledFeature();
            geometrieSaumFeature.setGeometry(geometrieSaum);
            geometrieSaumFeature.setFillingPaint(new Color(1, 0, 0, 0.5f));
            geometrieSaumFeature.setLineWidth(1);
            geometrieSaumFeature.setLinePaint(new Color(1, 0, 0, 1f));

            final StyledFeature flurstueckeFeature = new DefaultStyledFeature();
            flurstueckeFeature.setGeometry(geometrieFlurstuecke);
            flurstueckeFeature.setFillingPaint(new Color(1, 0, 0, 0.5f));
            flurstueckeFeature.setLineWidth(1);
            flurstueckeFeature.setLinePaint(new Color(1, 0, 0, 1f));
            this.geometrieFeature = geometrieFeature;
            this.geometrieSaumFeature = geometrieSaumFeature;
            this.flurstueckeFeature = flurstueckeFeature;
        } else {
            this.geometrieFeature = null;
            this.geometrieSaumFeature = null;
            this.flurstueckeFeature = null;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        if (cidsBean != null) {
            final Geometry combinedGeom;
            if ((flurstueckeFeature != null) && (flurstueckeFeature.getGeometry() != null)) {
                combinedGeom = geometrieSaumFeature.getGeometry().getEnvelope()
                            .union(flurstueckeFeature.getGeometry().getEnvelope());
                jToggleButton1.setEnabled(true);
            } else {
                combinedGeom = geometrieSaumFeature.getGeometry();
                jToggleButton1.setEnabled(false);
            }
            try {
                final XBoundingBox box = new XBoundingBox(combinedGeom.getEnvelope().buffer(
                            AlkisConstants.COMMONS.GEO_BUFFER));
                final Runnable mapRunnable = new Runnable() {

                        @Override
                        public void run() {
                            final ActiveLayerModel mappingModel = new ActiveLayerModel();
                            mappingModel.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);
                            mappingModel.addHome(new XBoundingBox(
                                    box.getX1(),
                                    box.getY1(),
                                    box.getX2(),
                                    box.getY2(),
                                    AlkisConstants.COMMONS.SRS_SERVICE,
                                    true));
                            final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                        AlkisConstants.COMMONS.MAP_CALL_STRING));
                            swms.setName("Vermessungsunterlagen-Auftrag");

                            // add the raster layer to the model
                            mappingModel.addLayer(swms);
                            // set the model
                            mappingComponent.setMappingModel(mappingModel);
                            // initial positioning of the map
                            mappingComponent.gotoInitialBoundingBox();
                            // interaction mode
                            mappingComponent.setInteractionMode(MappingComponent.ZOOM);
                            // finally when all configurations are done ...
                            mappingComponent.unlock();
                        }
                    };
                if (EventQueue.isDispatchThread()) {
                    mapRunnable.run();
                } else {
                    EventQueue.invokeLater(mapRunnable);
                }
            } catch (final Exception ex) {
                LOG.warn("could not init Map !", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void updateVermessungsstelle() {
        this.vermStelle = null;
        bindingGroup.getBinding("vermStelle").unbind();

        final String vermessungsstelle = (String)cidsBean.getProperty("vermessungsstelle");
        new SwingWorker<String, Void>() {

                @Override
                protected String doInBackground() throws Exception {
                    if (vermessungsstelle == null) {
                        return null;
                    } else if ("053290".equals(vermessungsstelle)) {
                        return "Stadt Wuppertal";
                    } else {
                        final CidsServerSearch search = new KundeByVermessungsStellenNummerSearch(
                                vermessungsstelle.substring(2));
                        final Collection res = SessionManager.getProxy()
                                    .customServerSearch(SessionManager.getSession().getUser(), search);
                        if ((res == null) || res.isEmpty()) {
                            return "nicht registrierte Vermessungsstelle";
                        } else {
                            return ((VermessungsStellenSearchResult)res.iterator().next()).getName();
                        }
                    }
                }

                @Override
                protected void done() {
                    try {
                        final String ret = get();
                        VermessungsunterlagenauftragRenderer.this.vermStelle = ret;
                        setTitle(null);
                    } catch (final Exception ex) {
                    } finally {
                        bindingGroup.getBinding("vermStelle").bind();
                    }
                }
            }.execute();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class AlkisFlurstueckInfoListCellRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList<?> list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final JLabel component = (JLabel)super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus); // To change body of generated methods, choose Tools | Templates.

            if (fsMap.containsKey((String)value)) {
                component.setEnabled(true);
                component.setText(fsMap.get((String)value).toString());
            } else {
                component.setEnabled(false);
            }
            return component;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class VermessungsunterlagenExceptionJsonDeserializer extends StdDeserializer<VermessungsunterlagenException> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CidsBeanJsonDeserializer object.
         */
        public VermessungsunterlagenExceptionJsonDeserializer() {
            super(VermessungsunterlagenException.class);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public VermessungsunterlagenException deserialize(final JsonParser jp, final DeserializationContext dc)
                throws IOException, JsonProcessingException {
            final ObjectNode on = jp.readValueAsTree();
            if (on.has("type")) {
                final JsonNode typeNode = on.get("type");
                final VermessungsunterlagenException.Type type = (VermessungsunterlagenException.Type)
                    objectMapper.treeToValue(typeNode, VermessungsunterlagenException.Type.class);
                switch (type) {
                    case VALIDATOR: {
                        return objectMapper.treeToValue(on, VermessungsunterlagenValidatorException.class);
                    }
                    case JOB: {
                        return objectMapper.treeToValue(on, VermessungsunterlagenJobException.class);
                    }
                    case TASK: {
                        return objectMapper.treeToValue(on, VermessungsunterlagenTaskException.class);
                    }
                    case OTHER: {
                        return objectMapper.treeToValue(on, VermessungsunterlagenException.class);
                    }
                }
            }
            throw new RuntimeException("invalid VermessungsunterlagenException");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class VermessungsunterlagenTaskExceptionJsonDeserializer
            extends StdDeserializer<VermessungsunterlagenTaskException> {

        //~ Instance fields ----------------------------------------------------

        private final ObjectMapper defaultMapper = new ObjectMapper();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CidsBeanJsonDeserializer object.
         */
        public VermessungsunterlagenTaskExceptionJsonDeserializer() {
            super(VermessungsunterlagenTaskException.class);
            defaultMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public VermessungsunterlagenTaskException deserialize(final JsonParser jp, final DeserializationContext dc)
                throws IOException, JsonProcessingException {
            final ObjectNode on = jp.readValueAsTree();
            if (on.has("taskExceptionType")) {
                final JsonNode typeNode = on.get("taskExceptionType");
                final VermessungsunterlagenTaskException.TaskExceptionType taskExceptionType =
                    (VermessungsunterlagenTaskException.TaskExceptionType)objectMapper.treeToValue(
                        typeNode,
                        VermessungsunterlagenTaskException.TaskExceptionType.class);
                switch (taskExceptionType) {
                    case RETRY: {
                        return objectMapper.treeToValue(on, VermessungsunterlagenTaskRetryException.class);
                    }
                    case SINGLE: {
                        return defaultMapper.treeToValue(on, VermessungsunterlagenTaskException.class);
                    }
                }
            }
            throw new RuntimeException("invalid VermessungsunterlagenTaskException");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class VermessungsunterlagenExceptionListCellRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final VermessungsunterlagenException ex = (VermessungsunterlagenException)value;
            final String newvalue = DATE_FORMAT.format(new Date(new Double(ex.getTimeMillis()).longValue()))
                        + " " + ex.getMessage();
            final Component component = super.getListCellRendererComponent(
                    list,
                    newvalue,
                    index,
                    isSelected,
                    cellHasFocus);
            return component;
        }
    }
}
