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

import java.util.Collection;
import java.util.List;

import de.cismet.cids.custom.wunda_blau.search.server.KkVerfahrenSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.server.search.AbstractCidsServerSearch;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class KkKompensationEditor extends KkVerfahrenEditor implements EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KkKompensationEditor.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean kompensationBean;
    private CidsBean verfahrenBean;

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
        kompensationBean = cidsBean;

        if (cidsBean != null) {
            try {
                final AbstractCidsServerSearch search = new KkVerfahrenSearch(cidsBean.getMetaObject().getId());
                final List res = (List)SessionManager.getProxy()
                            .customServerSearch(SessionManager.getSession().getUser(), search);

                if ((res != null) && (res.size() == 1)) {
                    verfahrenBean = ((MetaObject)res.get(0)).getBean();
                    verfahrenBean.addPropertyChangeListener(this);
                    super.setCidsBean(verfahrenBean);
                    selectKompensation(cidsBean);
                } else {
                    LOG.error("Cannot retrieve verfahren object");
                }
            } catch (Exception e) {
                LOG.error("Error while retrieving verfahren object", e);
            }
        }
    }

    @Override
    public void editorClosed(EditorClosedEvent event) {
        if (event.getStatus() == EditorSaveStatus.SAVE_SUCCESS) {
            try {
                verfahrenBean.persist();
            } catch (Exception ex) {
                LOG.error("Cannot log object", ex);
            }
        }
    }

    @Override
    public boolean prepareForSave() {
        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (editable) {
            kompensationBean.setArtificialChangeFlag(true);
        }
    }
}
