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
package de.cismet.cids.custom.objecteditors.wunda_blau.albo;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.BindingGroup;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.AlboFlaecheEditor;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractAlboFlaechePanel extends javax.swing.JPanel implements DisposableCidsBeanStore,
    EditorSaveListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Logger LOG = Logger.getLogger(AlboFlaecheEditor.class);

    //~ Instance fields --------------------------------------------------------

    private final boolean dummy;
    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Variables declaration - do not modify End of variables declaration.
     */
    public AbstractAlboFlaechePanel() {
        dummy = true;
        editable = true;
    }

    /**
     * Creates a new AbstractAlboFlaechePanel object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public AbstractAlboFlaechePanel(final boolean editable) {
        this.dummy = false;
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract BindingGroup getBindingGroup();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDummy() {
        return dummy;
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
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initGui();
    }

    /**
     * DOCUMENT ME!
     */
    protected abstract void initGui();

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (!isDummy()) {
            if (getBindingGroup() != null) {
                getBindingGroup().unbind();
            }
            if (cidsBean != null) {
                if (getBindingGroup() != null) {
                    DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                        getBindingGroup(),
                        cidsBean,
                        getConnectionContext());
                }
            }
            this.cidsBean = cidsBean;
            if (getBindingGroup() != null) {
                if (cidsBean != null) {
                    getBindingGroup().bind();
                }
                if (!isEditable()) {
                    RendererTools.makeReadOnly(getBindingGroup(), "cidsBean");
                }
            }
        }
    }

    @Override
    public void dispose() {
        setCidsBean(null);
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
    }

    @Override
    public boolean prepareForSave() {
        return true;
    }
}
