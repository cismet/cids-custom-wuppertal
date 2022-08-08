/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.utils;

import org.jdom.Element;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.cismet.tools.configuration.Configurable;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class DmsUrlPathMapper implements Configurable {

    //~ Instance fields --------------------------------------------------------

    private final HashMap<String, String> networkToLocalMap = new HashMap<>();
    private final HashMap<String, String> localToNetworkMap = new HashMap<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DmsUrlPathMapper object.
     */
    private DmsUrlPathMapper() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static DmsUrlPathMapper getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  networkPath  DOCUMENT ME!
     * @param  localPath    DOCUMENT ME!
     */
    public void map(final String networkPath, final String localPath) {
        networkToLocalMap.put(networkPath, localPath);
        localToNetworkMap.put(localPath, networkPath);
    }

    /**
     * DOCUMENT ME!
     */
    public void unmapAll() {
        networkToLocalMap.clear();
        localToNetworkMap.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  networkPath  DOCUMENT ME!
     */
    public void unmapNetwork(final String networkPath) {
        final String localPath = networkToLocalMap.remove(networkPath);
        if (localPath != null) {
            localToNetworkMap.remove(localPath);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  localPath  DOCUMENT ME!
     */
    public void unmapLocal(final String localPath) {
        final String networkPath = localToNetworkMap.remove(localPath);
        if (networkPath != null) {
            networkToLocalMap.remove(networkPath);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   networkPath  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getLocalPath(final String networkPath) {
        return networkToLocalMap.get(networkPath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   localPath  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getNetworkPath(final String localPath) {
        return localToNetworkMap.get(localPath);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<String> getAllNetworkPaths() {
        return networkToLocalMap.keySet();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<String> getAllLocalPaths() {
        return localToNetworkMap.keySet();
    }

    @Override
    public Element getConfiguration() {
        final Element conf = new Element("dmsUrlPathMapperConfiguration"); // NOI18N

        final Element allMappingConf = new Element("Mappings");
        for (final String networkPath : networkToLocalMap.keySet()) {
            final String localPath = networkToLocalMap.get(networkPath);

            final Element element = new Element("Mapping");
            element.setAttribute("networkPath", networkPath);
            element.setAttribute("localPath", localPath);

            allMappingConf.addContent(element);
        }
        conf.addContent(allMappingConf);
        return conf;
    }

    @Override
    public void configure(final Element parent) {
        unmapAll();

        if (parent != null) {
            final Element conf = parent.getChild("dmsUrlPathMapperConfiguration");

            if (conf != null) {
                final Element mappingsElement = conf.getChild("Mappings");
                if (mappingsElement != null) {
                    final List<Element> mappingElements = mappingsElement.getChildren();

                    for (final Element mappingElement : mappingElements) {
                        final String networkPath = mappingElement.getAttribute("networkPath").getValue();
                        final String localPath = mappingElement.getAttribute("localPath").getValue();
                        map(networkPath, localPath);
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String replaceNetworkPath(final String url) {
        for (final String networkPath : getAllNetworkPaths()) {
            if (url.startsWith(networkPath)) {
                return new File(url.replaceAll(
                            "^"
                                    + Pattern.quote(networkPath),
                            Matcher.quoteReplacement(getLocalPath(networkPath))).replaceAll(
                            "[/\\\\]",
                            Matcher.quoteReplacement(System.getProperty("file.separator")))).toString();
            }
        }
        return url;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String replaceLocalPath(final String url) {
        for (final String localPath : getAllLocalPaths()) {
            if (url.startsWith(localPath)) {
                return url.replaceAll("^" + Pattern.quote(localPath),
                            Matcher.quoteReplacement(getNetworkPath(localPath) + "/"))
                            .replaceAll("/", Matcher.quoteReplacement("\\"));
            }
        }
        return url;
    }

    @Override
    public void masterConfigure(final Element parent) {
        configure(parent);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final DmsUrlPathMapper INSTANCE = new DmsUrlPathMapper();
    }
}
