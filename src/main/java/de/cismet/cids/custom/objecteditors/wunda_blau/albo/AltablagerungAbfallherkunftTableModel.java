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

import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AltablagerungAbfallherkunftTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Logger LOG = Logger.getLogger(AltablagerungAbfallherkunftTableModel.class);

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> cidsBeans;
    private final boolean editable;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AltablagerungAbfallherkunftTableModel object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public AltablagerungAbfallherkunftTableModel(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        setCidsBeans(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        this.cidsBeans = cidsBeans;
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getRowIndex(final CidsBean cidsBean) {
        return cidsBeans.indexOf(cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void add(final CidsBean cidsBean) {
        cidsBeans.add(cidsBean);
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void remove(final CidsBean cidsBean) {
        cidsBeans.remove(cidsBean);
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    @Override
    public String getColumnName(final int columnIndex) {
        if (columnIndex == 0) {
            return "Abfallherkunft";
        } else {
            return "überwiegend";
        }
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return editable;
    }

    @Override
    public int getRowCount() {
        return (cidsBeans != null) ? cidsBeans.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean(final int rowIndex) {
        return cidsBeans.get(rowIndex);
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        final CidsBean cidsBean = getCidsBean(row);
        if (column == 0) {
            return cidsBean.getProperty("fk_abfallherkunft");
        } else {
            return cidsBean.getProperty("ueberwiegend");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  value   DOCUMENT ME!
     * @param  row     DOCUMENT ME!
     * @param  column  DOCUMENT ME!
     */
    @Override
    public void setValueAt(final Object value, final int row, final int column) {
        if ((column != 0) && (column != 1)) {
            return;
        }

        final CidsBean cidsBean = getCidsBean(row);
        if (column == 0) {
            try {
                cidsBean.setProperty("fk_abfallherkunft", value);
            } catch (Exception ex) {
                LOG.error(ex, ex);
            }
        } else {
            try {
                cidsBean.setProperty("ueberwiegend", value);
            } catch (Exception ex) {
                LOG.error(ex, ex);
            }
        }
        fireTableRowsUpdated(row, row);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   columnIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        if (columnIndex == 0) {
            return CidsBean.class;
        } else {
            return Boolean.class;
        }
    }
}
