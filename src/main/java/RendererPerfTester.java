/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/

import Sirius.server.middleware.types.MetaObject;

import java.awt.Dimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsObjectRendererFactory;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class RendererPerfTester extends javax.swing.JFrame {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            RendererPerfTester.class);
    private static final String SRS = "EPSG:25832";

    //~ Instance fields --------------------------------------------------------

    private final Map<String, Collection> objectsMap = new HashMap<>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form NewJFrame.
     */
    public RendererPerfTester() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected static void initMap() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initMap");
        }
        final MappingComponent mappingComponent = new MappingComponent();
        final Dimension d = new Dimension(300, 300);
        mappingComponent.setPreferredSize(d);
        mappingComponent.setSize(d);

        final ActiveLayerModel mappingModel = new ActiveLayerModel();
        mappingModel.addHome(new XBoundingBox(
                2583621.251964098d,
                5682507.032498134d,
                2584022.9413952776d,
                5682742.852810634d,
                SRS,
                false));
        mappingModel.setSrs(SRS);

        mappingComponent.setInteractionMode(MappingComponent.SELECT);
        mappingComponent.setMappingModel(mappingModel);
        mappingComponent.gotoInitialBoundingBox();
        mappingComponent.unlock();

        CismapBroker.getInstance().setMappingComponent(mappingComponent);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        UIManager.installLookAndFeel("Plastic 3D", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel"); // NOI18N
        final String heavyComps = System.getProperty("contains.heavyweight.comps");
        if ((heavyComps != null) && heavyComps.equals("true")) {
            com.jgoodies.looks.Options.setPopupDropShadowEnabled(false);
        }
        UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");

        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        initMap();

        final int w = 800;
        final int h = 600;
        final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        for (int i = 0; i < 5; i++) {
            final RendererPerfTester tester = new RendererPerfTester();
            tester.setSize(w, h);
            tester.setVisible(true);
            tester.setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);

            tester.run();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void run() {
//        final int number = 20;
//        final String[] classNames = new String[] {
//                "alkis_landparcel",
//                "billing_kunde",
////            "mauer",
////            "treppe",
////            "fs_bestellung",
//                "alb_baulast"
//            };
//
//        objectsMap.clear();
//
//        for (final String className : classNames) {
//            new SwingWorker<Collection<MetaObject>, Void>() {
//
//                    @Override
//                    protected Collection<MetaObject> doInBackground() throws Exception {
//                        final CidsBean[] cidsBeans = DevelopmentTools.createCidsBeansFromRestfulConnection(
//                                "http://s10221:9990/callserver/binary",
//                                "WUNDA_BLAU",
//                                null,
//                                "admin",
//                                "9eh1nich10hne",
//                                true,
//                                className,
//                                null,
//                                number);
//
//                        final ArrayList<MetaObject> mos = new ArrayList<>(cidsBeans.length);
//                        for (final CidsBean b : cidsBeans) {
//                            mos.add(b.getMetaObject());
//                        }
//                        return mos;
//                    }
//
//                    @Override
//                    protected void done() {
//                        Collection<MetaObject> mos = null;
//                        try {
//                            mos = get();
//                        } catch (final Exception ex) {
//                            LOG.error(ex, ex);
//                        } finally {
//                            objectsMap.put(className, mos);
//                            if (objectsMap.size() == classNames.length) {
//                                starttabbing();
//                            }
//                        }
//                    }
//                }.execute();
//        }
    }

    /**
     * DOCUMENT ME!
     */
    private void starttabbing() {
        for (final String key : objectsMap.keySet()) {
            final Collection<MetaObject> mos = objectsMap.get(key);
            new SwingWorker<JComponent, Void>() {

                    @Override
                    protected JComponent doInBackground() throws Exception {
                        try {
                            return CidsObjectRendererFactory.getInstance().getAggregationRenderer(mos, key);
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            return null;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            final JComponent c = get();
                            if (c != null) {
                                jTabbedPane1.add(key, c);
                                repaint();
                            }
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                }.execute();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jToggleButton1 = new javax.swing.JToggleButton();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel1.add(jTabbedPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jToggleButton1,
            org.openide.util.NbBundle.getMessage(RendererPerfTester.class, "RendererPerfTester.jToggleButton1.text")); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        jPanel1.add(jToggleButton1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jProgressBar1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton1ActionPerformed
        System.exit(0);
    }                                                                                  //GEN-LAST:event_jToggleButton1ActionPerformed
}
