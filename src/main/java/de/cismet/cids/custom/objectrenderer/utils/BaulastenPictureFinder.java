package de.cismet.cids.custom.objectrenderer.utils;

import de.cismet.cids.custom.wunda_blau.res.StaticProperties;
import java.io.File;

/**
 *
 * @author srichter
 */
public final class BaulastenPictureFinder {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BaulastenPictureFinder.class);
    private static final String[] SUFFIXE = new String[]{"tif", "jpg", "tiff", "jpeg"};

    public static File findPlanPicture(String picture) {
        final String picturePath = StaticProperties.ALB_PLAN_URL_PREFIX + picture + ".";
        return probeForRightSuffix(picturePath);

    }

    public static File findTextblattPicture(String picture) {
        final String picturePath = StaticProperties.ALB_TEXTBLATT_URL_PREFIX + picture + ".";
        return probeForRightSuffix(picturePath);
    }

    private static File probeForRightSuffix(String fileWithoutSuffix) {
        log.debug("Searching for picture: " + fileWithoutSuffix);
        for (String suffix : SUFFIXE) {
            File testFile = new File(fileWithoutSuffix + suffix);
            if (testFile.isFile()) {
                log.debug("Found picture in file: " + testFile.getAbsolutePath());
                return testFile;
            }
        }
        log.debug("No picture file found.");
        return null;
    }
}
