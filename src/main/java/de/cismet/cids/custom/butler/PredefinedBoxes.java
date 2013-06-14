/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.butler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author daniel
 */
public class PredefinedBoxes {

    private final String displayName;
    private double eSize;
    private final double nSize;
    protected static ArrayList<PredefinedBoxes> elements = new ArrayList<PredefinedBoxes>();
    private static final Logger LOG = Logger.getLogger(PredefinedBoxes.class);

    static {
        final Properties prop = new Properties();
        try {
            prop.load(PredefinedBoxes.class.getResourceAsStream("predefinedBoxes.properties"));
            final Enumeration keys = prop.propertyNames();
            while (keys.hasMoreElements()) {
                final String key = (String) keys.nextElement();
                final String[] splittedVal = ((String) prop.getProperty(key)).split(";");
                elements.add(new PredefinedBoxes(splittedVal[0], Double.parseDouble(splittedVal[1]), Double.parseDouble(splittedVal[2])));
            }
        } catch (IOException ex) {
            LOG.error("Could not read property file with defined boxes for butler 1");
        }
    }

    private PredefinedBoxes(String displayName, double eSize, double nSize) {
        this.displayName = displayName;
        this.eSize = eSize;
        this.nSize = nSize;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getEastSize() {
        return eSize;
    }

    public double getNorthSize() {
        return nSize;
    }

    @Override
    public String toString() {
        return displayName; 
    }
    
}
