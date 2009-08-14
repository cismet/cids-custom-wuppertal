/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils;

import de.cismet.cids.dynamics.CidsBean;
import java.awt.Font;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jdesktop.beansbinding.Converter;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author srichter
 */
public class XMLPropertyParser {

    private Document document;
    private List<String> properties;
    private List<String> description;
    private List<String> font;
    private List<Integer> size;
    private List<Boolean> bg;
    private List<Boolean> it;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(XMLPropertyParser.class);
//    private static final SQLDateToStringConverter DATE_CONVERTER = new SQLDateToStringConverter();
    private final Map<Class<?>, Converter<?, ?>> converters;

    public XMLPropertyParser(final InputStream is) {
        if (is == null) {
            throw new NullPointerException();
        }
        converters = new HashMap<Class<?>, Converter<?, ?>>();
        properties = new ArrayList<String>();
        description = new ArrayList<String>();
        font = new ArrayList<String>();
        size = new ArrayList<Integer>();
        bg = new ArrayList<Boolean>();
        it = new ArrayList<Boolean>();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            this.document = builder.parse(is);
            final NodeList nl = document.getElementsByTagName("property");
            for (int i = 0; i < nl.getLength(); ++i) {
                final Node cur = nl.item(i);
                properties.add(cur.getFirstChild().getNodeValue());
                final NamedNodeMap attr = cur.getAttributes();
                Node a = attr.getNamedItem("bg");
                if (a != null) {
                    bg.add("true".equalsIgnoreCase(a.getNodeValue()));
                } else {
                    bg.add(false);
                }
                a = attr.getNamedItem("it");
                if (a != null) {
                    it.add("true".equalsIgnoreCase(a.getNodeValue()));
                } else {
                    it.add(false);
                }
                a = attr.getNamedItem("size");
                if (a != null) {
                    size.add(Integer.parseInt(a.getNodeValue()));
                } else {
                    size.add(11);
                }
                a = attr.getNamedItem("font");
                if (a != null) {
                    font.add(a.getNodeValue());
                } else {
                    font.add("Tahoma");
                }
                a = attr.getNamedItem("description");
                if (a != null) {
                    description.add(a.getNodeValue());
                } else {
                    description.add("");
                }
            }
        } catch (Throwable ex) {
            log.error(ex, ex);
        }
    }

    private <T> Object convertProperty(final Object o) {
        if (o != null) {
            try {
                final Converter<T, ?> conv = (Converter<T, ?>) converters.get(o.getClass());
                if (conv != null) {
                    return conv.convertForward((T) o);
                }
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        }
        return o;
    }

    public <T> void addConverterForClass(final Class<T> clazz, final Converter<T, ?> con) {
        if (clazz != null && con != null) {
            converters.put(clazz, con);
        }
    }

    public void removeConverter(final Class<?> clazz) {
        converters.remove(clazz);
    }

    public Set<Class<?>> getConvertedClasses() {
        return converters.keySet();
    }

    public List<JLabel> getLabels(final CidsBean bean) {
        final List<JLabel> ret = new ArrayList<JLabel>();
        for (int i = 0; i < properties.size(); ++i) {
            try {
                final JLabel item = new JLabel();
                Object prop = bean.getProperty(properties.get(i));
                prop = convertProperty(prop);
                item.setText(description.get(i) + prop);
                int style = 0;
                if (bg.get(i)) {
                    style += Font.BOLD;
                }
                if (it.get(i)) {
                    style += Font.ITALIC;
                }
                final Font f = new Font(font.get(i), style, size.get(i));
                item.setFont(f);
                ret.add(item);
            } catch (Exception ex) {
                log.warn(ex, ex);
            }
        }
        return ret;
    }
}
