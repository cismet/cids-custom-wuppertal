/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.utils;

import de.cismet.cids.custom.utils.BaulastenPictureFinder;

import de.cismet.security.WebAccessManager;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class WebAccessBaulastenPictureFinder extends BaulastenPictureFinder {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WebAccessBaulastenPictureFinder object.
     */
    protected WebAccessBaulastenPictureFinder() {
        super(WebAccessManager.getInstance(), ClientStaticProperties.getInstance());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static WebAccessBaulastenPictureFinder getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final WebAccessBaulastenPictureFinder INSTANCE;

        static {
            try {
                INSTANCE = new WebAccessBaulastenPictureFinder();
            } catch (final Exception ex) {
                throw new RuntimeException("Exception while initializing BaulastenPictureFinder", ex);
            }
        }

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
