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
package de.cismet.cids.featurerenderer;

import edu.umd.cs.piccolo.PNode;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Paint;
import java.awt.image.BufferedImage;

import java.net.URL;

import java.util.Properties;
import java.util.Random;

import javax.swing.ImageIcon;

import de.cismet.cids.annotations.CidsAttribute;

/**
 * de.cismet.cids.featurerenderer.UmweltstationFeatureRenderer.
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class UmweltstationFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Instance fields --------------------------------------------------------

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel lblChart;
    private javax.swing.JLabel lblImagePreview;

    @CidsAttribute("Typ")
    public String typ;
    Random random = new Random();
    @CidsAttribute("noise")
    Float noise = 0.5f;
    @CidsAttribute("ozon")
    Float oz = 0.65f;
    @CidsAttribute("sox")
    Float sox = 0.1f;
    @CidsAttribute("pm10")
    Float pm = 0.5f;
    @CidsAttribute("nox")
    Float nox = 0.3f;
    @CidsAttribute("noiseVar")
    Float noiseVar = 0.5f;
    @CidsAttribute("ozonVar")
    Float ozVar = 0.65f;
    @CidsAttribute("soxVar")
    Float soxVar = 0.1f;
    @CidsAttribute("pm10Var")
    Float pmVar = 0.5f;
    @CidsAttribute("noxVar")
    Float noxVar = 0.3f;
    ImageIcon errorimage = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/cids/tools/metaobjectrenderer/examples/error.png"));
    /** Creates new form LSAFeatureRenderer. */
    Properties properties = new Properties();

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    /**
     * Creates a new UmweltstationFeatureRenderer object.
     */
    public UmweltstationFeatureRenderer() {
        initComponents();
        setOpaque(false);
        setPreferredSize(new Dimension(350, 150));
        final Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (true) { // && refreshable != null && refreshable instanceof PNode && ((PNode)
                                       // refreshable).getVisible()) {
                            // if (isVisible()){
                            refreshDiagram();
                            if (refreshable instanceof PNode) {
                                ((PNode)refreshable).repaint();
                            }
                            // }
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                                log.error("Fehler beim Darstellen des Diagrams", ex);
                            }
                        }
                    }
                });

        t.start();
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshDiagram() {
        final JFreeChart chart = createChart(createDataset(noiseVar, soxVar, ozVar, pmVar, noxVar));
        chart.setBackgroundPaint(new Color(210, 210, 210));

        final BufferedImage image = chart.createBufferedImage(250, 150);
        lblChart.setIcon(new ImageIcon(image));
    }

    @Override
    public void assign() {
        final Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final ImageIcon icon = new ImageIcon(
                                    new URL("http://s10221/cismet/res/luftmessung/" + typ + ".jpg"));
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        lblImagePreview.setIcon(icon);
                                    }
                                });
                        } catch (Throwable ex) {
                            log.warn("Fehler beim suchen des Bildes auf s10221. Versuche es auf kif.", ex);
                            try {
                                final ImageIcon icon = new ImageIcon(
                                        new URL("http://kif/web/luftmessung/" + typ + ".jpg"));
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            lblImagePreview.setIcon(icon);
                                        }
                                    });
                            } catch (Throwable ex2) {
                                log.error("Auch auf kif nix gefunden", ex);
                            }
                        }
                    }
                });

        t.start();
    }

    @Override
    public float getTransparency() {
        return 0.9f;
    }

    @Override
    public Paint getFillingStyle() {
        return new Color(100, 100, 100, 50);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        lblImagePreview = new javax.swing.JLabel();
        lblChart = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setLayout(new java.awt.GridBagLayout());

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setPreferredSize(new java.awt.Dimension(100, 100));
        lblImagePreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagePreview.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/tools/metaobjectrenderer/examples/load.png")));
        lblImagePreview.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewMouseClicked(evt);
                }
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewMouseEntered(evt);
                }
                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewMouseExited(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 11, 3, 3);
        add(lblImagePreview, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(lblChart, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }                                                                              //GEN-LAST:event_lblImagePreviewMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }                                                                               //GEN-LAST:event_lblImagePreviewMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewMouseClicked
//        try {
//            String url=properties.getProperty("luftbildschraegaufnahmenservicefull");
//            String newUrl=null;
//            if (url==null) {
//                newUrl="http://s10220:8098/luft/tiffer?bnr="+nummer+"&scale=1&format=JPG";
//            } else{
//                newUrl=url.replaceAll("<cismet::nummer>",nummer);
//
//            }
//            BrowserLauncher.openURL(newUrl);
//        } catch (Exception e) {
//
//        }
    } //GEN-LAST:event_lblImagePreviewMouseClicked
    /**
     * End of variables declaration//GEN-END:variables.
     *
     * @param   noiseVar  DOCUMENT ME!
     * @param   soxVar    DOCUMENT ME!
     * @param   ozVar     DOCUMENT ME!
     * @param   pmVar     DOCUMENT ME!
     * @param   noxVar    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    CategoryDataset createDataset(final float noiseVar,
            final float soxVar,
            final float ozVar,
            final float pmVar,
            final float noxVar) {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        oz = oz + (ozVar * (random.nextFloat() - 0.5f));
        noise = noise + (noiseVar * (random.nextFloat() - 0.5f));
        sox = sox + (soxVar * (random.nextFloat() - 0.5f));
        pm = pm + (pmVar * (random.nextFloat() - 0.5f));
        nox = nox + (noxVar * (random.nextFloat() - 0.5f));
        if (oz < 0) {
            oz = 0f;
        }
        if (noise < 0) {
            noise = 0f;
        }
        if (sox < 0) {
            sox = 0f;
        }
        if (pm < 0) {
            pm = 0f;
        }
        if (nox < 0) {
            nox = 0f;
        }
        if (noise > 1.2) {
            noise = 1.2f;
        }
        if (oz > 1.2) {
            oz = 1.2f;
        }
        if (sox > 1.2) {
            sox = 1.2f;
        }
        if (pm > 1.2) {
            pm = 1.2f;
        }
        if (nox > 1.2) {
            nox = 1.2f;
        }

        dataset.addValue(noise, "Series 1", "L\u00E4rm");
        dataset.addValue(sox, "Series 1", "SOx");
        dataset.addValue(oz, "Series 1", "Ozon");
        dataset.addValue(pm, "Series 1", "PM10");
        dataset.addValue(nox, "Series 1", "NOx");
//        dataset.addValue(1, "Series 1", "Calib");
        return dataset;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dataset  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    static JFreeChart createChart(final CategoryDataset dataset) {
        final JFreeChart chart = ChartFactory.createBarChart3D(
                "",                       // chart title
                "",                       // domain axis label
                "",                       // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                false,                    // include legend
                true,                     // tooltips
                false                     // urls
                );

        final CategoryPlot plot = (CategoryPlot)chart.getPlot();
        final CustomBarRenderer3D renderer = new CustomBarRenderer3D();
        plot.setRenderer(renderer);
        final ValueMarker marker = new ValueMarker(
                0.70,
                new Color(255, 0, 0, 100),
                new BasicStroke(1.0f),
                new Color(200, 200, 255),
                new BasicStroke(1.0f),
                1.0f);
        final ValueMarker marker2 = new ValueMarker(
                0.30,
                new Color(255, 255, 0, 100),
                new BasicStroke(1.0f),
                new Color(200, 200, 255),
                new BasicStroke(1.0f),
                1.0f);
//        marker.setLabel("Minimum grade to pass");
//        marker.setLabelPaint(Color.red);
//        marker.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
//        marker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
        plot.addRangeMarker(marker, Layer.BACKGROUND);
        plot.addRangeMarker(marker2, Layer.BACKGROUND);
        renderer.setItemLabelsVisible(true);
        renderer.setMaximumBarWidth(0.05);

        // couldn't get the label above to appear in front, so using an
        // annotation instead...
// CategoryTextAnnotation a = new CategoryTextAnnotation("", "SOx", 0.70);
// a.setCategoryAnchor(CategoryAnchor.START);
// a.setFont(new Font("SansSerif", Font.PLAIN, 12));
// a.setTextAnchor(TextAnchor.BOTTOM_LEFT);
// plot.addAnnotation(a);
//
// CategoryTextAnnotation b = new CategoryTextAnnotation("2", "SOx", 0.40);
// b.setCategoryAnchor(CategoryAnchor.START);
// b.setFont(new Font("SansSerif", Font.PLAIN, 12));
// b.setTextAnchor(TextAnchor.BOTTOM_LEFT);
// plot.addAnnotation(b);

        final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setVisible(false);
        // rangeAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
        rangeAxis.setAutoRange(false);
        rangeAxis.setRange(0, 1.2);
        // rangeAxis.setAutoRangeMinimumSize(1);
        return chart;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class CustomBarRenderer3D extends BarRenderer3D {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new renderer.
         */
        public CustomBarRenderer3D() {
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
            final CategoryDataset dataset = getPlot().getDataset();
            final double value = dataset.getValue(row, column).doubleValue();
            if (value <= 0.30) {
                return Color.green;
            } else if (value <= 0.70) {
                return Color.yellow;
            } else {
                return Color.red;
            }
        }
    }
}
