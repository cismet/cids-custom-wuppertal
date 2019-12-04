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
package de.cismet.cids.custom.wunda_blau.band;

import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TreppeObservableListListener implements ObservableListListener {

    //~ Instance fields --------------------------------------------------------

    protected CidsBean cidsBean;
    protected String collectionPropertyName;
    protected String secondCollectionPropertyName;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeObservableListListener object.
     *
     * @param  cidsBean                      DOCUMENT ME!
     * @param  collectionPropertyName        DOCUMENT ME!
     * @param  secondCollectionPropertyName  DOCUMENT ME!
     */
    public TreppeObservableListListener(final CidsBean cidsBean,
            final String collectionPropertyName,
            final String secondCollectionPropertyName) {
        this.cidsBean = cidsBean;
        this.collectionPropertyName = collectionPropertyName;
        this.secondCollectionPropertyName = secondCollectionPropertyName;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void listElementsAdded(final ObservableList list, final int index, final int length) {
        if (length == 1) {
            if (secondCollectionPropertyName != null) {
                final CidsBean bean = (CidsBean)list.get(index);
                List<CidsBean> all;

                if (bean.getClass().getName().endsWith("Treppe_podest")) {
                    all = cidsBean.getBeanCollectionProperty(secondCollectionPropertyName);
                } else {
                    all = cidsBean.getBeanCollectionProperty(collectionPropertyName);
                }
                all.add(bean);
            } else {
                final List<CidsBean> all = cidsBean.getBeanCollectionProperty(collectionPropertyName);

                final CidsBean bean = (CidsBean)list.get(index);
                all.add(bean);
            }
        } else {
            // not supported
        }
    }

    @Override
    public void listElementsRemoved(final ObservableList ol, final int i, final List oldElements) {
        List<CidsBean> all = cidsBean.getBeanCollectionProperty(collectionPropertyName);

        for (final Object b : oldElements) {
            all.remove((CidsBean)b);
        }
        all = cidsBean.getBeanCollectionProperty(secondCollectionPropertyName);

        for (final Object b : oldElements) {
            all.remove((CidsBean)b);
        }
    }

    @Override
    public void listElementReplaced(final ObservableList ol, final int i, final Object o) {
        // not used
    }

    @Override
    public void listElementPropertyChanged(final ObservableList ol, final int i) {
        // not used
    }
}
