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

import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.tools.collections.TypeSafeCollections;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;

/**
 *
 * @author srichter
 */
@org.openide.util.lookup.ServiceProvider(service = ToolbarComponentsProvider.class)
public class AlkisToobarPluginComponentProvider implements ToolbarComponentsProvider {

    private final List<ToolbarComponentDescription> toolbarComponents;

    public AlkisToobarPluginComponentProvider() {
        final List<ToolbarComponentDescription> preparationList = TypeSafeCollections.newArrayList();
        final ToolbarComponentDescription description = new ToolbarComponentDescription("tlbMain", new AlkisPrintJButton(), ToolbarPositionHint.AFTER, "print");
        preparationList.add(description);
        this.toolbarComponents = Collections.unmodifiableList(preparationList);
    }

    @Override
    public List<ToolbarComponentDescription> getToolbarComponents() {
        return toolbarComponents;
    }

    @Override
    public String getPluginName() {
        return "ALKIS";
    }
}

final class AlkisPrintJButton extends JButton {

    private final AlkisPrintingSettingsWidget printWidget;

    public AlkisPrintJButton() {
        this.printWidget = new AlkisPrintingSettingsWidget(false, CismapBroker.getInstance().getMappingComponent());
//        setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/frameprint.png"))); // NOI18N
        setText("Alkis-Drucken");
        setToolTipText("Alkis Drucken");
        setName("alkis_print");
        setBorderPainted(false);
        setFocusable(false);
        setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printWidget.setVisible(true);
            }
        });
    }
}

