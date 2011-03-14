/*
 *  Copyright (C) 2011 jweintraut
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

package de.cismet.cids.custom.actions.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.newuser.User;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.utils.MetaClassCacheService;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.features.CommonFeatureAction;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.tools.gui.StaticSwingTools;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jweintraut
 */
@ServiceProvider(service = CommonFeatureAction.class)
public class SetTIMNoteAction extends AbstractAction implements CommonFeatureAction {
    private static Logger LOG = Logger.getLogger(SetTIMNoteAction.class);

    private Feature feature;
    private boolean isCurrentUserAllowedToSetHint;
    private MetaClass timLiegMetaClass;


    public SetTIMNoteAction() {
        super(NbBundle.getMessage(SetTIMNoteAction.class, "SetTIMNoteAction.name"),
            new javax.swing.ImageIcon(
                SetTIMNoteAction.class.getResource("/de/cismet/cids/custom/actions/wunda_blau/tag_blue_add.png")));

        try {
            final MetaClassCacheService classcache = Lookup.getDefault().lookup(MetaClassCacheService.class);
            timLiegMetaClass = classcache.getMetaClass(SessionManager.getSession().getUser().getDomain(), "tim_lieg");
            isCurrentUserAllowedToSetHint = timLiegMetaClass.getPermissions()
                        .hasWritePermission(SessionManager.getSession().getUser().getUserGroup());
        } catch (Exception e) {
            LOG.error(
                "An error occurred while trying to set up SetTIMNoteAction. There was a problem with the lookup mechanism or session handling.",
                e);
            setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final String hinweis = JOptionPane.showInputDialog(
                StaticSwingTools.getParentFrame(CismapBroker.getInstance().getMappingComponent()),
                NbBundle.getMessage(SetTIMNoteAction.class, "SetTIMNoteAction.actionPerformed(ActionEvent).JOptionPane.message"),
                NbBundle.getMessage(SetTIMNoteAction.class, "SetTIMNoteAction.actionPerformed(ActionEvent).JOptionPane.title"),
                JOptionPane.QUESTION_MESSAGE);

        if(hinweis == null || hinweis.trim().length() <= 0) {
            return;
        }

        try {
            final CidsBean hint = CidsBeanSupport.createNewCidsBeanFromTableName("tim_lieg");
            final CidsBean geometry = CidsBeanSupport.createNewCidsBeanFromTableName("geom");

            final User usr = SessionManager.getSession().getUser();
            hint.setProperty("ein_beab", usr.getName());
            hint.setProperty("ein_dat", new java.sql.Timestamp(System.currentTimeMillis()));
            hint.setProperty("name", hinweis + " (" + usr.getName() + ")");
            hint.setProperty("hinweis", hinweis);

            // TODO: Should be centralised somewhere. It's the third occurrence of this calculation.
            int srid = feature.getGeometry().getSRID();
            final int defaultSrid = CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getDefaultCrs());
            if (srid == CismapBroker.getInstance().getDefaultCrsAlias()) {
                srid = defaultSrid;
            }
            if (srid != defaultSrid) {
                feature.setGeometry(CrsTransformer.transformToDefaultCrs(feature.getGeometry()));
            }
            feature.getGeometry().setSRID(CismapBroker.getInstance().getDefaultCrsAlias());

            geometry.setProperty("geo_field", feature.getGeometry());
            hint.setProperty("georeferenz", geometry);

            hint.persist();
        } catch (Exception ex) {
            LOG.error("Could not persist new entity for table 'tim_lieg'.", ex);
            JOptionPane.showMessageDialog(CismapBroker.getInstance().getMappingComponent(),
                NbBundle.getMessage(SetTIMNoteAction.class, "SetTIMNoteAction.actionPerformed(ActionEvent).errorMessage"),
                NbBundle.getMessage(SetTIMNoteAction.class, "SetTIMNoteAction.actionPerformed(ActionEvent).errorTitle"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void setSourceFeature(final Feature source) {
        feature = source;
    }

    @Override
    public Feature getSourceFeature() {
        return feature;
    }

    @Override
    public boolean isActive() {
        return isCurrentUserAllowedToSetHint && (feature instanceof PureNewFeature);
    }

    @Override
    public int getSorter() {
        return 10;
    }
}
