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

import org.apache.commons.lang.StringUtils;

import java.awt.Image;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieGridObject;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_RestrictionLevelUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_RestrictionLevelUtils.class);

    private static final CidsBean NO_RESTRICTION;

    private static Image FULL_RESTRICTION_IMAGE = Sb_stadtbildUtils.ERROR_IMAGE;
    private static Image NO_RESTRICTION_IMAGE = Sb_stadtbildUtils.ERROR_IMAGE;
    private static Image MIDDLE_RESTRICTION_IMAGE = Sb_stadtbildUtils.ERROR_IMAGE;
    private static String FULL_RESTRICTION_TOOLTIP = "";
    private static String NO_RESTRICTION_TOOLTIP = "";
    private static String MIDDLE_RESTRICTION_TOOLTIP = "";

    static {
        NO_RESTRICTION = getNutzungseinschraenkungNoRestriction();

        try {
            FULL_RESTRICTION_IMAGE = ImageIO.read(Sb_RestrictionLevelUtils.class.getResource(
                        "/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_red.png"));
            FULL_RESTRICTION_TOOLTIP = org.openide.util.NbBundle.getMessage(
                    Sb_RestrictionLevelUtils.class,
                    "Sb_stadtbildserieGridRenderer.determineColor().tooltip.fullRestriction");

            MIDDLE_RESTRICTION_IMAGE = ImageIO.read(Sb_RestrictionLevelUtils.class.getResource(
                        "/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_yellow.png"));
            MIDDLE_RESTRICTION_TOOLTIP = org.openide.util.NbBundle.getMessage(
                    Sb_RestrictionLevelUtils.class,
                    "Sb_stadtbildserieGridRenderer.determineColor().tooltip.middleRestriction");

            NO_RESTRICTION_IMAGE = ImageIO.read(Sb_RestrictionLevelUtils.class.getResource(
                        "/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_green.png"));
            NO_RESTRICTION_TOOLTIP = org.openide.util.NbBundle.getMessage(
                    Sb_RestrictionLevelUtils.class,
                    "Sb_stadtbildserieGridRenderer.determineColor().tooltip.NoRestriction");
        } catch (Exception ex) {
            LOG.error("Error in the static block", ex);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the CidsBean of the table SB_nutzungseinschraenkung with the key 'noRestriction'. Might be null.
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean getNoRestriction() {
        return NO_RESTRICTION;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean getNutzungseinschraenkungNoRestriction() {
        try {
            final MetaClass nutzungsClass = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "sb_nutzungseinschraenkung");
            if (nutzungsClass != null) {
                final StringBuffer noRestrictionQuery = new StringBuffer("select ").append(nutzungsClass.getId())
                            .append(", ")
                            .append(nutzungsClass.getPrimaryKey())
                            .append(" from ")
                            .append(nutzungsClass.getTableName())
                            .append(" where key ilike 'noRestriction'");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: wuppertalQuery:" + noRestrictionQuery.toString());
                }
                final MetaObject[] noRestriction;
                try {
                    noRestriction = SessionManager.getProxy().getMetaObjectByQuery(noRestrictionQuery.toString(), 0);
                    if (noRestriction.length > 0) {
                        return noRestriction[0].getBean();
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
     * Determine the restriction level for a stadtbildserie. The restriction level depends on the action tags which the
     * user possesses. It allows the user to download or preview stadtbilder of the serie.
     *
     * <p>The determined restriction level is saved in the extension attribute tmp_restriction_level, so the fetch is
     * faster for the CidsBean the next time.</p>
     *
     * @param   stadtbildserie  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static RestrictionLevel determineRestrictionLevelForStadtbildserie(final CidsBean stadtbildserie) {
        final RestrictionLevel level = new RestrictionLevel();
        if (stadtbildserie != null) {
            final Object tmp_level = stadtbildserie.getProperty("tmp_restriction_level");
            if (tmp_level instanceof RestrictionLevel) {
                return (RestrictionLevel)tmp_level;
            }

            final CidsBean nutzungseinschraenkung = (CidsBean)stadtbildserie.getProperty("nutzungseinschraenkung");
            if (nutzungseinschraenkung != null) {
                final String key = (String)nutzungseinschraenkung.getProperty("key");
                if (StringUtils.isNotBlank(key)) {
                    final String actionTagPreview = "custom.stadtbilder." + key + ".preview";
                    final String actionTagDownload = "custom.stadtbilder." + key + ".download";
                    final String actionTagInternalUsage = "custom.stadtbilder." + key + ".internalUsage";
                    final String actionTagExternalUsage = "custom.stadtbilder." + key + ".externalUsage";

                    final boolean previewAllowed = ObjectRendererUtils.checkActionTag(actionTagPreview);
                    final boolean downloadAllowed = ObjectRendererUtils.checkActionTag(actionTagDownload);
                    final boolean internalUsageAllowed = ObjectRendererUtils.checkActionTag(actionTagInternalUsage);
                    final boolean externalUsageAllowed = ObjectRendererUtils.checkActionTag(actionTagExternalUsage);

                    level.setPreviewAllowed(previewAllowed);
                    level.setDownloadAllowed(downloadAllowed);
                    level.setInternalUsageAllowed(internalUsageAllowed);
                    level.setExternalUsageAllowed(externalUsageAllowed);

                    try {
                        stadtbildserie.setProperty("tmp_restriction_level", level);
                    } catch (Exception ex) {
                        LOG.error("Could not set property tmp_restriction_level of the stadtbildserie", ex);
                    }
                }
            }
        }
        return level;
    }

    /**
     * Returns an array with two entries:
     *
     * <ul>
     *   <li>bullet point Image of the restriction</li>
     *   <li>information String about the restriction</li>
     * </ul>
     *
     * <p>To do this the RetsrictionLevel of {@code gridObject} is determined, afterwards it is checked if the user is
     * allowed to use the images of the Stadtbildserie internally or externally.</p>
     *
     * @param   stadtbildserie  gridObject DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Object[] determineBulletPointAndInfoText(final CidsBean stadtbildserie) {
        final RestrictionLevel level = Sb_RestrictionLevelUtils.determineRestrictionLevelForStadtbildserie(
                stadtbildserie);

        Image colorImage = FULL_RESTRICTION_IMAGE;
        String tooltipText = FULL_RESTRICTION_TOOLTIP;

        if (level.isInternalUsageAllowed()) {
            if (level.isExternalUsageAllowed()) {
                colorImage = NO_RESTRICTION_IMAGE;
                tooltipText = NO_RESTRICTION_TOOLTIP;
            } else {
                colorImage = MIDDLE_RESTRICTION_IMAGE;
                tooltipText = MIDDLE_RESTRICTION_TOOLTIP;
            }
        }

        return new Object[] { colorImage, tooltipText };
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class RestrictionLevel {

        //~ Instance fields ----------------------------------------------------

        private boolean previewAllowed;
        private boolean downloadAllowed;
        private boolean internalUsageAllowed;
        private boolean externalUsageAllowed;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isPreviewAllowed() {
            return previewAllowed;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  previewAllowed  DOCUMENT ME!
         */
        public void setPreviewAllowed(final boolean previewAllowed) {
            this.previewAllowed = previewAllowed;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isDownloadAllowed() {
            return downloadAllowed;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  downloadAllowed  DOCUMENT ME!
         */
        public void setDownloadAllowed(final boolean downloadAllowed) {
            this.downloadAllowed = downloadAllowed;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isInternalUsageAllowed() {
            return internalUsageAllowed;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  internalUsageAllowed  DOCUMENT ME!
         */
        public void setInternalUsageAllowed(final boolean internalUsageAllowed) {
            this.internalUsageAllowed = internalUsageAllowed;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isExternalUsageAllowed() {
            return externalUsageAllowed;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  externalUsageAllowed  DOCUMENT ME!
         */
        public void setExternalUsageAllowed(final boolean externalUsageAllowed) {
            this.externalUsageAllowed = externalUsageAllowed;
        }
    }
}
