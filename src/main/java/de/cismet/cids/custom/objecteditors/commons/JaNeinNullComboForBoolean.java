/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.commons;

import de.cismet.cids.editors.Bindable;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.beansbinding.Validator;


/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class JaNeinNullComboForBoolean extends JaNeinNullCombo implements Bindable {
    transient Converter<Boolean, String> c = new Converter<Boolean, String>() {

            @Override public String convertForward(Boolean b) {

                if ((b == null) || (b == false)) {
                    return "Nein";
                }
                else {
                    return "Ja";
                }
            }

            @Override public Boolean convertReverse(String s) {

                if ((s != null) && s.equals("Ja")) {
                    return true;
                }
                else {
                    return false;
                }
            }
        };

    @Override public String getBindingProperty() {
        return "selectedItem";
    }

    @Override public Converter getConverter() {
        return c;
    }

    @Override public Validator getValidator() {
        return null;
    }

    @Override public Object getErrorSourceValue() {
        return Boolean.FALSE;
    }

    @Override public Object getNullSourceValue() {
        return Boolean.FALSE;
    }

}
