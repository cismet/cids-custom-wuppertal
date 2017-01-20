/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * LSAFeatureRenderer.java
 *
 * Created on 1. Juni 2007, 10:15
 */
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import java.sql.Timestamp;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.featurerenderer.*;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.CustomFixedWidthStroke;

/**
 * de.cismet.cids.featurerenderer.ZaehlungsstandortFeatureRenderer.
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class ZaehlungsstandortFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final int MARK1 = 400;
    private static final int MARK2 = 700;
    private static final int MARK3 = 1000;
    private static final int MARK4 = 2000;
    private static final int MARK5 = 3000;
    private static final int MARK6 = 5000;
    private static final int MARK7 = 8000;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ZaehlungsstandortFeatureRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final ImageIcon errorimage = new ImageIcon(getClass().getResource(
                "/de/cismet/cids/tools/metaobjectrenderer/examples/error.png"));
    private DefaultCategoryDataset dataset;
    private final TreeMap<String, int[]> jahresAnzahl = new TreeMap();
    private long avgLast = 0L;
    private String lastYear = null;
    private Refreshable refresh;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblChart;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PassantenfrequenzFeatureRenderer.
     */
    public ZaehlungsstandortFeatureRenderer() {
        initComponents();
        setOpaque(false);
        setPreferredSize(new Dimension(350, 200));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
        cidsBean = metaObject.getBean();
        if (cidsBean != null) {
            final List<CidsBean> zaehlungen = cidsBean.getBeanCollectionProperty("zaehlungen");
            final ArrayList<Timestamp> datum = new ArrayList<Timestamp>(zaehlungen.size());
            final ArrayList<Integer> anzahl = new ArrayList<Integer>(zaehlungen.size());

            for (final CidsBean zaehlung : zaehlungen) {
                datum.add((Timestamp)zaehlung.getProperty("datum"));
                anzahl.add((Integer)zaehlung.getProperty("anzahl"));
            }

            if ((datum.size() > 0) && (anzahl.size() > 0)) {
                final Thread t = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                dataset = new DefaultCategoryDataset();
                                String max = "1000";
                                // Daten in HashMaps eintragen
                                for (int i = 0; i < datum.size(); i++) {
                                    try {
                                        String jahr = DateFormat.getDateInstance(DateFormat.YEAR_FIELD, Locale.GERMANY)
                                                    .format(datum.get(i));
                                        jahr = jahr.substring(jahr.length() - 4);
                                        final int wert = anzahl.get(i) * 12;
                                        if (jahresAnzahl.get(jahr) != null) {
                                            final int[] tmp = jahresAnzahl.get(jahr);
                                            tmp[0] += wert;
                                            tmp[1] += 1;
                                            jahresAnzahl.put(jahr, tmp);
                                        } else {
                                            final int[] newArr = { wert, 1 };
                                            jahresAnzahl.put(jahr, newArr);
                                        }
                                        if (jahr.compareTo(max) > 0) {
                                            max = new String(jahr);
                                        }
                                    } catch (Exception ex) {
                                        LOG.error("Error beim Erstellen des FeatureRenderers", ex);
                                    }
                                }

                                // Daten aus HashMaps in DefaultCategoryDataset eintragen
                                for (final String key : jahresAnzahl.keySet()) {
                                    dataset.addValue(
                                        Math.round(jahresAnzahl.get(key)[0] / jahresAnzahl.get(key)[1]),
                                        "Daten",
                                        key);
                                }
                                avgLast = Math.round(jahresAnzahl.get(max)[0] / jahresAnzahl.get(max)[1]);
                                lastYear = max;
                                final JFreeChart chart = createChart(dataset, avgLast);
                                chart.setBackgroundPaint(new Color(210, 210, 210));
                                final BufferedImage icon = chart.createBufferedImage(340, 170);
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            // TODO Refresh des PFeatures veranlassen
                                            lblChart.setIcon(new ImageIcon(icon));
                                            refresh.refresh();
                                        }
                                    });
                            }
                        });
                t.start();
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            lblChart.setIcon(errorimage);
                        }
                    });
            }
        }
    }

    @Override
    public float getTransparency() {
        return 1.0f;
    }

    @Override
    public Paint getFillingStyle() {
        return new Color(100, 100, 100, 50);
    }

    @Override
    public Stroke getLineStyle() {
        return new CustomFixedWidthStroke(3.0f);
    }

    @Override
    public Paint getLinePaint() {
        return new Color(255, 0, 0, 100);
    }

    @Override
    public String getAlternativeName() {
        String s = null;
        final Integer standpunkt = (Integer)cidsBean.getProperty("standort");
        if (standpunkt != null) {
            s = standpunkt.toString();
            switch (s.length()) {
                case 1: {
                    s = "00" + s;
                    break;
                }
                case 2: {
                    s = "0" + s;
                    break;
                }
            }
        }

        if (lastYear != null) {
            return s + " (" + avgLast + "/h)";
        } else {
            return s + " (noch keine Zählung vorhanden)";
        }
    }

    /**
     * Erzeugt ein Diagramm f\u00FCr Passantenfrequenzen.
     *
     * @param   dataset  anzuzeigende Daten
     * @param   average  DOCUMENT ME!
     *
     * @return  JFreeChart-Objekt
     */
    static JFreeChart createChart(final CategoryDataset dataset, final double average) {
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

        // X-Achsen Label gedreht darstellen
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible(true);

        final ValueMarker marker1 = new ValueMarker(
                MARK1,
                new Color(150, 150, 150, 150),
                new BasicStroke(1.0f),
                new Color(200, 200, 255),
                new BasicStroke(1.0f),
                1.0f);
        final ValueMarker marker2 = new ValueMarker(
                MARK2,
                new Color(150, 150, 150, 150),
                new BasicStroke(1.0f),
                new Color(200, 200, 255),
                new BasicStroke(1.0f),
                1.0f);
        final ValueMarker marker3 = new ValueMarker(
                MARK3,
                new Color(150, 150, 150, 150),
                new BasicStroke(1.0f),
                new Color(200, 200, 255),
                new BasicStroke(1.0f),
                1.0f);
        final ValueMarker marker4 = new ValueMarker(
                MARK4,
                new Color(150, 150, 150, 150),
                new BasicStroke(1.0f),
                new Color(200, 200, 255),
                new BasicStroke(1.0f),
                1.0f);
        final ValueMarker marker5 = new ValueMarker(
                MARK5,
                new Color(150, 150, 150, 150),
                new BasicStroke(1.0f),
                new Color(200, 200, 255),
                new BasicStroke(1.0f),
                1.0f);
        final ValueMarker marker6 = new ValueMarker(
                MARK6,
                new Color(150, 150, 150, 150),
                new BasicStroke(1.0f),
                new Color(200, 200, 255),
                new BasicStroke(1.0f),
                1.0f);
        final ValueMarker marker7 = new ValueMarker(
                MARK7,
                new Color(150, 150, 150, 150),
                new BasicStroke(1.0f),
                new Color(200, 200, 255),
                new BasicStroke(1.0f),
                1.0f);
        final ValueMarker markerAvg = new ValueMarker(average,
                new Color(100, 100, 255),
                new BasicStroke(2.5f));

        plot.addRangeMarker(marker1, Layer.BACKGROUND);
        plot.addRangeMarker(marker2, Layer.BACKGROUND);
        plot.addRangeMarker(marker3, Layer.BACKGROUND);
        plot.addRangeMarker(marker4, Layer.BACKGROUND);
        plot.addRangeMarker(marker5, Layer.BACKGROUND);
        plot.addRangeMarker(marker6, Layer.BACKGROUND);
        plot.addRangeMarker(marker7, Layer.BACKGROUND);
        plot.addRangeMarker(markerAvg, Layer.BACKGROUND);
        final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setVisible(true);
        rangeAxis.setAutoRange(true);
        return chart;
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        this.refresh = refresh;
        return super.getInfoComponent(refresh);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblChart = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 5, 0));
        setPreferredSize(new java.awt.Dimension(350, 200));
        setLayout(new java.awt.BorderLayout());

        lblChart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblChart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/load.png"))); // NOI18N
        lblChart.setPreferredSize(new java.awt.Dimension(350, 200));
        add(lblChart, java.awt.BorderLayout.CENTER);
    }                                                                                         // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class CustomBarRenderer extends BarRenderer {

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
            final CategoryDataset dataset = getPlot().getDataset();
            final double value = dataset.getValue(row, column).doubleValue();
            if (value <= MARK1) {
                return new Color(255, 255, 200);
            } else if (value <= MARK2) {
                return new Color(255, 255, 155);
            } else if (value <= MARK3) {
                return Color.yellow;
            } else if (value <= MARK4) {
                return new Color(255, 155, 0);
            } else if (value <= MARK5) {
                return new Color(255, 100, 0);
            } else if (value <= MARK6) {
                return new Color(255, 0, 0);
            } else if (value <= MARK7) {
                return new Color(150, 0, 0);
            } else {
                return new Color(100, 0, 0);
            }
        }

        @Override
        public Paint getItemOutlinePaint(final int arg0, final int arg1) {
            return Color.GRAY;
        }
    }
}
