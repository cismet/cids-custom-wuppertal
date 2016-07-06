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
package de.cismet.cids.custom.objectrenderer.utils;

import static de.cismet.cids.custom.objectrenderer.utils.BaulastenPictureFinder.PATH;
import static de.cismet.cids.custom.objectrenderer.utils.BaulastenPictureFinder.PATH_RS;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class FileWithoutSuffix {

    //~ Instance fields --------------------------------------------------------

    Integer number;
    String file;
    boolean checkReducedSize;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FileWithoutSuffix object.
     *
     * @param  number            DOCUMENT ME!
     * @param  checkReducedSize  DOCUMENT ME!
     * @param  file              DOCUMENT ME!
     */
    public FileWithoutSuffix(final Integer number, final boolean checkReducedSize, final String file) {
        this.number = number;
        this.file = file;
        this.checkReducedSize = checkReducedSize;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String toString() {
        return toString(checkReducedSize);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   reducedSize  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String toString(final boolean reducedSize) {
        if (number == null) {
            if (reducedSize) { // file.toUpperCase bacuase of cismet/wupp#942
                return BaulastenPictureFinder.PATH_RS + BaulastenPictureFinder.SEP + file.toUpperCase();
            } else {
                return BaulastenPictureFinder.PATH + BaulastenPictureFinder.SEP + file.toUpperCase();
            }
        } else {
            return getFolder(number, reducedSize) + BaulastenPictureFinder.SEP + file.toUpperCase();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   number       DOCUMENT ME!
     * @param   reducedSize  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getFolder(final int number, final boolean reducedSize) {
        int modulo = (number % 1000);
        if (modulo == 0) {
            modulo = 1000;
        }
        int lowerBorder = (number == 0) ? 0 : (number - modulo);
        final int higherBorder = lowerBorder + 1000;
        if (lowerBorder != 0) {
            lowerBorder += 1;
        }

        final String lb = String.format("%06d", lowerBorder);
        final String hb = String.format("%06d", higherBorder);
        return new StringBuffer(reducedSize ? PATH_RS : PATH).append(lb).append("-").append(hb).toString();
    }
}
