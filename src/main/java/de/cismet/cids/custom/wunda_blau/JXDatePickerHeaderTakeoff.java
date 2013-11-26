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
package de.cismet.cids.custom.wunda_blau;

import org.jdesktop.swingx.plaf.basic.CalendarHeaderHandler;
import org.jdesktop.swingx.plaf.basic.SpinningCalendarHeaderHandler;

import org.openide.util.lookup.ServiceProvider;

import javax.swing.UIManager;

import de.cismet.cids.custom.objectrenderer.utils.billing.TimeFilterPanel;

import de.cismet.tools.configuration.TakeoffHook;

/**
 * This Takeoff is the first step in changing the header of a JXDatePicker, actually of its JXMonthView. The second step
 * is to make the monthview of a JXDatePicker instance zoomable (datePicker.getMonthView().setZoomable(true)). The new
 * header SpinningCalendarHeaderHandler will, beside of the normal header, also have a spinner to change the year.<br/>
 * Further information can be found in http://stackoverflow.com/questions/16111943/java-swing-jxdatepicker and in
 * Gilles' post http://stackoverflow.com/questions/20123886/jxdatepicker-change-year<br/>
 * <b>Note: This should be considered as a hack, but no other solution is possible at the moment.</b>
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 * @see      TimeFilterPanel
 */
@ServiceProvider(service = TakeoffHook.class)
public class JXDatePickerHeaderTakeoff implements TakeoffHook {

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationTakeoff() {
        UIManager.put(
            CalendarHeaderHandler.uiControllerID,
            "org.jdesktop.swingx.plaf.basic.SpinningCalendarHeaderHandler");
        UIManager.put(
            SpinningCalendarHeaderHandler.ARROWS_SURROUND_MONTH,
            Boolean.TRUE);
    }
}
