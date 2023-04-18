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
package de.cismet.cids.custom.reports.wunda_blau;

import lombok.Getter;
import lombok.Setter;

import java.awt.Image;

import java.util.List;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public abstract class AbstractReportBeanWithMapAndImages extends ReportBeanWithMap
        implements ConnectionContextProvider {

    //~ Instance fields --------------------------------------------------------

    private final ImageState[] imgStates = new ImageState[2];
    private final List<CidsBean> imageBeans;
    private final String positionProp;
    private final String filenameProp;

    private boolean img0Ready = false;
    private boolean img1Ready = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernBeanWithMapAndImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  geomProp           DOCUMENT ME!
     * @param  imgsProp           DOCUMENT ME!
     * @param  positionProp       DOCUMENT ME!
     * @param  filenameProp       DOCUMENT ME!
     * @param  mapUrl             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    protected AbstractReportBeanWithMapAndImages(final CidsBean cidsBean,
            final String geomProp,
            final String imgsProp,
            final String positionProp,
            final String filenameProp,
            final String mapUrl,
            final ConnectionContext connectionContext) {
        super(cidsBean, geomProp, mapUrl, connectionContext);
        this.imageBeans = (imgsProp != null) ? CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, imgsProp) : null;
        this.positionProp = positionProp;
        this.filenameProp = filenameProp;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPositionProp() {
        return positionProp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFilenameProp() {
        return filenameProp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getImageBeans() {
        return imageBeans;
    }

    /**
     * DOCUMENT ME!
     */
    protected abstract void initImgStates();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImg0() {
        return (imgStates[0] != null) ? imgStates[0].getImg() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  img0  DOCUMENT ME!
     */
    public void setImg0(final Image img0) {
        if (imgStates[0] != null) {
            imgStates[0].setImg(img0);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  imgState0  DOCUMENT ME!
     */
    public void setImgState0(final ImageState imgState0) {
        imgStates[0] = imgState0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  imgState1  DOCUMENT ME!
     */
    public void setImgState1(final ImageState imgState1) {
        imgStates[1] = imgState1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImg1() {
        return (imgStates[1] != null) ? imgStates[1].getImg() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  img1  DOCUMENT ME!
     */
    public void setImg1(final Image img1) {
        if (imgStates[1] != null) {
            imgStates[1].setImg(img1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean isReadyToProceed() {
        return super.isReadyToProceed() && isImg0Ready() && isImg1Ready();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isImg0Ready() {
        return img0Ready;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isImg1Ready() {
        return img1Ready;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  img0Ready  DOCUMENT ME!
     */
    protected void setImg0Ready(final boolean img0Ready) {
        this.img0Ready = img0Ready;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  img1Ready  DOCUMENT ME!
     */
    protected void setImg1Ready(final boolean img1Ready) {
        this.img1Ready = img1Ready;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    public class ImageState {

        //~ Instance fields ----------------------------------------------------

        private Image img;
        private boolean error;
    }
}
