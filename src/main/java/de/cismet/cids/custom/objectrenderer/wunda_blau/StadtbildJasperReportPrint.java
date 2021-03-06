/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.objectrenderer.utils.AbstractJasperReportPrint;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.StadtbilderUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class StadtbildJasperReportPrint extends AbstractJasperReportPrint {

    //~ Static fields/initializers ---------------------------------------------

    public static final ImageIcon WUPPERWURM = new ImageIcon(StadtbildJasperReportPrint.class.getResource(
                "/de/cismet/cids/custom/wunda_blau/res/WupperWurm.gif"));
//    public static final ImageIcon WUPPERWURM = new ImageIcon(StadtbildJasperReportPrint.class.getResource("/res/WupperWurm.gif"));

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StadtbildJasperReportPrint object.
     *
     * @param  reportURL  DOCUMENT ME!
     * @param  beans      DOCUMENT ME!
     */
    public StadtbildJasperReportPrint(final String reportURL, final Collection<CidsBean> beans) {
        super(reportURL, beans);
        setBeansCollection(false);
    }

    /**
     * Creates a new StadtbildJasperReportPrint object.
     *
     * @param  reportURL  DOCUMENT ME!
     * @param  bean       DOCUMENT ME!
     */
    public StadtbildJasperReportPrint(final String reportURL, final CidsBean bean) {
        super(reportURL, bean);
        setBeansCollection(false);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Map generateReportParam(final CidsBean current) {
        final Map<String, Object> params = new HashMap<>();
        params.put("wupperwurm", WUPPERWURM.getImage());
        if (current != null) {
            final String bnr = (String)current.getProperty("bildnummer");
            if (bnr != null) {
                try {
                    final String imageURL = StadtbilderUtils.getArcUrlPath(bnr);
                    log.info(imageURL);
                    final ImageIcon ii = ObjectRendererUtils.loadPicture(imageURL, 300, 300, 0);
                    log.info(ii);
                    if (ii != null) {
                        params.put("image", ii.getImage());
                    }
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            }
        }
        return params;
    }

    @Override
    public Map generateReportParam(final Collection<CidsBean> beans) {
        return Collections.EMPTY_MAP;
    }
}
