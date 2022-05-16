/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.utils;

import Sirius.server.middleware.types.MetaClass;
import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

import java.awt.Component;
import java.awt.event.MouseEvent;

import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.apache.log4j.Logger;

/**
 * A table cell editor that shows a bindable combobox.
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class DefaultCismapGeometryComboBoxCellEditor extends AbstractCellEditor implements TableCellEditor {

    //~ Instance fields --------------------------------------------------------

    private final DefaultCismapGeometryComboBoxEditor comboBox;
    public static final String FIELD__GEO_FIELD = "geo_field"; // geom
    public static final String TABLE_GEOM = "geom";
    private static final Logger LOG = Logger.getLogger(DefaultCismapGeometryComboBoxCellEditor.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DefaultBindableComboboxCellEditor object.
     *
     */
    public DefaultCismapGeometryComboBoxCellEditor() {
        
        comboBox = new DefaultCismapGeometryComboBoxEditor(true);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isCellEditable(final EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent)anEvent).getClickCount() >= 2;
        }
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        ConnectionContext concon = ConnectionContext.create(AbstractConnectionContext.Category.STATIC, BaumConfProperties.class.getSimpleName());
        final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_GEOM,
                concon);
        CidsBean newGeom = null;
        if ( null != comboBox.getSelectedItem()){
            if (comboBox.getSelectedItem() instanceof PureNewFeature){
                final Geometry geom = ((PureNewFeature)comboBox.getSelectedItem()).getGeometry();
                newGeom = geomMetaClass.getEmptyInstance(concon).getBean();
                try {
                    newGeom.setProperty(FIELD__GEO_FIELD, geom);
                }catch (final Exception ex) {
                    LOG.warn("Geom not set.", ex);
                }
            } 
        }
        return newGeom;
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table,
            final Object value,
            final boolean isSelected,
            final int row,
            final int column) {
        comboBox.setSelectedItem(value);

        return comboBox;
    }
}
