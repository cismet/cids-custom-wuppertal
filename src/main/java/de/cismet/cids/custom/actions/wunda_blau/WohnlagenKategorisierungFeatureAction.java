/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.actions.wunda_blau;

/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2011 thorsten
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import org.openide.util.lookup.ServiceProvider;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.actions.WohnlagenKategorisierungServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.cidslayer.CidsLayerFeature;

import de.cismet.cismap.commons.features.CommonFeatureAction;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeaturesProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.custom.attributerule.WohnlageRuleSet;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CommonFeatureAction.class)
public class WohnlagenKategorisierungFeatureAction extends AbstractAction implements CommonFeatureAction,
    FeaturesProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            WohnlagenKategorisierungFeatureAction.class);

    private static final boolean IS_ACTIVE;
    private static final MetaClass META_CLASS;

    static {
        MetaClass metaClass = null;
        try {
            metaClass = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "WOHNLAGE");
        } catch (final Exception ex) {
            LOG.error("Could get MetaClass (WOHNLAGE)!", ex);
        }
        META_CLASS = metaClass;

        boolean isActive = false;
        try {
            isActive = SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(),
                                "csa://"
                                + WohnlagenKategorisierungServerAction.TASK_NAME)
                        != null;
        } catch (final Exception ex) {
            LOG.error("Could not validate action tag (custom.wohnlage.kategorisierung_featureaction)!", ex);
        }
        IS_ACTIVE = isActive;
    }

    //~ Instance fields --------------------------------------------------------

    private List<Feature> features = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DuplicateGeometryFeatureAction object.
     */
    public WohnlagenKategorisierungFeatureAction() {
        super("Wohnlage kategorisieren");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getSorter() {
        return 1;
    }

    @Override
    public Feature getSourceFeature() {
        return (features == null) ? null : (Feature)features.iterator().next();
    }

    @Override
    public boolean isActive() {
        return IS_ACTIVE;
    }

    @Override
    public void setSourceFeature(final Feature source) {
        features = Arrays.asList(source);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final List<CidsLayerFeature> cidsLayerFeatures = new ArrayList<CidsLayerFeature>();
        final boolean allSameKategorie = true;
        final CidsBean kategorie = null;
        for (final Feature feature : getSourceFeatures()) {
            if (feature instanceof CidsLayerFeature) {
                final CidsLayerFeature cidsLayerFeature = (CidsLayerFeature)feature;
                cidsLayerFeatures.add(cidsLayerFeature);
            }
        }

        final Frame frame = StaticSwingTools.getFirstParentFrame(CismapBroker.getInstance().getMappingComponent());
        final WohnlagenKategorisierungDialog dialog = new WohnlagenKategorisierungDialog(
                frame,
                cidsLayerFeatures,
                allSameKategorie ? kategorie : null);
        StaticSwingTools.showDialog(dialog);
    }

    @Override
    public boolean isResponsibleFor(final Feature feature) {
        if ((META_CLASS != null) && (feature instanceof CidsLayerFeature)) {
            final CidsLayerFeature cidsLayerFeature = (CidsLayerFeature)feature;
            return cidsLayerFeature.getLayerProperties().getAttributeTableRuleSet() instanceof WohnlageRuleSet;
        } else {
            return false;
        }
    }

    @Override
    public void setSourceFeatures(final List<Feature> features) {
        this.features = features;

        final int size = features.size();
        if (size == 1) {
            super.putValue(Action.NAME, "Wohnlage kategorisieren");
        } else {
            super.putValue(Action.NAME, features.size() + " Wohnlagen kategorisieren");
        }
    }

    @Override
    public List<Feature> getSourceFeatures() {
        return features;
    }
}
