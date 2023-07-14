/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import org.apache.log4j.Logger;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class SimpleCategorizedWebDavPanel extends SimpleWebDavPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(SimpleCategorizedWebDavPanel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SimpleCategorizedWebDavPanel object.
     */
    public SimpleCategorizedWebDavPanel() {
    }

    /**
     * Creates a new SimpleCategorizedWebDavPanel object.
     *
     * @param  editable           DOCUMENT ME!
     * @param  beanCollProp       DOCUMENT ME!
     * @param  bildClassName      DOCUMENT ME!
     * @param  nameProp           DOCUMENT ME!
     * @param  tunnelAction       DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public SimpleCategorizedWebDavPanel(final boolean editable,
            final String beanCollProp,
            final String bildClassName,
            final String nameProp,
            final String tunnelAction,
            final ConnectionContext connectionContext) {
        super(editable, beanCollProp, bildClassName, nameProp, tunnelAction, connectionContext);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class ImageUploadWorker extends SwingWorker<Collection<CidsBean>, Void> {

        //~ Instance fields ----------------------------------------------------

        private final Collection<File> dokumente;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageUploadWorker object.
         *
         * @param  dokumente  DOCUMENT ME!
         */
        public ImageUploadWorker(final Collection<File> dokumente) {
            this.dokumente = dokumente;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Collection<CidsBean> doInBackground() throws Exception {
            final Collection<CidsBean> newBeans = new ArrayList<>();
            for (final File dokument : dokumente) {
                webdavHelper.uploadFileToWebDAV(dokument.getName(),
                    dokument,
                    SimpleCategorizedWebDavPanel.this,
                    getConnectionContext());

                final CidsBean newDokument = CidsBean.createNewCidsBeanFromTableName(
                        "WUNDA_BLAU",
                        bildClassName,
                        getConnectionContext());
                newDokument.setProperty(nameProp, dokument.getName());
                newDokument.setProperty("messstelle", cidsBean.getProperty("id"));
                newBeans.add(newDokument);
            }
            return newBeans;
        }

        @Override
        protected void done() {
            try {
                final Collection<CidsBean> newBeans = get();
                if (!newBeans.isEmpty()) {
                    final List<CidsBean> oldBeans = cidsBean.getBeanCollectionProperty(beanCollProp);
                    oldBeans.addAll(newBeans);
                    removeNewAddedFotoBean.addAll(newBeans);
                    lstFotos.setSelectedValue(newBeans.iterator().next(), true);
                }
            } catch (final InterruptedException ex) {
                LOG.warn(ex, ex);
            } catch (final ExecutionException ex) {
                LOG.error(ex, ex);
            }
        }
    }
}
