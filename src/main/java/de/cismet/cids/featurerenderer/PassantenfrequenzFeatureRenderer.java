/*
 * LSAFeatureRenderer.java
 *
 * Created on 1. Juni 2007, 10:15
 */
package de.cismet.cids.featurerenderer;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cismap.commons.Refreshable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
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

/**
 * de.cismet.cids.featurerenderer.PassantenfrequenzFeatureRenderer
 * @author  hell
 */
public class PassantenfrequenzFeatureRenderer extends CustomCidsFeatureRenderer {
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    @CidsAttribute("Standpunkt")
    public Integer standpunkt;
    @CidsAttribute("Zählungen[].Zählung.Datum")
    public Vector<Timestamp> datum = new Vector();

    @CidsAttribute("Zählungen[].Zählung.Anzahl")
    public Vector<Integer> anzahl = new Vector();

    private static final int MARK1 = 400;
    private static final int MARK2 = 700;
    private static final int MARK3 = 1000;
    private static final int MARK4 = 2000;
    private static final int MARK5 = 3000;
    private static final int MARK6 = 5000;
    private static final int MARK7 = 8000;
    private final ImageIcon errorimage = new ImageIcon(getClass().getResource("/de/cismet/cids/tools/metaobjectrenderer/examples/error.png"));
    private DefaultCategoryDataset dataset;
    private TreeMap<String, int[]> jahresAnzahl = new TreeMap();
    private long avgLast = 0L;
    private String lastYear = null;
    private Refreshable refresh;


    /**
     * Creates a new PassantenfrequenzFeatureRenderer
     */
    public PassantenfrequenzFeatureRenderer() {
        initComponents();
        setOpaque(false);
        setPreferredSize(new Dimension(250, 150));
    }

    @Override
    public void assign() {
        if (datum != null && anzahl != null && datum.size() > 0 && anzahl.size() > 0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dataset = new DefaultCategoryDataset();
                    String max = "1000";
                    // Daten in HashMaps eintragen
                    for (int i = 0; i < datum.size(); i++) {
                        try {
                            String jahr = DateFormat.getDateInstance(DateFormat.YEAR_FIELD, Locale.GERMANY).format(datum.get(i));
                            jahr = jahr.substring(jahr.length() - 4);
                            int wert = anzahl.get(i) * 12;
                            if (jahresAnzahl.get(jahr) != null) {
                                int[] tmp = jahresAnzahl.get(jahr);
                                tmp[0] += wert;
                                tmp[1] += 1;
                                jahresAnzahl.put(jahr, tmp);
                            } else {
                                int[] newArr = {wert, 1};
                                jahresAnzahl.put(jahr, newArr);
                            }
                            if (jahr.compareTo(max) > 0) {
                                max = new String(jahr);
                            }
                        } catch (Exception ex) {
                            log.error("Error beim Erstellen des FeatureRenderers", ex);
                        }
                    }

                    // Daten aus HashMaps in DefaultCategoryDataset eintragen
                    for (String key : jahresAnzahl.keySet()) {
                        dataset.addValue(Math.round(jahresAnzahl.get(key)[0] / jahresAnzahl.get(key)[1]), "Daten", key);
                    }
                    avgLast = Math.round(jahresAnzahl.get(max)[0] / jahresAnzahl.get(max)[1]);
                    JFreeChart chart = createChart(dataset, avgLast);
                    chart.setBackgroundPaint(new Color(210, 210, 210));
                    final BufferedImage icon = chart.createBufferedImage(250, 150);
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

    @Override
    public float getTransparency() {
        return 0.9f;
    }

    @Override
    public Paint getFillingStyle() {
        return new Color(100, 100, 100, 50);
    }

    @Override
    public String getAlternativeName() {
        return "" + avgLast;
    }

    /**
     * Erzeugt ein Diagramm f\u00FCr Passantenfrequenzen
     * @param dataset anzuzeigende Daten
     * @return JFreeChart-Objekt
     */
    static JFreeChart createChart(CategoryDataset dataset, double average) {
        JFreeChart chart = ChartFactory.createBarChart3D(null, null, null, dataset,
                PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        CustomBarRenderer renderer = new CustomBarRenderer();
        plot.setRenderer(renderer);

        // X-Achsen Label gedreht darstellen
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible(true);

        ValueMarker marker1 = new ValueMarker(MARK1, new Color(150, 150, 150, 150),
                new BasicStroke(1.0f), new Color(200, 200, 255),
                new BasicStroke(1.0f), 1.0f);
        ValueMarker marker2 = new ValueMarker(MARK2, new Color(150, 150, 150, 150),
                new BasicStroke(1.0f), new Color(200, 200, 255),
                new BasicStroke(1.0f), 1.0f);
        ValueMarker marker3 = new ValueMarker(MARK3, new Color(150, 150, 150, 150),
                new BasicStroke(1.0f), new Color(200, 200, 255),
                new BasicStroke(1.0f), 1.0f);
        ValueMarker marker4 = new ValueMarker(MARK4, new Color(150, 150, 150, 150),
                new BasicStroke(1.0f), new Color(200, 200, 255),
                new BasicStroke(1.0f), 1.0f);
        ValueMarker marker5 = new ValueMarker(MARK5, new Color(150, 150, 150, 150),
                new BasicStroke(1.0f), new Color(200, 200, 255),
                new BasicStroke(1.0f), 1.0f);
        ValueMarker marker6 = new ValueMarker(MARK6, new Color(150, 150, 150, 150),
                new BasicStroke(1.0f), new Color(200, 200, 255),
                new BasicStroke(1.0f), 1.0f);
        ValueMarker marker7 = new ValueMarker(MARK7, new Color(150, 150, 150, 150),
                new BasicStroke(1.0f), new Color(200, 200, 255),
                new BasicStroke(1.0f), 1.0f);
        ValueMarker markerAvg = new ValueMarker(average,
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
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setVisible(true);
        rangeAxis.setAutoRange(true);
        return chart;
    }

    @Override
    public JComponent getInfoComponent(Refreshable refresh) {
        this.refresh = refresh;
        return super.getInfoComponent(refresh);

    }

    /**
     * 
     */
    static class CustomBarRenderer extends BarRenderer {
        /**
         * Returns the paint for an item.  Overrides the default behaviour
         * inherited from AbstractSeriesRenderer.
         * @param row  the series.
         * @param column  the category.
         * @return The item color.
         */
        @Override
        public Paint getItemPaint(int row, int column) {
            CategoryDataset dataset = getPlot().getDataset();
            double value = dataset.getValue(row, column).doubleValue();
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
        public Paint getItemOutlinePaint(int arg0, int arg1) {
            return Color.GRAY;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblChart = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 5, 0));
        setLayout(new java.awt.BorderLayout());

        lblChart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblChart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/load.png"))); // NOI18N
        lblChart.setPreferredSize(new java.awt.Dimension(250, 150));
        add(lblChart, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblChart;
    // End of variables declaration//GEN-END:variables
}
