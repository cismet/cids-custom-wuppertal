/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.commons.gui;

import Sirius.navigator.tools.MetaObjectCache;

import Sirius.server.localserver.attribute.MemberAttributeInfo;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaClassStore;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.beansbinding.Validator;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.client.tools.ConnectionContextUtils;
import de.cismet.cids.custom.objecteditors.wunda_blau.WrapLayout;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DefaultBindableLabelsField extends JPanel implements Bindable,
    MetaClassStore,
    ActionListener,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(DefaultBindableLabelsField.class);

    //~ Instance fields --------------------------------------------------------

    private PropertyChangeSupport propertyChangeSupport;
    private List selectedElements = null;
    private MetaClass metaClass = null;
    private final Map<JToggleButton, MetaObject> toggleToObjectMapping = new HashMap<>();
    private volatile boolean threadRunning = false;
    private final Comparator<MetaObject> comparator;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DefaultBindableLabelsField object.
     */
    public DefaultBindableLabelsField() {
        this(null, true);
    }

    /**
     * Creates a new DefaultBindableLabelsField object.
     *
     * @param  enabled  DOCUMENT ME!
     */
    public DefaultBindableLabelsField(final boolean enabled) {
        this(null, enabled);
    }

    /**
     * Creates a new DefaultBindableLabelsField object.
     *
     * @param  comparator  DOCUMENT ME!
     * @param  enabled     DOCUMENT ME!
     */
    public DefaultBindableLabelsField(final Comparator<MetaObject> comparator, final boolean enabled) {
        this.comparator = comparator;
        super.setEnabled(enabled);
        setLayout(new WrapLayout(WrapLayout.LEFT));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public PropertyChangeSupport getPropertyChangeSupport() {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        return propertyChangeSupport;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        if (listener != null) {
            getPropertyChangeSupport().addPropertyChangeListener(listener);
        }
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        getPropertyChangeSupport().removePropertyChangeListener(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  elements  DOCUMENT ME!
     */
    public void setSelectedElements(final Object elements) {
        if (elements instanceof List) {
            this.selectedElements = (List)elements;
        }
        activateSelectedObjects();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getSelectedElements() {
        return selectedElements;
    }

    @Override
    public String getBindingProperty() {
        return "selectedElements";
    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public Converter getConverter() {
        return null;
    }

    @Override
    public Object getNullSourceValue() {
        return null;
    }

    @Override
    public Object getErrorSourceValue() {
        return null;
    }

    @Override
    public MetaClass getMetaClass() {
        return this.metaClass;
    }

    @Override
    public void setMetaClass(final MetaClass metaClass) {
        toggleToObjectMapping.clear();
        removeAll();

        this.metaClass = metaClass;

        new SwingWorker<MetaObject[], Void>() {

                @Override
                protected MetaObject[] doInBackground() throws Exception {
                    while (!setThreadRunning()) {
                        try {
                            Thread.sleep(20);
                        } catch (final Exception ex) {
                        }
                    }

                    if (metaClass != null) {
                        final MetaClass foreignClass = getReferencedClass(metaClass);
                        final String query = "select " + foreignClass.getID() + ", " + foreignClass.getPrimaryKey()
                                    + " from "
                                    + foreignClass.getTableName();

                        return MetaObjectCache.getInstance()
                                    .getMetaObjectsByQuery(query, metaClass.getDomain(), getConnectionContext());
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        final MetaObject[] metaObjects = get();

                        if (metaObjects != null) {
                            if (comparator != null) {
                                Arrays.sort(metaObjects, comparator);
                            }

                            for (final MetaObject metaObject : metaObjects) {
                                final JToggleButton toggle = new JToggleButton(metaObject.getBean().toString());
                                toggle.addActionListener(DefaultBindableLabelsField.this);
                                toggle.setOpaque(false);
//                                toggle.setContentAreaFilled(false);
                                toggle.setFocusPainted(false);
                                toggle.setBorder(new EmptyBorder(2, 5, 2, 5));
                                toggleToObjectMapping.put(toggle, metaObject);
                                add(toggle);
                            }
                            activateSelectedObjects();
                            setEnabled(isEnabled());
                        }
                    } catch (final Exception e) {
                        LOG.error("Error while filling a togglebutton field.", e); // NOI18N
                    } finally {
                        threadRunning = false;
                    }
                }
            }.execute();
    }
    /**
     * DOCUMENT ME!
     */
    private void activateSelectedObjects() {
        if (selectedElements != null) {
            final Iterator<JToggleButton> it = toggleToObjectMapping.keySet().iterator();

            while (it.hasNext()) {
                final JToggleButton toggle = it.next();
                final MetaObject mo = toggleToObjectMapping.get(toggle);
                if ((mo != null) && selectedElements.contains(mo.getBean())) {
                    toggle.setSelected(true);
                    toggle.setBorder(new RoundedBorder(10, 2, 5, 2, 5));

//
//                    if (backgroundSelected != null) {
//                        tmp.setOpaque(true);
//                        tmp.setContentAreaFilled(true);
//                        tmp.setBackground(backgroundSelected);
//                    }
                } else {
                    toggle.setSelected(false);
                    toggle.setBorder(new EmptyBorder(2, 5, 2, 5));
//
//                    if (backgroundUnselected != null) {
//                        tmp.setOpaque(true);
//                        tmp.setContentAreaFilled(true);
//                        tmp.setBackground(backgroundUnselected);
//                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   metaClass  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MetaClass getReferencedClass(final MetaClass metaClass) {
        MetaClass result = metaClass;
        if (metaClass.isArrayElementLink()) {
            final HashMap hm = metaClass.getMemberAttributeInfos();
            for (final Object tmp : hm.values()) {
                if (tmp instanceof MemberAttributeInfo) {
                    if (((MemberAttributeInfo)tmp).isForeignKey()) {
                        final int classId = ((MemberAttributeInfo)tmp).getForeignKeyClassId();
                        result = ClassCacheMultiple.getMetaClass(metaClass.getDomain(),
                                classId,
                                getConnectionContext());
                    }
                }
            }
        }
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  decider                 DOCUMENT ME!
     * @param  removeSelectedElements  DOCUMENT ME!
     */
    public void refreshCheckboxState(final FieldStateDecider decider, final boolean removeSelectedElements) {
        refreshCheckboxState(decider, false, removeSelectedElements);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  decider                 DOCUMENT ME!
     * @param  hideElements            Elements which are not accepted by the decider should be shown as unenabled, if
     *                                 hideElements is false. Otherwise, the elements should not be shown, if they are
     *                                 not accepted by the decider.
     * @param  removeSelectedElements  DOCUMENT ME!
     */
    public void refreshCheckboxState(final FieldStateDecider decider,
            final boolean hideElements,
            final boolean removeSelectedElements) {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    while (!setThreadRunning()) {
                        try {
                            Thread.sleep(20);
                        } catch (final Exception ex) {
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        final JToggleButton[] toggles = toggleToObjectMapping.keySet().toArray(new JToggleButton[0]);

                        if (comparator != null) {
                            Arrays.sort(toggles, new Comparator<JToggleButton>() {

                                    @Override
                                    public int compare(final JToggleButton o1, final JToggleButton o2) {
                                        return comparator.compare(
                                                toggleToObjectMapping.get(o1),
                                                toggleToObjectMapping.get(o2));
                                    }
                                });
                        }

                        if (removeSelectedElements) {
                            selectedElements.clear();
                        }

                        if (hideElements) {
                            removeAll();
                        }

                        for (final JToggleButton toggle : toggles) {
                            if (!hideElements) {
                                toggle.setVisible(
                                    isEnabled()
                                            || decider.isCheckboxForClassActive(toggleToObjectMapping.get(toggle)));
//                                toggle.setEnabled(
//                                    isEnabled()
//                                            && decider.isCheckboxForClassActive(toggleToObjectMapping.get(toggle)));
                            } else {
                                if (decider.isCheckboxForClassActive(toggleToObjectMapping.get(toggle))) {
                                    add(toggle);
                                }
                            }
                            toggle.setSelected(false);
                            if (Thread.currentThread().isInterrupted()) {
                                return;
                            }
                        }
                        activateSelectedObjects();
                    } finally {
                        threadRunning = false;
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    public void dispose() {
    }

    @Override
    public void actionPerformed(final ActionEvent ae) {
        final JToggleButton toggle = (JToggleButton)ae.getSource();

        final MetaObject mo = toggleToObjectMapping.get(toggle);
        final List old = new ArrayList(selectedElements);
        if (selectedElements.contains(mo.getBean())) {
            selectedElements.remove(mo.getBean());
        } else {
            selectedElements.add(mo.getBean());
        }

//        toggle.setOpaque(false);
//        toggle.setContentAreaFilled(false);

        if (toggle.isSelected()) {
            toggle.setBorder(new RoundedBorder(10, 2, 5, 2, 5));
//            if (backgroundSelected != null) {
//                toggle.setOpaque(true);
//                toggle.setContentAreaFilled(true);
//                toggle.setBackground(backgroundSelected);
//            }
        } else {
            toggle.setBorder(new EmptyBorder(2, 5, 2, 5));
//            if (backgroundUnselected != null) {
//                toggle.setOpaque(true);
//                toggle.setContentAreaFilled(true);
//                toggle.setBackground(backgroundUnselected);
//            }
        }

        propertyChangeSupport.firePropertyChange("selectedElements", old, selectedElements);
        propertyChangeSupport.firePropertyChange("selectedElements", null, mo.getBean());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private synchronized boolean setThreadRunning() {
        if (threadRunning) {
            return false;
        } else {
            threadRunning = true;
            return true;
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        for (final JToggleButton toggle : toggleToObjectMapping.keySet()) {
            toggle.setVisible(isEnabled() || toggle.isEnabled());
            // toggle.setEnabled(isEnabled() && toggle.isEnabled());
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return ConnectionContextUtils.getFirstParentClientConnectionContext(this);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class RoundedBorder implements Border {

        //~ Instance fields ----------------------------------------------------

        private final int radius;
        private final Insets insets;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RoundedBorder object.
         *
         * @param  radius  DOCUMENT ME!
         * @param  insets  DOCUMENT ME!
         */
        public RoundedBorder(final int radius, final Insets insets) {
            this.radius = radius;
            this.insets = insets;
        }

        /**
         * Creates a new RoundedBorder object.
         *
         * @param  radius  DOCUMENT ME!
         * @param  top     DOCUMENT ME!
         * @param  left    DOCUMENT ME!
         * @param  bottom  DOCUMENT ME!
         * @param  right   DOCUMENT ME!
         */
        public RoundedBorder(final int radius, final int top, final int left, final int bottom, final int right) {
            this(radius, new Insets(top, left, bottom, right));
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Insets getBorderInsets(final Component c) {
            return insets;
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(final Component c,
                final Graphics g,
                final int x,
                final int y,
                final int width,
                final int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
