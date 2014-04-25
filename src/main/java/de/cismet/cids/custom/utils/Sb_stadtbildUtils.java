/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_stadtbildUtils.class);

    private static final CidsBean WUPPERTAL;
    private static final CidsBean R102;

    static {
        WUPPERTAL = getOrtWupertal();
        R102 = getLagerR102();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the CidsBean of the Sb_Ort with the name 'Wuppertal'. Might be null.
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean getWUPPERTAL() {
        return WUPPERTAL;
    }
    /**
     * Get the CidsBean of the Sb_Lager with the name 'R102'. Might be null.
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean getR102() {
        return R102;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean getOrtWupertal() {
        try {
            final MetaClass ortClass = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "sb_ort");
            if (ortClass != null) {
                final StringBuffer wuppertalQuery = new StringBuffer("select ").append(ortClass.getId())
                            .append(", ")
                            .append(ortClass.getPrimaryKey())
                            .append(" from ")
                            .append(ortClass.getTableName())
                            .append(" where name ilike 'Wuppertal'");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: wuppertalQuery:" + wuppertalQuery.toString());
                }
                final MetaObject[] wuppertal;
                try {
                    wuppertal = SessionManager.getProxy().getMetaObjectByQuery(wuppertalQuery.toString(), 0);
                    if (wuppertal.length > 0) {
                        return wuppertal[0].getBean();
                    }
                } catch (ConnectionException ex) {
                    LOG.error(ex, ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("The Location Wuppertal could not be loaded.", ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean getLagerR102() {
        try {
            final MetaClass lagerClass = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "sb_lager");
            if (lagerClass != null) {
                final StringBuffer r102Query = new StringBuffer("select ").append(lagerClass.getId())
                            .append(", ")
                            .append(lagerClass.getPrimaryKey())
                            .append(" from ")
                            .append(lagerClass.getTableName())
                            .append(" where name = 'R102'");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: r102Query:" + r102Query.toString());
                }
                final MetaObject[] r102;
                try {
                    r102 = SessionManager.getProxy().getMetaObjectByQuery(r102Query.toString(), 0);
                    if (r102.length > 0) {
                        return r102[0].getBean();
                    }
                } catch (ConnectionException ex) {
                    LOG.error(ex, ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("The storage location R102 could not be loaded.", ex);
        }
        return null;
    }

    /**
     * Fills the model of combobox with the LightweightMetaObjects for all elements of a certain cids-class.
     * The method creates a temporary FastBindableReferenceCombo and set its MetaClass to the class given as argument.
     * Then the model of the FastBindableReferenceCombo is set as the model of the combobox.
     * Finally the combobox is decorated with the AutoCompleteDecorator.
     *
     * @param  combobox   The model of that combobox will be replaced
     * @param  className  the cids class name. The elements of this class will be fetched.
     */
    public static void setModelForComboBoxesAndDecorateIt(final JComboBox combobox, final String className) {
        final MetaClass metaClass = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", className);
        final DefaultComboBoxModel comboBoxModel;
        try {
            final FastBindableReferenceCombo tempFastBindableCombo = new FastBindableReferenceCombo();
            tempFastBindableCombo.setSorted(true);
            tempFastBindableCombo.setNullable(true);
            tempFastBindableCombo.setMetaClass(metaClass);
            comboBoxModel = (DefaultComboBoxModel)tempFastBindableCombo.getModel();
            combobox.setModel(comboBoxModel);
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(combobox);
    }
}
