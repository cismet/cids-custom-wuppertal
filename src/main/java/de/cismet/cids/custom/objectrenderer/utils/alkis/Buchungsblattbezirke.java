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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import java.util.HashMap;

import javax.swing.JOptionPane;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public class Buchungsblattbezirke {

    //~ Instance fields --------------------------------------------------------

    private HashMap<String, String> districtNamesMap;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Buchungsblattbezirke object.
     */
    public Buchungsblattbezirke() {
        setDistrictNamesMap(new HashMap<String, String>());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<String, String> getDistrictNamesMap() {
        return districtNamesMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  districtNamesMap  DOCUMENT ME!
     */
    public void setDistrictNamesMap(final HashMap<String, String> districtNamesMap) {
        this.districtNamesMap = districtNamesMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final Buchungsblattbezirke config = mapper.readValue(Buchungsblattbezirke.class.getResourceAsStream(
                        "/de/cismet/cids/custom/wunda_blau/res/alkis/buchungsblattbezirke.json"),
                    Buchungsblattbezirke.class);
            System.out.println(config.getDistrictNamesMap().get("053278"));
            JOptionPane.showMessageDialog(null, config.getDistrictNamesMap().get("053278"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
