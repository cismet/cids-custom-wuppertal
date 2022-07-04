/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.commons.lang.StringUtils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.image.BufferedImage;

import java.sql.Timestamp;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import de.cismet.cids.client.tools.ReportLookupButton;
import de.cismet.cids.custom.wunda_blau.search.server.ZaehlungLastYearsSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;
import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.TitleComponentProvider;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import org.openide.util.Exceptions;

/**
 * DOCUMENT ME!
 *
 * @author   nh  update 06/22 Sandra
 * @version  $Revision$, $Date$
 */
public class ZaehlungsstandortRenderer extends JPanel implements CidsBeanRenderer, TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------
    public static final String FIELD__WETTER = "Wetter";                                       
    public static final String FIELD__DATUM = "datum";                                       
    public static final String FIELD__ANZAHL = "anzahl";                                       
    public static final String FIELD__WETTER_WETTER = "wetter.wetter";                                       
    public static final String FIELD__BEZIRK = "bezirk";                                       
    public static final String FIELD__ZAEHLUNGEN = "zaehlungen";                                       
    public static final String FIELD__GEO_FIELD = "georeferenz.geo_field";                                       
    public static final String FIELD__STADTTEIL = "stadtteil.stadtteil";                                 
    public static final String FIELD__STANDORT = "standort";                                      
    public static final String AVG_TEXT = "Durchschnitt \u00D8";    
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            "de.cismet.cids.objectrenderer.CoolPassantenfrequenzRenderer");
    private static final String SONNE = "sonne";
    private static final String LEICHTER_BEW = "leichter bewoelkt";
    private static final String LEICHT_BEW = "leicht bewoelkt";
    private static final String BEWOELKT = "bewoelkt";
    private static final String STARK_BEW = "stark bewoelkt";
    private static final String REGEN = "regen";
    private static final String GEWITTER = "gewitter";
    private static final String WECHSELHAFT = "wechselhaft";
    private static final String SCHNEE = "schnee";
    private static final String SCHNEE_WECHSEL = "schnee wechselhaft";
    private static final Color COLOR_MO = new Color(255, 255, 0);
    private static final Color COLOR_DI = new Color(255, 150, 0);
    private static final Color COLOR_MI = new Color(200, 0, 0);
    private static final Color COLOR_DO = new Color(180, 50, 180);
    private static final Color COLOR_FR = new Color(0, 0, 230);
    private static final Color COLOR_SA = new Color(0, 170, 0);
    private static final Color COLOR_SO = new Color(50, 50, 50);
    private static final Paint LIGHT_MO = new GradientPaint(0.0f, 0.0f, COLOR_MO, 0.0f, 0.0f, new Color(255, 255, 180));
    private static final Paint LIGHT_DI = new GradientPaint(0.0f, 0.0f, COLOR_DI, 0.0f, 0.0f, new Color(255, 220, 180));
    private static final Paint LIGHT_MI = new GradientPaint(0.0f, 0.0f, COLOR_MI, 0.0f, 0.0f, new Color(240, 175, 150));
    private static final Paint LIGHT_DO = new GradientPaint(0.0f, 0.0f, COLOR_DO, 0.0f, 0.0f, new Color(230, 190, 230));
    private static final Paint LIGHT_FR = new GradientPaint(0.0f, 0.0f, COLOR_FR, 0.0f, 0.0f, new Color(175, 175, 250));
    private static final Paint LIGHT_SA = new GradientPaint(0.0f, 0.0f, COLOR_SA, 0.0f, 0.0f, new Color(150, 210, 150));
    private static final Paint LIGHT_SO = new GradientPaint(0.0f, 0.0f, COLOR_SO, 0.0f, 0.0f, new Color(180, 180, 180));
    private static final String STRING_MO = "Mo";
    private static final String STRING_DI = "Tu";
    private static final String STRING_MI = "We";
    private static final String STRING_DO = "Th";
    private static final String STRING_FR = "Fr";
    private static final String STRING_SA = "Sa";
    private static final String STRING_SO = "Su";
    public static final String TITLE = "Passantenfrequenzz\u00E4hlung";

    //~ Instance fields --------------------------------------------------------

    private final TreeMap<String, int[]> jahresDurchschnitt = new TreeMap();
    private final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                ZaehlungsstandortRenderer.class.getSimpleName());
    
    private CidsBean cidsBean;
    private final ZaehlungLastYearsSearch yearsSearch = new ZaehlungLastYearsSearch();

    private final ImageIcon ICON_SONNE = new ImageIcon(getClass().getResource("/res/16/sonne.png"));
    private final ImageIcon ICON_LEICHTER_BEW = new ImageIcon(getClass().getResource("/res/16/leichter_bew.png"));
    private final ImageIcon ICON_LEICHT_BEW = new ImageIcon(getClass().getResource("/res/16/leicht_bew.png"));
    private final ImageIcon ICON_BEWOELKT = new ImageIcon(getClass().getResource("/res/16/bewoelkt.png"));
    private final ImageIcon ICON_STARK_BEW = new ImageIcon(getClass().getResource("/res/16/stark_bewoelkt.png"));
    private final ImageIcon ICON_REGEN = new ImageIcon(getClass().getResource("/res/16/regen.png"));
    private final ImageIcon ICON_GEWITTER = new ImageIcon(getClass().getResource("/res/16/gewitter.png"));
    private final ImageIcon ICON_WECHSELHAFT = new ImageIcon(getClass().getResource("/res/16/wechselhaft.png"));
    private final ImageIcon ICON_SCHNEE = new ImageIcon(getClass().getResource("/res/16/schnee.png"));
    private final ImageIcon ICON_SCHNEE_WECHSEL = new ImageIcon(getClass().getResource("/res/16/schnee_wechsel.png"));
    private final ArrayList<String> colNames = new ArrayList<>();
    private final ArrayList<String> colNamesJahr = new ArrayList<>();
    private DefaultCategoryDataset dataset;
    private DefaultCategoryDataset datasetJahr;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cmdPrint;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelDaten;
    private javax.swing.JPanel jPanelDaten1;
    private javax.swing.JPanel jPanelDaten2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel lblChartAlle;
    private javax.swing.JLabel lblChartJahre;
    private javax.swing.JLabel lblChartLetzte;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panInhalt;
    private javax.swing.JPanel panInhalt1;
    private javax.swing.JPanel panInhalt2;
    private javax.swing.JPanel panLegend;
    private javax.swing.JPanel panLegend2;
    private de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel panMapPreview;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel pnlCard1;
    private javax.swing.JTable tabFrequenzenAlle;
    private javax.swing.JTable tabFrequenzenJahre;
    private javax.swing.JTable tabFrequenzenLetzte;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CoolPassantenfrequenzRenderer.
     */
    public ZaehlungsstandortRenderer() {
        colNamesJahr.add("Jahr");
        colNamesJahr.add("Anz/h-Mittel");
        colNames.add("Datum");
        colNames.add("Anzahl/h");
        colNames.add("Wetter");
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        cmdPrint = new  ReportLookupButton("PFZReport");
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        pnlCard1 = new javax.swing.JPanel();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        panInhalt = new javax.swing.JPanel();
        jPanelDaten = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabFrequenzenAlle = new JTable() {
            public boolean isCellEditable(int x, int y) {
                return false;
            }
        };
        panLegend = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lblChartAlle = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        panInhalt1 = new javax.swing.JPanel();
        jPanelDaten1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabFrequenzenLetzte = new JTable() {
            public boolean isCellEditable(int x, int y) {
                return false;
            }
        };
        jScrollPane4 = new javax.swing.JScrollPane();
        lblChartLetzte = new javax.swing.JLabel();
        panLegend2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        panInhalt2 = new javax.swing.JPanel();
        jPanelDaten2 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabFrequenzenJahre = new JTable() {
            public boolean isCellEditable(int x, int y) {
                return false;
            }
        };
        jScrollPane6 = new javax.swing.JScrollPane();
        lblChartJahre = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        panMapPreview = new de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Passantenfrequenzzählung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 0);
        panTitle.add(lblTitle, gridBagConstraints);

        cmdPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/printer.png"))); // NOI18N
        cmdPrint.setBorderPainted(false);
        cmdPrint.setContentAreaFilled(false);
        cmdPrint.setFocusPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 22);
        panTitle.add(cmdPrint, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(filler1, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new java.awt.GridBagLayout());

        jTabbedPane.setMinimumSize(new java.awt.Dimension(849, 520));

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panInhalt.setOpaque(false);
        panInhalt.setLayout(new java.awt.GridBagLayout());

        jPanelDaten.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(230, 200));

        tabFrequenzenAlle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Datum", "Anzahl"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabFrequenzenAlle.setGridColor(new java.awt.Color(153, 153, 153));
        tabFrequenzenAlle.setSelectionBackground(new java.awt.Color(153, 204, 255));
        tabFrequenzenAlle.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabFrequenzenAlle);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        jPanelDaten.add(jScrollPane1, gridBagConstraints);

        panLegend.setMinimumSize(new java.awt.Dimension(125, 35));
        panLegend.setOpaque(false);
        panLegend.setPreferredSize(new java.awt.Dimension(125, 35));
        panLegend.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Mo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jLabel4, gridBagConstraints);

        jLabel5.setText("Di");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Mi");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jLabel6, gridBagConstraints);

        jLabel7.setText("Do");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jLabel7, gridBagConstraints);

        jLabel8.setText("Fr");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jLabel8, gridBagConstraints);

        jLabel9.setText("Sa");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jLabel9, gridBagConstraints);

        jLabel10.setText("So");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jLabel10, gridBagConstraints);

        jPanel8.setBackground(new java.awt.Color(255, 255, 0));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel8.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel8.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanel8.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jPanel8, gridBagConstraints);

        jPanel9.setBackground(new java.awt.Color(255, 153, 0));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel9.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel9.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanel9.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jPanel9, gridBagConstraints);

        jPanel10.setBackground(new java.awt.Color(204, 0, 0));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel10.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel10.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanel10.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jPanel10, gridBagConstraints);

        jPanel11.setBackground(new java.awt.Color(180, 50, 180));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel11.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel11.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanel11.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jPanel11, gridBagConstraints);

        jPanel12.setBackground(new java.awt.Color(51, 51, 51));
        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel12.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel12.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanel12.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jPanel12, gridBagConstraints);

        jPanel13.setBackground(new java.awt.Color(0, 170, 0));
        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel13.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel13.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanel13.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jPanel13, gridBagConstraints);

        jPanel14.setBackground(new java.awt.Color(0, 0, 230));
        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel14.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel14.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanel14.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend.add(jPanel14, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        jPanelDaten.add(panLegend, gridBagConstraints);

        lblChartAlle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblChartAlle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/load.png"))); // NOI18N
        lblChartAlle.setPreferredSize(new java.awt.Dimension(1000, 400));
        jScrollPane2.setViewportView(lblChartAlle);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelDaten.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panInhalt.add(jPanelDaten, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel1.add(panInhalt, gridBagConstraints);

        jTabbedPane.addTab("alle Werte", jPanel1);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        panInhalt1.setOpaque(false);
        panInhalt1.setLayout(new java.awt.GridBagLayout());

        jPanelDaten1.setLayout(new java.awt.GridBagLayout());

        jScrollPane3.setPreferredSize(new java.awt.Dimension(230, 200));

        tabFrequenzenLetzte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Datum", "Anzahl"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabFrequenzenLetzte.setGridColor(new java.awt.Color(153, 153, 153));
        tabFrequenzenLetzte.setSelectionBackground(new java.awt.Color(153, 204, 255));
        tabFrequenzenLetzte.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabFrequenzenLetzte);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        jPanelDaten1.add(jScrollPane3, gridBagConstraints);

        lblChartLetzte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblChartLetzte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/load.png"))); // NOI18N
        lblChartLetzte.setPreferredSize(new java.awt.Dimension(1000, 400));
        jScrollPane4.setViewportView(lblChartLetzte);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelDaten1.add(jScrollPane4, gridBagConstraints);

        panLegend2.setMinimumSize(new java.awt.Dimension(125, 35));
        panLegend2.setOpaque(false);
        panLegend2.setPreferredSize(new java.awt.Dimension(125, 35));
        panLegend2.setLayout(new java.awt.GridBagLayout());

        jLabel18.setText("Mo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jLabel18, gridBagConstraints);

        jLabel19.setText("Di");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jLabel19, gridBagConstraints);

        jLabel20.setText("Mi");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jLabel20, gridBagConstraints);

        jLabel21.setText("Do");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jLabel21, gridBagConstraints);

        jLabel22.setText("Fr");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jLabel22, gridBagConstraints);

        jLabel23.setText("Sa");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jLabel23, gridBagConstraints);

        jLabel24.setText("So");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jLabel24, gridBagConstraints);

        jPanel22.setBackground(new java.awt.Color(255, 255, 0));
        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel22.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jPanel22, gridBagConstraints);

        jPanel23.setBackground(new java.awt.Color(255, 153, 0));
        jPanel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel23.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jPanel23, gridBagConstraints);

        jPanel24.setBackground(new java.awt.Color(204, 0, 0));
        jPanel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel24.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jPanel24, gridBagConstraints);

        jPanel25.setBackground(new java.awt.Color(180, 50, 180));
        jPanel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel25.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jPanel25, gridBagConstraints);

        jPanel26.setBackground(new java.awt.Color(51, 51, 51));
        jPanel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel26.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jPanel26, gridBagConstraints);

        jPanel27.setBackground(new java.awt.Color(0, 170, 0));
        jPanel27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel27.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jPanel27, gridBagConstraints);

        jPanel28.setBackground(new java.awt.Color(0, 0, 230));
        jPanel28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel28.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLegend2.add(jPanel28, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        jPanelDaten1.add(panLegend2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panInhalt1.add(jPanelDaten1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel3.add(panInhalt1, gridBagConstraints);

        jTabbedPane.addTab("letzten zwei Jahre", jPanel3);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        panInhalt2.setOpaque(false);
        panInhalt2.setLayout(new java.awt.GridBagLayout());

        jPanelDaten2.setLayout(new java.awt.GridBagLayout());

        jScrollPane5.setPreferredSize(new java.awt.Dimension(230, 200));

        tabFrequenzenJahre.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Datum", "Anzahl"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabFrequenzenJahre.setGridColor(new java.awt.Color(153, 153, 153));
        tabFrequenzenJahre.setSelectionBackground(new java.awt.Color(153, 204, 255));
        tabFrequenzenJahre.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tabFrequenzenJahre);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        jPanelDaten2.add(jScrollPane5, gridBagConstraints);

        lblChartJahre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblChartJahre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/load.png"))); // NOI18N
        lblChartJahre.setPreferredSize(new java.awt.Dimension(1000, 400));
        jScrollPane6.setViewportView(lblChartJahre);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(35, 0, 0, 0);
        jPanelDaten2.add(jScrollPane6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panInhalt2.add(jPanelDaten2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel4.add(panInhalt2, gridBagConstraints);

        jTabbedPane.addTab("Jahresdurchschnitt", jPanel4);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel2.add(panMapPreview, gridBagConstraints);

        jTabbedPane.addTab("Karte", jPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCard1.add(jTabbedPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlCard1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     * @param werte
     * @param chartLabel
     * @param tabelle
     */
    public void fillTableAndCreateChart(final List<CidsBean> werte,
            final JLabel chartLabel,
            final JTable tabelle) {
        if (!werte.isEmpty()) {
            final Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                dataset = new DefaultCategoryDataset();

                                final TreeMap<Date, Integer> tmAnzahl = new TreeMap<>();
                                final TreeMap<Date, String> tmWetter = new TreeMap<>();

                                final DefaultTableModel model = new DefaultTableModel(
                                        colNames.toArray(),
                                        werte.size()
                                                + 1);
                                tabelle.setModel(model);
                                if (tabelle.getColumn(FIELD__WETTER) != null) {
                                    tabelle.getColumn(FIELD__WETTER).setCellRenderer(new WetterRenderer());
                                }

                                double avg = 0.0d;
                                for (final CidsBean zaehlung : werte) {
                                    final Timestamp datum = (Timestamp)zaehlung.getProperty(FIELD__DATUM);
                                    final Date key = new Date(datum.getTime());

                                    final Integer anzahl = (Integer)zaehlung.getProperty(FIELD__ANZAHL);
                                    tmAnzahl.put(key, anzahl);
                                    
                                    final String wetter = (String)zaehlung.getProperty(FIELD__WETTER_WETTER);
                                    if (wetter != null) {
                                        tmWetter.put(key, wetter);
                                    }
                                }

                                int i = 0;
                                for (final Date s : tmAnzahl.keySet()) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("KEY=" + s);
                                        LOG.debug("ANZAHL=" + tmAnzahl.get(s));
                                    }
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("WETTER=" + tmWetter.get(s));
                                    }
                                   
                                    final int wert = tmAnzahl.get(s) * 12;
                                    avg += wert;
                                    model.setValueAt(
                                        DateFormat.getDateTimeInstance(
                                            DateFormat.SHORT,
                                            DateFormat.SHORT,
                                            Locale.GERMANY).format(s),
                                        i
                                                + 1,
                                        0);
                                    model.setValueAt(wert, i + 1, 1);
                                   
                                    if (tmWetter.get(s) != null) {
                                        model.setValueAt(
                                            tmWetter.get(s),
                                            i
                                                    + 1,
                                            tabelle.getColumn(FIELD__WETTER).getModelIndex());
                                    }

                                    dataset.addValue(
                                        wert,
                                        "Daten",
                                        DateFormat.getDateTimeInstance(
                                            DateFormat.SHORT,
                                            DateFormat.SHORT,
                                            Locale.GERMANY).format(s));
                                    i++;
                                    tabelle.getColumnModel().getColumn(0).setPreferredWidth(150);
                                }
                                avg = avg / werte.size();
                                model.setValueAt(AVG_TEXT, 0, 0);
                                model.setValueAt(Math.round(avg), 0, 1);
                                final JFreeChart chart = createChart(dataset, avg);
                                chart.setBackgroundPaint(new Color(200, 200, 200));

                                final BufferedImage icon = chart.createBufferedImage(1000, 400);
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            chartLabel.setIcon(new ImageIcon(icon));
                                        }
                                    });
                            } catch (Exception exception) {
                                LOG.error("Error while filling the table and creating the chart", exception);
                            }
                        }
                    });
            t.start();
        }
    }

    public void fillTableAndCreateYearChart(final List<CidsBean> werte) {
        if (!werte.isEmpty()) {
            final ArrayList<Timestamp> datum = new ArrayList<>(werte.size());
            final ArrayList<Integer> anzahl = new ArrayList<>(werte.size());

            for (final CidsBean wert : werte) {
                datum.add((Timestamp)wert.getProperty(FIELD__DATUM));
                anzahl.add((Integer)wert.getProperty(FIELD__ANZAHL));
            }
            final Thread t = new Thread(new Runnable() {

                        @Override
                            public void run() {
                                datasetJahr = new DefaultCategoryDataset();
                                ///String max = "1000";
                                Integer summe = 0;
                                
                                // Daten in HashMaps eintragen
                                for (int i = 0; i < datum.size(); i++) {
                                    try {
                                        String jahr = DateFormat.getDateInstance(DateFormat.YEAR_FIELD, Locale.GERMANY)
                                                    .format(datum.get(i));
                                        jahr = jahr.substring(jahr.length() - 4);
                                        final int wert = anzahl.get(i) * 12;
                                        if (jahresDurchschnitt.get(jahr) != null) {
                                            final int[] tmp = jahresDurchschnitt.get(jahr);
                                            tmp[0] += wert;
                                            tmp[1] += 1;
                                            jahresDurchschnitt.put(jahr, tmp);
                                        } else {
                                            final int[] newArr = { wert, 1 };
                                            jahresDurchschnitt.put(jahr, newArr);
                                        }
                                        summe = summe + wert;
                                    } catch (Exception ex) {
                                        LOG.error("Error beim Erstellen des FeatureRenderers", ex);
                                    }
                                }

                                // Daten aus HashMaps in DefaultCategoryDataset eintragen
                                for (final String key : jahresDurchschnitt.keySet()) {
                                    datasetJahr.addValue((int)(Math.round(jahresDurchschnitt.get(key)[0] / jahresDurchschnitt.get(key)[1])),
                                        "Daten",
                                        key);
                                    
                                }
                                
                                final DefaultTableModel modelJahr = new DefaultTableModel(
                                        colNamesJahr.toArray(),
                                        datasetJahr.getColumnCount()+1);
                                tabFrequenzenJahre.setModel(modelJahr);
                                for (int i=0; i<datasetJahr.getColumnCount(); i++){
                                    modelJahr.setValueAt(
                                        datasetJahr.getColumnKey(i),
                                        i + 1,
                                        0);
                                    modelJahr.setValueAt(
                                        datasetJahr.getValue(0, i).intValue(),
                                        i + 1,
                                        1);
                                }
                                modelJahr.setValueAt(AVG_TEXT , 0, 0);
                                Integer durchschnitt = Math.round(summe / werte.size());
                                modelJahr.setValueAt(durchschnitt , 0, 1);
                                final JFreeChart chart = createChart(datasetJahr, durchschnitt);
                                chart.setBackgroundPaint(new Color(210, 210, 210));
                                final BufferedImage icon = chart.createBufferedImage(1000, 400);
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            lblChartJahre.setIcon(new ImageIcon(icon));
                                        }
                                    });
                            }
                    });
            t.start();
        }
    }
    
    /**
     * Erzeugt ein Diagramm fuer Passantenfrequenzen.
     *
     * @param   dataset  anzuzeigende Daten
     * @param   average  DOCUMENT ME!
     *
     * @return  JFreeChart-Objekt
     */
    JFreeChart createChart(final CategoryDataset dataset, final double average) {
        final JFreeChart chart = ChartFactory.createBarChart3D(
                null,
                null,
                null,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        final CategoryPlot plot = chart.getCategoryPlot();
        final CustomBarRenderer renderer = new CustomBarRenderer();
        plot.setRenderer(renderer);

        // X-Achsen Label nicht anzeigen
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible(true);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        final ValueMarker markerAvg = new ValueMarker(average,
                new Color(100, 100, 255),
                new BasicStroke(2.5f));

        plot.addRangeMarker(markerAvg, Layer.BACKGROUND);
        final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setVisible(true);
        rangeAxis.setAutoRange(true);
        return chart;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            ((ReportLookupButton)(cmdPrint)).setBean(cidsBean);
            final List<CidsBean> zaehlungen = cidsBean.getBeanCollectionProperty(FIELD__ZAEHLUNGEN);
            fillTableAndCreateChart(zaehlungen, lblChartAlle, tabFrequenzenAlle);
            fillTableAndCreateYearChart(zaehlungen);
            panMapPreview.initMap(cidsBean, FIELD__GEO_FIELD);
            yearsSearch.setStandortId(cidsBean.getPrimaryKeyValue());
            try {
                Collection<MetaObjectNode> colMONZaehlung = SessionManager.getProxy().customServerSearch(
                        SessionManager.getSession().getUser(),
                        yearsSearch,
                        getConnectionContext());
                final List<CidsBean> yearBeans = new ArrayList<>();
                    for (final MetaObjectNode mon : colMONZaehlung) {
                        yearBeans.add(SessionManager.getProxy().getMetaObject(
                                mon.getObjectId(),
                                mon.getClassId(),
                                "WUNDA_BLAU",
                                getConnectionContext()).getBean());
                    }
                    fillTableAndCreateChart(yearBeans, lblChartLetzte, tabFrequenzenLetzte);
            } catch (ConnectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        return lblTitle.getText();
    }

    @Override
    public void setTitle(final String title) {
    }

    @Override
    public JComponent getTitleComponent() {
        final String stadtteil = (String)cidsBean.getProperty(FIELD__STADTTEIL);
        String title = TITLE;
        if (StringUtils.isNotBlank(stadtteil)) {
            title += " - " + stadtteil;
        }

        final Integer bezirk = (Integer)cidsBean.getProperty(FIELD__BEZIRK);
        if (bezirk != null) {
            title += " (Bezirk " + bezirk.toString();
        }

        final Integer standort = (Integer)cidsBean.getProperty(FIELD__STANDORT);
        if (standort != null) {
            title += ", Standort " + standort.toString() + ")";
        } else {
            title += ")";
        }

        lblTitle.setText(title);
        return panTitle;
    }

    
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class WetterRenderer extends DefaultTableCellRenderer {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   table       DOCUMENT ME!
         * @param   value       DOCUMENT ME!
         * @param   isSelected  DOCUMENT ME!
         * @param   hasFocus    DOCUMENT ME!
         * @param   row         DOCUMENT ME!
         * @param   column      DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            try {
                if (value != null) {
                    final String wetter = value.toString().toLowerCase();
                    switch (wetter) {
                        case SONNE:
                            return new JLabel(ICON_SONNE);
                        case LEICHTER_BEW:
                            return new JLabel(ICON_LEICHTER_BEW);
                        case LEICHT_BEW:
                            return new JLabel(ICON_LEICHT_BEW);
                        case WECHSELHAFT:
                            return new JLabel(ICON_WECHSELHAFT);
                        case BEWOELKT:
                            return new JLabel(ICON_BEWOELKT);
                        case STARK_BEW:
                            return new JLabel(ICON_STARK_BEW);
                        case REGEN:
                            return new JLabel(ICON_REGEN);
                        case GEWITTER:
                            return new JLabel(ICON_GEWITTER);
                        case SCHNEE:
                            return new JLabel(ICON_SCHNEE);
                        case SCHNEE_WECHSEL:
                            return new JLabel(ICON_SCHNEE_WECHSEL);
                        default:
                            return new JLabel("");
                    }
                }
            } catch (Exception exception) {
                LOG.warn("Wetterrendererfehler", exception);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class CustomBarRenderer extends BarRenderer {

        //~ Instance fields ----------------------------------------------------

        private boolean paintLight = false;

        //~ Constructors -------------------------------------------------------

        /**
         * public CustomBarRenderer3D(double xOffset, double yOffset) { super(xOffset, yOffset); }.
         */
        public CustomBarRenderer() {
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Returns the paint for an item. Overrides the default behaviour inherited from AbstractSeriesRenderer.
         *
         * @param   row     the series.
         * @param   column  the category.
         *
         * @return  The item color.
         */
        @Override
        public Paint getItemPaint(final int row, final int column) {
            try {
                final CategoryDataset dataset = getPlot().getDataset();
                final String key = (String)dataset.getColumnKey(column);
                final Date date = DateFormat.getInstance().parse(key);
                final Calendar mittag = Calendar.getInstance();
                mittag.setTime(date);
                mittag.set(Calendar.HOUR_OF_DAY,12);
                mittag.set(Calendar.MINUTE, 0);
                mittag.set(Calendar.SECOND, 0);
                paintLight = date.before(mittag.getTime());
                if (date.toString().startsWith(STRING_MO)) {
                    if (paintLight) {
                        return LIGHT_MO;
                    } else {
                        return COLOR_MO;
                    }
                } else if (date.toString().startsWith(STRING_DI)) {
                    if (paintLight) {
                        return LIGHT_DI;
                    } else {
                        return COLOR_DI;
                    }
                } else if (date.toString().startsWith(STRING_MI)) {
                    if (paintLight) {
                        return LIGHT_MI;
                    } else {
                        return COLOR_MI;
                    }
                } else if (date.toString().startsWith(STRING_DO)) {
                    if (paintLight) {
                        return LIGHT_DO;
                    } else {
                        return COLOR_DO;
                    }
                } else if (date.toString().startsWith(STRING_FR)) {
                    if (paintLight) {
                        return LIGHT_FR;
                    } else {
                        return COLOR_FR;
                    }
                } else if (date.toString().startsWith(STRING_SA)) {
                    if (paintLight) {
                        return LIGHT_SA;
                    } else {
                        return COLOR_SA;
                    }
                } else if (date.toString().startsWith(STRING_SO)) {
                    if (paintLight) {
                        return LIGHT_SO;
                    } else {
                        return COLOR_SO;
                    }
                } else {
                    return Color.BLACK;
                }
            } catch (ParseException exception) {
                LOG.error("FEHLER", exception);
                return Color.BLACK;
            }
        }
    }
}
