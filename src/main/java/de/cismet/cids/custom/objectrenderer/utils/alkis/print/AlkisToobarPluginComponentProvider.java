/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 srichter
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.objectrenderer.utils.alkis.print;

import Sirius.navigator.ui.ComponentRegistry;

import java.awt.Dimension;

import java.util.Collections;
import java.util.List;

import javax.swing.JButton;

import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;

import de.cismet.cids.server.connectioncontext.ClientConnectionContext;
import de.cismet.cids.server.connectioncontext.ClientConnectionContextStore;

import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * ToolbarComponentsProvider that delivers a Button for Alkis Print to integrate into the Navigator Toolbar.
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ToolbarComponentsProvider.class)
public class AlkisToobarPluginComponentProvider implements ToolbarComponentsProvider, ClientConnectionContextStore {

    //~ Instance fields --------------------------------------------------------

    private final List<ToolbarComponentDescription> toolbarComponents;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private ClientConnectionContext connectionContext = ClientConnectionContext.create(getClass().getSimpleName());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisToobarPluginComponentProvider object.
     */
    public AlkisToobarPluginComponentProvider() {
        final List<ToolbarComponentDescription> preparationList = TypeSafeCollections.newArrayList();
        final ToolbarComponentDescription description = new ToolbarComponentDescription(
                "tlbMain",
                new AlkisPrintJButton(connectionContext),
                ToolbarPositionHint.AFTER,
                "cmdPrint");
        preparationList.add(description);
        this.toolbarComponents = Collections.unmodifiableList(preparationList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initAfterConnectionContext() {
    }

    @Override
    public List<ToolbarComponentDescription> getToolbarComponents() {
        if (AlkisUtils.validateUserHasAlkisPrintAccess(getConnectionContext())) {
            return toolbarComponents;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getPluginName() {
        return "ALKIS";
    }

    @Override
    public final ClientConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public void setConnectionContext(final ClientConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }
}

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
final class AlkisPrintJButton extends JButton {

    //~ Static fields/initializers ---------------------------------------------

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            AlkisToobarPluginComponentProvider.class);

    //~ Instance fields --------------------------------------------------------

    private final AlkisPrintingSettingsWidget printWidget;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisPrintJButton object.
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    public AlkisPrintJButton(final ClientConnectionContext connectionContext) {
        try {
            this.printWidget = new AlkisPrintingSettingsWidget(
                    false,
                    CismapBroker.getInstance().getMappingComponent(),
                    connectionContext);
//            printWidget.setLocationRelativeTo(CismapBroker.getInstance().getMappingComponent());
            // printWidget.setLocationRelativeTo(CismapBroker.getInstance().getMappingComponent());
            printWidget.setLocationRelativeTo(ComponentRegistry.getRegistry().getCatalogueTree());
        } catch (Exception ex) {
            log.fatal(ex, ex);
            throw new RuntimeException(ex);
        }
//        setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/frameprint.png"))); // NOI18N
        setText(null);
        setToolTipText("ALKIS Drucken");
        setName("alkis_print");
        setBorderPainted(false);
        setFocusable(false);
        setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/alkisframeprint.png")));
        setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    final Dimension pos = AlkisPrintingSettingsWidget.getPreferredPositionOnScreen();
                    printWidget.pack();
                    final int x = pos.width;
                    final int y = pos.height;
                    if ((x == -1) || (y == -1)) {
                        StaticSwingTools.showDialog(printWidget);
                    } else {
                        printWidget.setLocation(x, y);
                        printWidget.setVisible(true);
                    }
                }
            });
    }
}
