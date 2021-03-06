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

import org.jdesktop.beansbinding.BindingGroup;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.wunda_blau.search.server.AlboFlaecheSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CsSearchconfEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    ConnectionContextStore,
    EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(CsSearchconfEditor.class);

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.custom.wunda_blau.AlboFlaecheSearchPanel alboFlaecheSearchPanel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboWirtschaftszweigEditor object.
     */
    public CsSearchconfEditor() {
        this(true);
    }

    /**
     * Creates a new AlboWirtschaftszweigEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public CsSearchconfEditor(final boolean editable) {
        this.editable = editable;
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

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        alboFlaecheSearchPanel1 = new de.cismet.cids.custom.wunda_blau.AlboFlaecheSearchPanel(editable);

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Name:");   // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jLabel3, gridBagConstraints);

        jTextField1.setName("jTextField1"); // NOI18N

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                jTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jTextField1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);

        alboFlaecheSearchPanel1.setName("alboFlaecheSearchPanel1"); // NOI18N
        alboFlaecheSearchPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(alboFlaecheSearchPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            cidsBean.setArtificialChangeFlag(true);
        }
        if (!editable) {
            RendererTools.makeReadOnly(bindingGroup, "cidsBean");
        }
        bindingGroup.bind();
        if (alboFlaecheSearchPanel1 != null) {
            final String seachInfoJson = (cidsBean != null) ? (String)cidsBean.getProperty("conf_json") : null;
            AlboFlaecheSearch.FlaecheSearchInfo searchInfo;
            try {
                searchInfo = (seachInfoJson != null)
                    ? AlboFlaecheSearch.OBJECT_MAPPER.readValue(
                        seachInfoJson,
                        AlboFlaecheSearch.FlaecheSearchInfo.class) : null;
            } catch (final Exception ex) {
                searchInfo = null;
            }
            alboFlaecheSearchPanel1.initFromSeachInfo(searchInfo);
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        initComponents();

        alboFlaecheSearchPanel1.initWithConnectionContext(connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BindingGroup getBindingGroup() {
//        return bindingGroup;
        return null;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    @Override
    public boolean prepareForSave() {
        try {
            cidsBean.setProperty(
                "conf_json",
                AlboFlaecheSearch.OBJECT_MAPPER.writeValueAsString(alboFlaecheSearchPanel1.createSearchInfo()));
            return true;
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return false;
        }
    }
}
