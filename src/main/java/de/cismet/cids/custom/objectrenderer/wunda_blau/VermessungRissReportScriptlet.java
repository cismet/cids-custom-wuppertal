/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.security.WebAccessManager;

import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@Deprecated
public class VermessungRissReportScriptlet {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Logger LOG = Logger.getLogger(VermessungRissReportScriptlet.class);
        
    private static final de.cismet.cids.custom.utils.alkis.VermessungRissReportScriptlet DELEGATE = de.cismet.cids.custom.utils.alkis.VermessungRissReportScriptlet.getInstance();

    
    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   host        DOCUMENT ME!
     * @param   schluessel  DOCUMENT ME!
     * @param   gemarkung   DOCUMENT ME!
     * @param   flur        DOCUMENT ME!
     * @param   blatt       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Boolean isImageAvailable(final String host,
            final String schluessel,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        return DELEGATE.isImageAvailable(host, schluessel, gemarkung, flur, blatt, WebAccessManager.getInstance());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   imageToRotate  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BufferedImage rotate(final BufferedImage imageToRotate) {
        return DELEGATE.rotate(imageToRotate);
    }
   
}
