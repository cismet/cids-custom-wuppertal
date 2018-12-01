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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.util.ShapeUtilities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;

import de.cismet.cids.custom.reports.wunda_blau.GrundwassermessstellenReportBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class GrundwassermessstelleMesswerteDiagrammPanel extends javax.swing.JPanel
        implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(GrundwassermessstelleMesswerteDiagrammPanel.class);

    public static final DataFlavor STOFF_BEAN_FLAVOR = new DataFlavor(
            DataFlavor.javaJVMLocalObjectMimeType,
            "DiagrammStoffBean"); // NOI18N

    //~ Instance fields --------------------------------------------------------

    private Collection<CidsBean> messungBeans;
    private final ConnectionContext connectionContext;
    private final Shape[] shapes = new Shape[] {
            ShapeUtilities.createDiagonalCross(3, 1),
            ShapeUtilities.createDiamond(5),
            ShapeUtilities.createDownTriangle(5),
            ShapeUtilities.createRegularCross(3, 1),
            ShapeUtilities.createUpTriangle(5)
        };

    private final Paint[] colors = new Paint[] {
//            Color.RED,
//            Color.BLUE,
//            Color.BLACK,
//            Color.YELLOW,
//            Color.CYAN,
//            Color.PINK,
//            Color.GREEN
            new Color(247, 150, 70, 192),
            new Color(155, 187, 89, 192),
            new Color(128, 100, 162, 192),
            new Color(75, 172, 198, 192),
            new Color(192, 80, 77, 192)
        };
    private List<CidsBean> stoffBeans;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.custom.objecteditors.wunda_blau.GrundwassermessstelleDiagrammAxisPanel
        grundwassermessstelleDiagrammAxisPanel4;
    private de.cismet.cids.custom.objecteditors.wunda_blau.GrundwassermessstelleDiagrammAxisPanel
        grundwassermessstelleDiagrammAxisPanel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GrundwassermessstelleMesswerteDiagrammPanel object.
     */
    public GrundwassermessstelleMesswerteDiagrammPanel() {
        this(ConnectionContext.createDummy());
    }

    /**
     * Creates new form GrundwassermessstelleMessungenDiagrammPanel.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public GrundwassermessstelleMesswerteDiagrammPanel(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   axisPanel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<GrundwassermessstellenReportBean.LegendeBean> getLegendBeans(
            final GrundwassermessstelleDiagrammAxisPanel axisPanel) {
        final List<GrundwassermessstellenReportBean.LegendeBean> legendBeans = new ArrayList<>();
        for (final CidsBean stoffBean : axisPanel.getEnabledStoffBeans()) {
            final Icon icon = createIcon((String)stoffBean.getProperty("schluessel"));
            final BufferedImage image = new BufferedImage(icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.drawRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
            icon.paintIcon(null, g2d, 0, 0);
            final GrundwassermessstellenReportBean.LegendeBean legendBean =
                new GrundwassermessstellenReportBean.LegendeBean(stoffBean, image);
            legendBeans.add(legendBean);
        }
        return legendBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<GrundwassermessstellenReportBean.LegendeBean> getLegendLeftBeans() {
        return getLegendBeans(grundwassermessstelleDiagrammAxisPanel4);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<GrundwassermessstellenReportBean.LegendeBean> getLegendRightBeans() {
        return getLegendBeans(grundwassermessstelleDiagrammAxisPanel5);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  messungBeans  DOCUMENT ME!
     */
    public void setMessungBeans(final Collection<CidsBean> messungBeans) {
        this.messungBeans = messungBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> getMessungBeans() {
        return messungBeans;
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshChart() {
        getChartPanel().setChart(createChartPanel());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stoffBeans  DOCUMENT ME!
     */
    public void setStoffBeans(final List<CidsBean> stoffBeans) {
        this.stoffBeans = stoffBeans;
        grundwassermessstelleDiagrammAxisPanel4.setStoffBeans(stoffBeans);
        grundwassermessstelleDiagrammAxisPanel5.setStoffBeans(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ChartPanel getChartPanel() {
        return (ChartPanel)jPanel2;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new ChartPanel(null);
        grundwassermessstelleDiagrammAxisPanel4 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.GrundwassermessstelleDiagrammAxisPanel(this);
        grundwassermessstelleDiagrammAxisPanel5 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.GrundwassermessstelleDiagrammAxisPanel(this);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(jPanel2, gridBagConstraints);

        grundwassermessstelleDiagrammAxisPanel4.setAxisName(null);
        grundwassermessstelleDiagrammAxisPanel4.setMinimumSize(new java.awt.Dimension(215, 43));
        grundwassermessstelleDiagrammAxisPanel4.setOpaque(false);
        grundwassermessstelleDiagrammAxisPanel4.setPreferredSize(new java.awt.Dimension(215, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(grundwassermessstelleDiagrammAxisPanel4, gridBagConstraints);

        grundwassermessstelleDiagrammAxisPanel5.setAxisName(null);
        grundwassermessstelleDiagrammAxisPanel5.setMinimumSize(new java.awt.Dimension(215, 43));
        grundwassermessstelleDiagrammAxisPanel5.setOpaque(false);
        grundwassermessstelleDiagrammAxisPanel5.setPreferredSize(new java.awt.Dimension(215, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(grundwassermessstelleDiagrammAxisPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel3, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param   axisPanel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private TimeSeriesCollection createDataSet(final GrundwassermessstelleDiagrammAxisPanel axisPanel) {
        final TimeSeriesCollection dataset = new TimeSeriesCollection();

        for (final CidsBean stoffBean : axisPanel.getEnabledStoffBeans()) {
            final String einheit = (String)stoffBean.getProperty("einheit");
            final String name = (String)stoffBean.getProperty("name");
            final TimeSeries series = new TimeSeries(name + ((einheit != null) ? (" (" + einheit + ")") : ""));
            final String schluessel = (String)stoffBean.getProperty("schluessel");
            series.setKey(schluessel);
            for (final CidsBean messungBean : messungBeans) {
                final Date datum = (Date)messungBean.getProperty("datum");
                for (final CidsBean messwertBean : messungBean.getBeanCollectionProperty("messwerte")) {
                    if ((schluessel != null)
                                && schluessel.equals((String)messwertBean.getProperty("stoff_schluessel"))) {
                        if (series.getDataItem(new Day(datum)) == null) {
                            series.add(new Day(datum), (Double)messwertBean.getProperty("wert"));
                        }
                    }
                }
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Icon createIcon(final String schluessel) {
        final Shape shape = getShape(schluessel);
        final Paint color = getColor(schluessel);
        final Icon icon = new ShapeIcon(shape, color, 18, 18);
        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     * @param   stoffBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndex(final String schluessel, final List<CidsBean> stoffBeans) {
        if (schluessel == null) {
            return -1;
        }
        if (stoffBeans != null) {
            for (final CidsBean stoffBean : stoffBeans) {
                if (schluessel.equals(stoffBean.getProperty("schluessel"))) {
                    return stoffBeans.indexOf(stoffBean);
                }
            }
        }
        return -1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Shape getShape(final String schluessel) {
        final int index = getIndex(schluessel, stoffBeans);
        if (index >= 0) {
            return shapes[index % shapes.length];
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Paint getColor(final String schluessel) {
        final int index = getIndex(schluessel, stoffBeans);
        if (index >= 0) {
            return colors[index % colors.length];
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JFreeChart createChartPanel() {
        final TimeSeriesCollection dataSetLeft = createDataSet(grundwassermessstelleDiagrammAxisPanel4);

        final TimeSeriesCollection dataSetRight =
            (!grundwassermessstelleDiagrammAxisPanel5.getEnabledStoffBeans().isEmpty())
            ? createDataSet(grundwassermessstelleDiagrammAxisPanel5) : null;

//        final Set<String> einheitenLeft = new HashSet<>();
//        for (final CidsBean stoffBean : grundwassermessstelleDiagrammAxisPanel2.getStoffBeans()) {
//            final String einheit = (String)stoffBean.getProperty("einheit");
//            if (einheit != null) {
//                einheitenLeft.add(einheit);
//            }
//        }

        final JFreeChart chart = ChartFactory.createScatterPlot(
                null,
                null,
                null,
                dataSetLeft,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);

        final XYPlot plot = chart.getXYPlot();
//        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setBackgroundPaint(Color.WHITE);

        final DateAxis dateAxis = new DateAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setDateFormatOverride(new SimpleDateFormat("dd.MM.yyy"));
        dateAxis.setVerticalTickLabels(true);

//        if (getMessungBeans() != null) {
//            Date maxDate = null;
//            Date minDate = null;
//            for (final CidsBean messungBean : getMessungBeans()) {
//                final Date date = (Date)messungBean.getProperty("datum");
//                if ((maxDate == null) || (maxDate.compareTo(date) < 0)) {
//                    maxDate = date;
//                }
//                if ((minDate == null) || (minDate.compareTo(date) > 0)) {
//                    minDate = date;
//                }
//            }
//            dateAxis.setAutoRange(false);
//            if (minDate != null) {
//                dateAxis.setMinimumDate(minDate);
//            }
//            if (maxDate != null) {
//                dateAxis.setMaximumDate(maxDate);
//            }
//        }

        plot.setDomainAxis(dateAxis);

//        final Set<String> einheitenRight = new HashSet<>();
//        for (final CidsBean stoffBean : grundwassermessstelleDiagrammAxisPanel3.getStoffBeans()) {
//            final String einheit = (String)stoffBean.getProperty("einheit");
//            if (einheit != null) {
//                einheitenRight.add(einheit);
//            }
//        }

        plot.setDataset(0, dataSetLeft);
        plot.setRenderer(0, new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES));

        plot.setRangeAxis(0, plot.getRangeAxis());
        plot.mapDatasetToRangeAxis(0, 0);
        final XYItemRenderer leftRenderer = plot.getRenderer();

        for (int index = 0; index < dataSetLeft.getSeriesCount(); index++) {
            final String schluessel = (String)dataSetLeft.getSeries(index).getKey();
            leftRenderer.setSeriesShape(index, getShape(schluessel));
            leftRenderer.setSeriesPaint(index, getColor(schluessel));
        }

//        final LegendTitle legendLeft = new LegendTitle(rendererLeft);
//        legendLeft.setPosition(RectangleEdge.LEFT);
//        chart.addLegend(legendLeft);

        if (dataSetRight != null) {
            plot.setDataset(1, dataSetRight);

            final XYItemRenderer rightRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
            plot.setRenderer(1, rightRenderer);

            final NumberAxis numberAxis = new NumberAxis(); // String.join(", ", einheitenRight));
            plot.setRangeAxis(1, numberAxis);
            plot.mapDatasetToRangeAxis(1, 1);
            for (int index = 0; index < dataSetRight.getSeriesCount(); index++) {
                final String schluessel = (String)dataSetRight.getSeries(index).getKey();
                rightRenderer.setSeriesShape(index, getShape(schluessel));
                rightRenderer.setSeriesPaint(index, getColor(schluessel));
            }
        }

        return chart;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class ShapeIcon implements Icon {

        //~ Instance fields ----------------------------------------------------

        private final Shape shape;
        private final Paint paint;
        private final int width;
        private final int height;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ShapeIcon object.
         *
         * @param  shape   DOCUMENT ME!
         * @param  paint   DOCUMENT ME!
         * @param  width   DOCUMENT ME!
         * @param  height  DOCUMENT ME!
         */
        public ShapeIcon(final Shape shape, final Paint paint, final int width, final int height) {
            this.shape = shape;
            this.width = width;
            this.height = height;
            this.paint = paint;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        @Override
        public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
            final Graphics2D g2 = (Graphics2D)g;
            if (paint != null) {
                g2.setPaint(paint);
            }
            if (shape != null) {
                g2.translate(width / 2, height / 2);
                g2.draw(shape);
                g2.fill(shape);
                g2.translate(-width / 2, -height / 2);
            }
        }
    }
}