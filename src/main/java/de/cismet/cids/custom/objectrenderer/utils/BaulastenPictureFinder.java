package de.cismet.cids.custom.objectrenderer.utils;

import de.cismet.cids.custom.wunda_blau.res.StaticProperties;
import de.cismet.tools.collections.TypeSafeCollections;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 *
 * @author srichter
 */
public final class BaulastenPictureFinder {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BaulastenPictureFinder.class);
    private static final String[] SUFFIXE = new String[]{"tif", "jpg", "tiff", "jpeg"};
//    "TIF", "JPG", "TIFF", "JPEG"};

    public static List<File> findPlanPicture(String picture) {
        final String picturePath = StaticProperties.ALB_PLAN_URL_PREFIX + picture + ".";
        return probeForRightSuffix(picturePath);

    }

    public static List<File> findTextblattPicture(String picture) {
        final String picturePath = StaticProperties.ALB_TEXTBLATT_URL_PREFIX + picture + ".";
        return probeForRightSuffix(picturePath);
    }

    private static List<File> probeForRightSuffix(String fileWithoutSuffix) {
        log.debug("Searching for picture: " + fileWithoutSuffix + "xxx");
        List<File> results = TypeSafeCollections.newArrayList();
        for (String suffix : SUFFIXE) {
            try {
                URL fileURL = new URL(fileWithoutSuffix + suffix);
                File testFile = new File(fileURL.toURI());
                if (testFile.isFile()) {
                    log.debug("Found picture in file: " + testFile.getAbsolutePath());
                    results.add(testFile);
                }
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        }
        log.debug("No picture file found.");
        return results;
    }
}
