/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab;

import Sirius.server.middleware.types.MetaClass;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.cismet.cids.custom.wunda.oab.mapvis.Oab_BerechnungMapVisualisationProvider;
import de.cismet.cids.custom.wunda.oab.mapvis.Oab_GewaessereinzugsgebietMapVisualisationProvider;
import de.cismet.cids.custom.wunda.oab.mapvis.Oab_ProjektMapVisualisationProvider;
import de.cismet.cids.custom.wunda.oab.mapvis.Oab_Zustand_MassnahmeMapVisualisationProvider;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.navigatorplugin.MapVisualisationProvider;

import de.cismet.ext.CExtContext;
import de.cismet.ext.CExtProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@ServiceProvider(
    service = CExtProvider.class,
    position = 1000
)
public class OabMapVisCExtProvider implements CExtProvider<MapVisualisationProvider> {

    //~ Instance fields --------------------------------------------------------

    private final String ifaceClass;
    private final String concreteClass1;
    private final String concreteClass2;
    private final String concreteClass3;
    private final String concreteClass4;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new OabMapVisCExtProvider object.
     */
    public OabMapVisCExtProvider() {
        ifaceClass = "de.cismet.cismap.navigatorplugin.MapVisualisationProvider";                                     // NOI18N
        concreteClass1 = "de.cismet.cids.custom.wunda.oab.mapvis.Oab_GewaessereinzugsgebietMapVisualisationProvider"; // NOI18N
        concreteClass2 = "de.cismet.cids.custom.wunda.oab.mapvis.Oab_ProjektMapVisualisationProvider";                // NOI18N
        concreteClass3 = "de.cismet.cids.custom.wunda.oab.mapvis.Oab_Zustand_MassnahmeMapVisualisationProvider";      // NOI18N
        concreteClass4 = "de.cismet.cids.custom.wunda.oab.mapvis.Oab_BerechnungMapVisualisationProvider";             // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<? extends MapVisualisationProvider> provideExtensions(final CExtContext context) {
        final Object ref = context.getProperty(CExtContext.CTX_REFERENCE);
        final MetaClass mc;
        if (ref instanceof MetaClass) {
            mc = (MetaClass)ref;
        } else if (ref instanceof CidsBean) {
            mc = ((CidsBean)ref).getMetaObject().getMetaClass();
        } else {
            mc = null;
        }

        if (mc == null) {
            return new ArrayList(0);
        } else if (OabUtilities.OAB_GEWAESSEREINZUGSGEBIET_TABLE_NAME.equals(mc.getTableName())) {
            return Arrays.asList(new Oab_GewaessereinzugsgebietMapVisualisationProvider());
        } else if (OabUtilities.OAB_PROJEKT_TABLE_NAME.equals(mc.getTableName())) {
            return Arrays.asList(new Oab_ProjektMapVisualisationProvider());
        } else if (OabUtilities.OAB_ZUSTAND_MASSNAHME_TABLE_NAME.equals(mc.getTableName())) {
            return Arrays.asList(new Oab_Zustand_MassnahmeMapVisualisationProvider());
        } else if (OabUtilities.OAB_BERECHNUNG_TABLE_NAME.equals(mc.getTableName())) {
            return Arrays.asList(new Oab_BerechnungMapVisualisationProvider());
        } else {
            return new ArrayList(0);
        }
    }

    @Override
    public Class<? extends MapVisualisationProvider> getType() {
        return MapVisualisationProvider.class;
    }

    @Override
    public boolean canProvide(final Class<?> c) {
        return c.getCanonicalName().equals(ifaceClass)
                    || c.getCanonicalName().equals(concreteClass1)
                    || c.getCanonicalName().equals(concreteClass2)
                    || c.getCanonicalName().equals(concreteClass3)
                    || c.getCanonicalName().equals(concreteClass4);
    }
}
