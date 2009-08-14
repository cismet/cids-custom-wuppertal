/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objectrenderer.utils.AbstractJasperReportPrint;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUIUtils;
import de.cismet.cids.custom.wunda_blau.res.StaticProperties;
import de.cismet.cids.dynamics.CidsBean;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author srichter
 */
public class StadtbildJasperReportPrint extends AbstractJasperReportPrint {

    public static final ImageIcon WUPPERWURM = new ImageIcon(StadtbildJasperReportPrint.class.getResource("/res/WupperWurm.gif"));

    public StadtbildJasperReportPrint(String reportURL, Collection<CidsBean> beans) {
        super(reportURL, beans);
        setBeansCollection(false);
    }

    public StadtbildJasperReportPrint(String reportURL, CidsBean bean) {
        super(reportURL, bean);
        setBeansCollection(false);
    }

    @Override
    public Map generateReportParam(final CidsBean current) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("wupperwurm", WUPPERWURM.getImage());
        if (current != null) {
            final Object bnr = current.getProperty("bildnummer");
            if (bnr != null) {
                try {
                    final String imageURL = StaticProperties.URL_PREFIX + bnr + StaticProperties.URL_SUFFIX;
                    log.fatal(imageURL);
                    final ImageIcon ii = ObjectRendererUIUtils.loadPicture(imageURL, 300, 300, 0);
                    log.fatal(ii);
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
}
