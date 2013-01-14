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
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.types.treenode.DefaultMetaTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

import de.cismet.cismap.commons.features.DefaultFeatureCollection;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.SearchFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AbstractCreateSearchGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateGeometryListenerInterface;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateSearchGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.MetaSearchCreateSearchGeometryListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.cismap.tools.gui.CidsBeanDropJPopupMenuButton;

import de.cismet.tools.gui.HighlightingRadioButtonMenuItem;
import de.cismet.tools.gui.JPopupMenuButton;
import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class GeoSearchButton extends CidsBeanDropJPopupMenuButton {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(GeoSearchButton.class);

    //~ Instance fields --------------------------------------------------------

    private MappingComponent mappingComponent;
    private ImageIcon icoPluginRectangle;
    private ImageIcon icoPluginPolygon;
    private ImageIcon icoPluginEllipse;
    private ImageIcon icoPluginPolyline;
    private final String interActionMode;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgrSearch;
    private javax.swing.JMenuItem mniSearchBuffer;
    private javax.swing.JRadioButtonMenuItem mniSearchCidsFeature;
    private javax.swing.JRadioButtonMenuItem mniSearchEllipse;
    private javax.swing.JRadioButtonMenuItem mniSearchPolygon;
    private javax.swing.JRadioButtonMenuItem mniSearchPolyline;
    private javax.swing.JRadioButtonMenuItem mniSearchRectangle;
    private javax.swing.JMenuItem mniSearchRedo;
    private javax.swing.JMenuItem mniSearchShowLastFeature;
    private javax.swing.JPopupMenu popMenSearch;
    private javax.swing.JSeparator sepSearchGeometries;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoSearchButton object.
     *
     * @param  interActionMode   DOCUMENT ME!
     * @param  interactionMode   DOCUMENT ME!
     * @param  mappingComponent  DOCUMENT ME!
     * @param  searchName        DOCUMENT ME!
     */
    public GeoSearchButton(final String interActionMode,
            final String interactionMode,
            final MappingComponent mappingComponent,
            final String searchName) {
        this(interActionMode, mappingComponent, searchName, "");
    }

    /**
     * Creates new form GeoSearchButton.
     *
     * @param  interActionMode  DOCUMENT ME!
     * @param  mc               DOCUMENT ME!
     * @param  searchName       DOCUMENT ME!
     * @param  toolTipText      DOCUMENT ME!
     */
    public GeoSearchButton(final String interActionMode,
            final MappingComponent mc,
            final String searchName,
            final String toolTipText) {
        super(interActionMode, mc, searchName);
        mappingComponent = mc;
        this.interActionMode = interActionMode;
        icoPluginRectangle = new ImageIcon(getClass().getResource("/images/pluginSearchRectangle.png"));
        icoPluginPolygon = new ImageIcon(getClass().getResource("/images/pluginSearchPolygon.png"));
        icoPluginEllipse = new ImageIcon(getClass().getResource("/images/pluginSearchEllipse.png"));
        icoPluginPolyline = new ImageIcon(getClass().getResource("/images/pluginSearchPolyline.png"));

        initComponents();
        setToolTipText(toolTipText);
        ((JPopupMenuButton)this).setPopupMenu(popMenSearch);
        this.setFocusPainted(false);
        new CidsBeanDropTarget(this);

        ((CidsBeanDropJPopupMenuButton)this).setTargetIcon(new javax.swing.ImageIcon(
                getClass().getResource("/images/pluginSearchTarget.png")));
        visualizeSearchMode((MetaSearchCreateSearchGeometryListener)mappingComponent.getInputListener(
                MappingComponent.CREATE_SEARCH_POLYGON));
        this.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    mappingComponent.setInteractionMode(
                        interActionMode);
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        popMenSearch = new javax.swing.JPopupMenu();
        mniSearchRectangle = new HighlightingRadioButtonMenuItem(javax.swing.UIManager.getDefaults().getColor(
                    "ProgressBar.foreground"),
                Color.WHITE);
        mniSearchPolygon = new HighlightingRadioButtonMenuItem(javax.swing.UIManager.getDefaults().getColor(
                    "ProgressBar.foreground"),
                Color.WHITE);
        mniSearchEllipse = new HighlightingRadioButtonMenuItem(javax.swing.UIManager.getDefaults().getColor(
                    "ProgressBar.foreground"),
                Color.WHITE);
        mniSearchPolyline = new HighlightingRadioButtonMenuItem(javax.swing.UIManager.getDefaults().getColor(
                    "ProgressBar.foreground"),
                Color.WHITE);
        sepSearchGeometries = new javax.swing.JSeparator();
        mniSearchCidsFeature = new javax.swing.JRadioButtonMenuItem();
        mniSearchShowLastFeature = new javax.swing.JMenuItem();
        mniSearchRedo = new javax.swing.JMenuItem();
        mniSearchBuffer = new javax.swing.JMenuItem();
        bgrSearch = new javax.swing.ButtonGroup();

        bgrSearch.add(mniSearchRectangle);
        mniSearchRectangle.setSelected(true);
        mniSearchRectangle.setText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchRectangle.text"));                                                    // NOI18N
        mniSearchRectangle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/rectangle.png"))); // NOI18N
        mniSearchRectangle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSearchRectangleActionPerformed(evt);
                }
            });
        popMenSearch.add(mniSearchRectangle);

        bgrSearch.add(mniSearchPolygon);
        mniSearchPolygon.setText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchPolygon.text"));                                                  // NOI18N
        mniSearchPolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/polygon.png"))); // NOI18N
        mniSearchPolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSearchPolygonActionPerformed(evt);
                }
            });
        popMenSearch.add(mniSearchPolygon);

        bgrSearch.add(mniSearchEllipse);
        mniSearchEllipse.setText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchEllipse.text"));                                                  // NOI18N
        mniSearchEllipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ellipse.png"))); // NOI18N
        mniSearchEllipse.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSearchEllipseActionPerformed(evt);
                }
            });
        popMenSearch.add(mniSearchEllipse);

        bgrSearch.add(mniSearchPolyline);
        mniSearchPolyline.setText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchPolyline.text"));                                                   // NOI18N
        mniSearchPolyline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/polyline.png"))); // NOI18N
        mniSearchPolyline.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSearchPolylineActionPerformed(evt);
                }
            });
        popMenSearch.add(mniSearchPolyline);
        popMenSearch.add(sepSearchGeometries);

        bgrSearch.add(mniSearchCidsFeature);
        mniSearchCidsFeature.setText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchCidsFeature.text"));                                                  // NOI18N
        mniSearchCidsFeature.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/polygon.png"))); // NOI18N
        mniSearchCidsFeature.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSearchCidsFeatureActionPerformed(evt);
                }
            });
        popMenSearch.add(mniSearchCidsFeature);

        mniSearchShowLastFeature.setText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchShowLastFeature.text"));        // NOI18N
        mniSearchShowLastFeature.setToolTipText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchShowLastFeature.toolTipText")); // NOI18N
        mniSearchShowLastFeature.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSearchShowLastFeatureActionPerformed(evt);
                }
            });
        popMenSearch.add(mniSearchShowLastFeature);

        mniSearchRedo.setText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchRedo.text"));        // NOI18N
        mniSearchRedo.setToolTipText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchRedo.toolTipText")); // NOI18N
        mniSearchRedo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSearchRedoActionPerformed(evt);
                }
            });
        popMenSearch.add(mniSearchRedo);

        mniSearchBuffer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/buffer.png"))); // NOI18N
        mniSearchBuffer.setText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchBuffer.text"));                                                 // NOI18N
        mniSearchBuffer.setToolTipText(org.openide.util.NbBundle.getMessage(
                GeoSearchButton.class,
                "GeoSearchButton.mniSearchBuffer.toolTipText"));                                          // NOI18N
        mniSearchBuffer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSearchBufferActionPerformed(evt);
                }
            });
        popMenSearch.add(mniSearchBuffer);

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void setToolTipText(final String string) {
        super.setToolTipText(string);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  searchListener  DOCUMENT ME!
     */
    public void visualizeSearchMode(final CreateSearchGeometryListener searchListener) {
        final String searchMode = searchListener.getMode();
        final PureNewFeature lastGeometry = searchListener.getLastSearchFeature();

        if (CreateGeometryListenerInterface.RECTANGLE.equals(searchMode)) {
            this.setIcon(icoPluginRectangle);
            this.setSelectedIcon(icoPluginRectangle);
        } else if (CreateGeometryListenerInterface.POLYGON.equals(searchMode)) {
            this.setIcon(icoPluginPolygon);
            this.setSelectedIcon(icoPluginPolygon);
        } else if (CreateGeometryListenerInterface.ELLIPSE.equals(searchMode)) {
            this.setIcon(icoPluginEllipse);
            this.setSelectedIcon(icoPluginEllipse);
        } else if (CreateGeometryListenerInterface.LINESTRING.equals(searchMode)) {
            this.setIcon(icoPluginPolyline);
            this.setSelectedIcon(icoPluginPolyline);
        }

        mniSearchRectangle.setSelected(CreateGeometryListenerInterface.RECTANGLE.equals(searchMode));
        mniSearchPolygon.setSelected(CreateGeometryListenerInterface.POLYGON.equals(searchMode));
        mniSearchEllipse.setSelected(CreateGeometryListenerInterface.ELLIPSE.equals(searchMode));
        mniSearchPolyline.setSelected(CreateGeometryListenerInterface.LINESTRING.equals(searchMode));

        if (lastGeometry == null) {
            mniSearchShowLastFeature.setIcon(null);
            mniSearchShowLastFeature.setEnabled(false);
            mniSearchRedo.setIcon(null);
            mniSearchRedo.setEnabled(false);
            mniSearchBuffer.setEnabled(false);
        } else {
            switch (lastGeometry.getGeometryType()) {
                case ELLIPSE: {
                    mniSearchRedo.setIcon(mniSearchEllipse.getIcon());
                    break;
                }

                case LINESTRING: {
                    mniSearchRedo.setIcon(mniSearchPolyline.getIcon());
                    break;
                }

                case POLYGON: {
                    mniSearchRedo.setIcon(mniSearchPolygon.getIcon());
                    break;
                }

                case RECTANGLE: {
                    mniSearchRedo.setIcon(mniSearchRectangle.getIcon());
                    break;
                }
            }

            mniSearchRedo.setEnabled(true);
            mniSearchShowLastFeature.setIcon(mniSearchRedo.getIcon());
            mniSearchShowLastFeature.setEnabled(true);
            mniSearchBuffer.setEnabled(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSearchRectangleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSearchRectangleActionPerformed
        this.setIcon(icoPluginRectangle);
        this.setSelectedIcon(icoPluginRectangle);

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ((AbstractCreateSearchGeometryListener)mappingComponent.getInputListener(interActionMode)).setMode(
                        CreateGeometryListenerInterface.RECTANGLE);
                    mappingComponent.setInteractionMode(
                        interActionMode);
                }
            });
    } //GEN-LAST:event_mniSearchRectangleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSearchPolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSearchPolygonActionPerformed
        this.setIcon(icoPluginPolygon);
        this.setSelectedIcon(icoPluginPolygon);

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ((AbstractCreateSearchGeometryListener)mappingComponent.getInputListener(
                            interActionMode)).setMode(
                        CreateGeometryListenerInterface.POLYGON);
                    mappingComponent.setInteractionMode(
                        interActionMode);
                }
            });
    } //GEN-LAST:event_mniSearchPolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSearchEllipseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSearchEllipseActionPerformed
        this.setIcon(icoPluginEllipse);
        this.setSelectedIcon(icoPluginEllipse);

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ((AbstractCreateSearchGeometryListener)mappingComponent.getInputListener(
                            interActionMode)).setMode(
                        CreateGeometryListenerInterface.ELLIPSE);
                    mappingComponent.setInteractionMode(
                        interActionMode);
                }
            });
    } //GEN-LAST:event_mniSearchEllipseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSearchPolylineActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSearchPolylineActionPerformed
        this.setIcon(icoPluginPolyline);
        this.setSelectedIcon(icoPluginPolyline);

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ((AbstractCreateSearchGeometryListener)mappingComponent.getInputListener(
                            interActionMode)).setMode(
                        CreateGeometryListenerInterface.LINESTRING);
                    mappingComponent.setInteractionMode(
                        interActionMode);
                }
            });
    } //GEN-LAST:event_mniSearchPolylineActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSearchCidsFeatureActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSearchCidsFeatureActionPerformed
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    mappingComponent.setInteractionMode(
                        interActionMode);
                    final AbstractCreateSearchGeometryListener searchListener = ((AbstractCreateSearchGeometryListener)
                            mappingComponent.getInputListener(
                                interActionMode));

                    de.cismet.tools.CismetThreadPool.execute(
                        new javax.swing.SwingWorker<SearchFeature, Void>() {

                            @Override
                            protected SearchFeature doInBackground() throws Exception {
                                final DefaultMetaTreeNode[] nodes = ComponentRegistry.getRegistry()
                                                .getActiveCatalogue()
                                                .getSelectedNodesArray();
                                final Collection<Geometry> searchGeoms = new ArrayList<Geometry>();

                                for (final DefaultMetaTreeNode dmtn : nodes) {
                                    if (dmtn instanceof ObjectTreeNode) {
                                        final MetaObject mo = ((ObjectTreeNode)dmtn).getMetaObject();
                                        final CidsFeature cf = new CidsFeature(mo);
                                        searchGeoms.add(cf.getGeometry());
                                    }
                                }

                                final Geometry[] searchGeomsArr = searchGeoms.toArray(new Geometry[0]);
                                final GeometryCollection coll =
                                    new GeometryFactory().createGeometryCollection(searchGeomsArr);

                                final Geometry newG = coll.buffer(0.1d);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("SearchGeom " + newG.toText());
                                }

                                final SearchFeature sf = new SearchFeature(newG);
                                sf.setGeometryType(PureNewFeature.geomTypes.MULTIPOLYGON);
                                return sf;
                            }

                            @Override
                            protected void done() {
                                try {
                                    final SearchFeature search = get();
                                    if (search != null) {
                                        searchListener.search(search);
                                    }
                                } catch (Exception e) {
                                    LOG.error("Exception in Background Thread", e);
                                }
                            }
                        });
                }
            });
    } //GEN-LAST:event_mniSearchCidsFeatureActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSearchShowLastFeatureActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSearchShowLastFeatureActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final AbstractCreateSearchGeometryListener searchListener = (AbstractCreateSearchGeometryListener)
                        mappingComponent.getInputListener(
                            interActionMode);
                    searchListener.showLastFeature();
                    mappingComponent.setInteractionMode(
                        interActionMode);
                }
            });
    } //GEN-LAST:event_mniSearchShowLastFeatureActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSearchRedoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSearchRedoActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final AbstractCreateSearchGeometryListener searchListener = (AbstractCreateSearchGeometryListener)
                        mappingComponent.getInputListener(
                            interActionMode);
                    searchListener.redoLastSearch();
                    mappingComponent.setInteractionMode(
                        interActionMode);
                }
            });
    } //GEN-LAST:event_mniSearchRedoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSearchBufferActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSearchBufferActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final String s = (String)JOptionPane.showInputDialog(
                            StaticSwingTools.getParentFrame(GeoSearchButton.this),
                            "Geben Sie den Abstand des zu erzeugenden\n"       // NOI18N
                                    + "Puffers der letzten Suchgeometrie an.", // NOI18N
                            "Puffer",                                          // NOI18N
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "");                                               // NOI18N
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(s);
                    }

                    // , statt . ebenfalls erlauben
                    if (s.matches("\\d*,\\d*")) { // NOI18N
                        s.replace(",", ".");      // NOI18N
                    }

                    try {
                        final float buffer = Float.valueOf(s);

                        final AbstractCreateSearchGeometryListener searchListener =
                            (AbstractCreateSearchGeometryListener)mappingComponent.getInputListener(
                                interActionMode);
                        final PureNewFeature lastFeature = searchListener.getLastSearchFeature();

                        if (lastFeature != null) {
                            // Geometrie-Daten holen
                            final Geometry geom = lastFeature.getGeometry();

                            // Puffer-Geometrie holen
                            final Geometry bufferGeom = geom.buffer(buffer);

                            // und setzen
                            lastFeature.setGeometry(bufferGeom);

                            // Geometrie ist jetzt eine Polygon (keine Linie, Ellipse, oder
                            // �hnliches mehr)
                            lastFeature.setGeometryType(PureNewFeature.geomTypes.POLYGON);

                            for (final Object feature : mappingComponent.getFeatureCollection().getAllFeatures()) {
                                final PFeature sel = (PFeature)mappingComponent.getPFeatureHM().get(feature);

                                if (sel.getFeature().equals(lastFeature)) {
                                    // Koordinaten der Puffer-Geometrie als Feature-Koordinaten
                                    // setzen
                                    sel.setCoordArr(bufferGeom.getCoordinates());

                                    // refresh
                                    sel.syncGeometry();

                                    final Vector v = new Vector();
                                    v.add(sel.getFeature());
                                    ((DefaultFeatureCollection)mappingComponent.getFeatureCollection())
                                            .fireFeaturesChanged(v);
                                }
                            }

                            searchListener.search(lastFeature);
                            mappingComponent.setInteractionMode(
                                interActionMode);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(
                            StaticSwingTools.getParentFrame(GeoSearchButton.this),
                            "The given value was not a floating point value.!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE); // NOI18N
                    } catch (Exception ex) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("", ex);          // NOI18N
                        }
                    }
                }
            });
    }                                                   //GEN-LAST:event_mniSearchBufferActionPerformed
}
