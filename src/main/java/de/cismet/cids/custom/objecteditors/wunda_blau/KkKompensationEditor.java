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

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import java.beans.PropertyChangeEvent;

import java.util.List;

import de.cismet.cids.custom.wunda_blau.search.server.KkVerfahrenSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.server.connectioncontext.ClientConnectionContext;
import de.cismet.cids.server.connectioncontext.ClientConnectionContextProvider;
import de.cismet.cids.server.search.AbstractCidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class KkKompensationEditor extends KkVerfahrenEditor implements EditorSaveListener,
    ClientConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KkKompensationEditor.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean kompensationBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KkKompensationEditor object.
     */
    public KkKompensationEditor() {
        super();
    }

    /**
     * Creates a new KkKompensationEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public KkKompensationEditor(final boolean editable) {
        super(editable);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.kompensationBean = cidsBean;
        if (cidsBean != null) {
            try {
                final AbstractCidsServerSearch search = new KkVerfahrenSearch(cidsBean.getMetaObject().getId());
                final List res = (List)SessionManager.getProxy()
                            .customServerSearch(SessionManager.getSession().getUser(),
                                    search,
                                    getClientConnectionContext());

                if ((res != null) && (res.size() == 1)) {
                    setVerfahrenBean(((MetaObject)res.get(0)).getBean());
                    selectKompensation(cidsBean);
                } else {
                    setVerfahrenBean(null);
                    LOG.error("Cannot retrieve verfahren object");
                }
            } catch (Exception e) {
                setVerfahrenBean(null);
                LOG.error("Error while retrieving verfahren object", e);
            }
        } else {
            setVerfahrenBean(null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  verfahrenBean  DOCUMENT ME!
     */
    private void setVerfahrenBean(final CidsBean verfahrenBean) {
        if (editable && (super.getCidsBean() != null)) {
            LOG.info("remove propchange verfahren: " + super.getCidsBean());
            super.getCidsBean().removePropertyChangeListener(this);
        }

        super.setCidsBean(verfahrenBean);

        if (editable && (super.getCidsBean() != null)) {
            LOG.info("add propchange verfahren: " + super.getCidsBean());
            super.getCidsBean().addPropertyChangeListener(this);
        }
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        setCidsBean(null);
        super.editorClosed(event);
    }

    @Override
    public boolean prepareForSave() {
        try {
            if (super.prepareForSave()) {
                final CidsBean verfahrenBean = super.getCidsBean();
                if (verfahrenBean != null) {
                    super.setCidsBean(verfahrenBean.persist());
                    return true;
                }
            }
        } catch (Exception ex) {
            LOG.error("Cannot persist object", ex);
        }
        return false;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (editable) {
            LOG.info("propchange " + evt.getPropertyName() + " " + evt.getNewValue());
            kompensationBean.setArtificialChangeFlag(true);
        }
    }

    @Override
    public void dispose() {
        setCidsBean(null);
        super.dispose();
    }

    @Override
    public ClientConnectionContext getClientConnectionContext() {
        return ClientConnectionContext.create(getClass().getSimpleName());
    }
}
