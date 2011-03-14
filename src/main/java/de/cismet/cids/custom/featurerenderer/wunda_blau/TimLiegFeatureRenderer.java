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

package de.cismet.cids.custom.featurerenderer.wunda_blau;

import Sirius.navigator.exception.ConnectionException;
import Sirius.server.middleware.types.MetaObject;
import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;
import de.cismet.cismap.navigatorplugin.CidsFeature;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.sql.Timestamp;
import java.text.DateFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.apache.log4j.Logger;
import org.openide.util.NbBundle;

/**
 *
 * @author jweintraut
 */
public class TimLiegFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------
    private static Logger LOG = Logger.getLogger(TimLiegFeatureRenderer.class);

    private static final Color TIMLIEG_COLOR = new Color(0, 0, 255);

    //~ Instance fields --------------------------------------------------------

    private JPanel pnlMore;
    private JLabel lblEinBeabAndEinDat;
    private JTextArea txtAHinweis;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoHintFeatureRenderer object.
     */
    public TimLiegFeatureRenderer() {
        LOG.debug("FeatureRenderer constructor");
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Initializes the necessary components.
     */
    private void initComponents() {
        pnlMore = new JPanel();
        lblEinBeabAndEinDat = new JLabel();
        txtAHinweis = new JTextArea();

        txtAHinweis.setLineWrap(true);
        txtAHinweis.setWrapStyleWord(true);
        txtAHinweis.setEditable(false);
        txtAHinweis.setBorder(null);

        pnlMore.setLayout(new BorderLayout());
        pnlMore.add(lblEinBeabAndEinDat, BorderLayout.PAGE_START);
        pnlMore.add(txtAHinweis, BorderLayout.CENTER);

        add(pnlMore);
    }

    @Override
    public void setMetaObject(final MetaObject metaObject) throws ConnectionException {
        super.setMetaObject(metaObject);

        if (cidsBean != null) {
            final String ein_beab = (String)cidsBean.getProperty("ein_beab");
            final String hinweis = (String)cidsBean.getProperty("hinweis");
            final Timestamp ein_dat = (Timestamp)cidsBean.getProperty("ein_dat");

            final String date = DateFormat.getDateInstance().format(ein_dat);
            final String time = DateFormat.getTimeInstance().format(ein_dat);
            lblEinBeabAndEinDat.setText(NbBundle.getMessage(
                    TimLiegFeatureRenderer.class,
                    "TimLiegFeatureRenderer.lblUsrAndTimestamp.text",
                    ein_beab,
                    date,
                    time));
            txtAHinweis.setText(hinweis);
        }
    }

    @Override
    public Paint getLinePaint(final CidsFeature subFeature) {
        return getFillingStyle();
    }

    @Override
    public Paint getFillingStyle() {
        return TIMLIEG_COLOR;
    }

    @Override
    public void assign() {
        // NOP
    }
}
