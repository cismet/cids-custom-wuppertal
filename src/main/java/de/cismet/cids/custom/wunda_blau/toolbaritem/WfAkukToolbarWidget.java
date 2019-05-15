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
package de.cismet.cids.custom.wunda_blau.toolbaritem;

import Sirius.navigator.types.treenode.DefaultMetaTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.event.ActionEvent;

import java.io.*;

import java.net.URL;

import java.util.*;

import javax.swing.AbstractAction;

import de.cismet.cids.navigator.utils.CidsClientToolbarItem;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.menu.CidsUiAction;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsUiAction.class)
public class WfAkukToolbarWidget extends AbstractAction implements CidsClientToolbarItem,
    CidsUiAction,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final String PROPERTIES_FILE = "/de/cismet/cids/custom/wunda_blau/toolbaritem/wfAkuk.properties";
    private static final Logger LOG = Logger.getLogger(WfAkukToolbarWidget.class);

    //~ Instance fields --------------------------------------------------------

    private String akukClassKey;
    private String exchangeFile;
    private String exchangeDirectory;
    private String triggerExe;
    private String fs;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WFAkukPlugin object.
     */
    public WfAkukToolbarWidget() {
        final Properties props = new Properties();
        InputStream is = null;

        try {
            is = this.getClass().getResourceAsStream(PROPERTIES_FILE);
            props.load(is);

            fs = "";
            akukClassKey = props.getProperty("akukClassKey");
            exchangeFile = props.getProperty("exchangeFile");
            exchangeDirectory = props.getProperty("exchangeDirectory");
            triggerExe = props.getProperty("exchangeTriggerExe");
            fs = System.getProperty("file.separator");
        } catch (Exception ex) {
            LOG.error("Error while reading WfAkuk properties file.", ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                LOG.error("Error while closing WfAkuk properties file.", ex);
            }
        }

        putValue(
            SHORT_DESCRIPTION,
            NbBundle.getMessage(WfAkukToolbarWidget.class, "WfAkukToolbarWidget.WfAkukToolbarWidget().tooltip"));
        final URL icon = getClass().getResource("/res/akuk.png");
        putValue(
            NAME,
            NbBundle.getMessage(WfAkukToolbarWidget.class, "WfAkukToolbarWidget.WfAkukToolbarWidget().name"));
        putValue(LARGE_ICON_KEY, new javax.swing.ImageIcon(icon));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getSorterString() {
        return "ZZZ";
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Object getValue(final String key) {
        if (key.equals(CidsUiAction.CIDS_ACTION_KEY)) {
            return "WfAkuk";
        } else {
            return super.getValue(key);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final Collection selectedNodes = ComponentRegistry.getRegistry().getActiveCatalogue().getSelectedNodes();
        final Iterator it = selectedNodes.iterator();
        final ArrayList mos = new ArrayList();
        try {
            while (it.hasNext()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("selected node");
                }
                final DefaultMetaTreeNode node = (DefaultMetaTreeNode)it.next();
                if (node instanceof ObjectTreeNode) {
                    final MetaObject mo = ((ObjectTreeNode)node).getMetaObject();
                    if (mo.getClassKey().equalsIgnoreCase(akukClassKey)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Metaobject hinzugefuegt");
                        }
                        mos.add(mo);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug((new StringBuilder()).append("falscher ClassKey:").append(mo.getClassKey())
                                        .append("=?=").append(akukClassKey).toString());
                        }
                    }
                } else {
                    LOG.warn((new StringBuilder()).append("Node ist kein ObjectTreeNode, sondern:").append(
                            node.getClass()).toString());
                }
            }
            if (mos.size() > 0) {
                String outString = "";
                for (final Iterator i$ = mos.iterator(); i$.hasNext();) {
                    final MetaObject mo = (MetaObject)i$.next();
                    outString = (new StringBuilder()).append(outString).append(mo.getID()).append("\r\n").toString();
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug((new StringBuilder()).append("outstring").append(outString).toString());
                }
                try {
                    final String dir = new StringBuilder().append(exchangeDirectory).toString();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Anlegen von:" + dir);
                    }
                    final File dirFile = new File(dir);
                    dirFile.mkdirs();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("existiert=" + dirFile.exists());
                    }
                    final String outS = new StringBuilder().append(exchangeDirectory)
                                .append(fs)
                                .append(exchangeFile)
                                .toString();
                    final File outFile = new File(outS);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("existiert " + outS + " schon? =" + outFile.exists());
                    }
                    final BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
                    out.write(outString);
                    out.close();
                } catch (Throwable e) {
                    LOG.error("Fehler beim Schreiben des WF-AKUK Exchange Files", e);
                }
                try {
                    final ProcessBuilder pb = new ProcessBuilder(triggerExe);
                    pb.directory(new File(new StringBuilder().append(exchangeDirectory).toString()));
                    pb.start();
                } catch (Throwable t) {
                    LOG.error("Fehler beim Aufruf der WF-AKUK triggerExe", t);
                }
            }
        } catch (Throwable t) {
            LOG.fatal("Unerwarteter Fehler im WF-AKUK-Plugin", t);
        }
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext cc) {
        this.connectionContext = cc;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return this.connectionContext;
    }
}
