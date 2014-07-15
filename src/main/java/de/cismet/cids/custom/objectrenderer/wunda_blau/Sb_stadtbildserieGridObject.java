/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.util.List;

import javax.swing.JComponent;

import de.cismet.cids.custom.utils.Sb_stadtbildUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import static de.cismet.cids.custom.objecteditors.wunda_blau.MauerEditor.adjustScale;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieGridObject implements CidsBeanStore {

    //~ Instance fields --------------------------------------------------------

    private CidsBean stadtbildserie;

    private float fraction;

    private int index;
    private int amountImages = 1;

    private boolean marker;

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean getCidsBean() {
        return stadtbildserie;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        stadtbildserie = cidsBean;
        final List images = stadtbildserie.getBeanCollectionProperty("stadtbilder_arr");
        if (!images.isEmpty()) {
            amountImages = images.size();
        } else {
            amountImages = 1;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   componentToShowImage  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImage(final JComponent componentToShowImage) {
        final String bildnummer;
        final List<CidsBean> images = stadtbildserie.getBeanCollectionProperty("stadtbilder_arr");
        if (!images.isEmpty() && (index < images.size())) {
            bildnummer = (String)images.get(index).getProperty("bildnummer");
        } else {
            bildnummer = (String)stadtbildserie.getProperty("vorschaubild.bildnummer");
        }

        try {
            final BufferedImage image = Sb_stadtbildUtils.downloadImageForBildnummer(bildnummer);
            return (Image)adjustScale(image, componentToShowImage, 0, 0);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  marker  DOCUMENT ME!
     */
    public void setMarker(final boolean marker) {
        this.marker = marker;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isMarker() {
        return marker;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndex() {
        return index;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float getFraction() {
        return fraction;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fraction  DOCUMENT ME!
     */
    public void setFraction(final float fraction) {
        this.fraction = Math.max(0.0f, Math.min(1.0f, fraction));
        this.index = (int)(this.fraction * (float)(amountImages));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getAmountImages() {
        return amountImages;
    }
}
