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

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FileWithoutSuffix object.
     *
     * @param  number  DOCUMENT ME!
     * @param  file    DOCUMENT ME!
     */
    public FileWithoutSuffix(final Integer number, final String file) {
        this.number = number;
        this.file = file;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String toString() {
        if (number == null) {
            return file.toUpperCase();
        } else {
            return getFolder(number) + BaulastenPictureFinder.SEP + file.toUpperCase();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   number  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getFolder(final int number) {
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
        return new StringBuffer("docs/Baulasten/").append(lb).append("-").append(hb).toString();
    }
}
