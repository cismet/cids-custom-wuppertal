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

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class SideComboBox extends JComboBox<String> implements CidsBeanStore, PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(SideComboBox.class);
    private static final String RIGHT = "rechts";
    private static final String LEFT = "links";
    private static final String BOTH = "beide";

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private boolean changedFromCombo = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SideComboBox object.
     */
    public SideComboBox() {
        setModel(new DefaultComboBoxModel<String>(new String[] { RIGHT, LEFT, BOTH }));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Side getSelectedSide() {
        if (getSelectedIndex() == -1) {
            return null;
        } else {
            return Side.values()[getSelectedIndex()];
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        if (this.cidsBean != null) {
            final CidsBean positionBean = (CidsBean)this.cidsBean.getProperty("position");

            if (positionBean != null) {
                positionBean.removePropertyChangeListener(this);
            }
        }
        this.cidsBean = cb;

        if (cb != null) {
            final Integer wo = (Integer)cb.getProperty("position.wo");
            setSelectedItem(getStringFromInteger(wo));
        } else {
            setSelectedItem(null);
        }
        if (cidsBean != null) {
            final CidsBean positionBean = (CidsBean)this.cidsBean.getProperty("position");

            if (positionBean != null) {
                positionBean.addPropertyChangeListener(this);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wo  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getStringFromInteger(final Integer wo) {
        if (wo == null) {
            return BOTH;
        }

        switch (wo) {
            case 0: {
                return RIGHT;
            }
            case 1: {
                return LEFT;
            }
            case 2: {
                return BOTH;
            }
            default: {
                return BOTH;
            }
        }
    }

    @Override
    public void setSelectedItem(final Object anObject) {
        super.setSelectedItem(anObject);

        if (cidsBean != null) {
            try {
                changedFromCombo = true;
                cidsBean.setProperty("position.wo", getSelectedSide().ordinal());
                changedFromCombo = false;
            } catch (Exception ex) {
                LOG.error("Cannot set property wo", ex);
            }
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (cidsBean != null) {
            if (evt.getPropertyName().equals("wo") && !changedFromCombo) {
                setSelectedItem(getStringFromInteger((Integer)cidsBean.getProperty("position.wo")));
            }
        }
    }
}
