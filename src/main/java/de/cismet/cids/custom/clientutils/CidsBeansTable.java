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
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DatePickerCellEditor;

import java.sql.Date;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableComboboxCellEditor;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CidsBeansTable extends JXTable implements ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(CidsBeansTable.class);

    //~ Instance fields --------------------------------------------------------

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeansTable object.
     */
    public CidsBeansTable() {
        this(true);
    }

    /**
     * Creates a new CidsBeansTable object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public CidsBeansTable(final boolean editable) {
        setEditable(editable);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  model  DOCUMENT ME!
     */
    public void setModel(final CidsBeansTableModel model) {
        super.setModel(model);
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int column) {
        final TableModel model = getModel();
        if (model == null) {
            return null;
        }
        if ((row < 0) || (column < 0) || (row >= model.getRowCount()) || (column >= model.getColumnCount())) {
            return null;
        }
        if (!getModel().isCellEditable(row, column)) {
            return null;
        }
        final Class columnClass = model.getColumnClass(column);
        if (columnClass != null) {
            if (CidsBean.class.equals(columnClass)) {
                if (model instanceof CidsBeansTableModel) {
                    final CidsBeansTableModel cidsBeanModel = (CidsBeansTableModel)model;
                    final CidsBean cidsBean = cidsBeanModel.getCidsBean(row);
                    if (cidsBean != null) {
                        final String domain = cidsBean.getMetaObject().getDomain();
                        final String columnProperty = cidsBeanModel.getColumnProperty(column);
                        final int classId = cidsBean.getMetaObject()
                                    .getAttributeByFieldName(columnProperty)
                                    .getMai()
                                    .getForeignKeyClassId();
                        try {
                            final MetaClass metaClass = SessionManager.getProxy()
                                        .getMetaClass(classId, domain, getConnectionContext());
                            return new DefaultBindableComboboxCellEditor(metaClass);
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            return null;
                        }
                    }
                }
            } else if (Date.class.equals(columnClass)) {
                return new DatePickerCellEditor();
            }
        }
        return super.getCellEditor(row, column);
    }
}
