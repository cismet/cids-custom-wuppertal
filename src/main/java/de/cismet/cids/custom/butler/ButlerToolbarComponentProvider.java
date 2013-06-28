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
package de.cismet.cids.custom.butler;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;

import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ToolbarComponentsProvider.class)
public class ButlerToolbarComponentProvider implements ToolbarComponentsProvider {

    //~ Instance fields --------------------------------------------------------

    private final List<ToolbarComponentDescription> toolbarComponents;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ButlerToolbarComponentProvider object.
     */
    public ButlerToolbarComponentProvider() {
        final List<ToolbarComponentDescription> preparationList = new LinkedList<ToolbarComponentDescription>();
        final ToolbarComponentDescription description = new ToolbarComponentDescription(
                "tlbMain",
                new ButlerPrintButton(),
                ToolbarPositionHint.AFTER,
                "cmdPrint");
        preparationList.add(description);
        this.toolbarComponents = Collections.unmodifiableList(preparationList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getPluginName() {
        return "BUTLER";
    }

    @Override
    public Collection<ToolbarComponentDescription> getToolbarComponents() {
        return toolbarComponents;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class ButlerPrintButton extends JButton {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ButlerPrintButton object.
         */
        public ButlerPrintButton() {
            final Butler1Dialog butlerDialog = new Butler1Dialog(StaticSwingTools.getParentFrame(
                        CismapBroker.getInstance().getMappingComponent()),
                    true);
            setText("Butler");
            setToolTipText("Butler Auftrag");
            setName("butler_request");
            setBorderPainted(false);
            setFocusable(false);
//            setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/alkisframeprint.png")));
            setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        StaticSwingTools.showDialog(butlerDialog);
                    }
                });
        }
    }
}
