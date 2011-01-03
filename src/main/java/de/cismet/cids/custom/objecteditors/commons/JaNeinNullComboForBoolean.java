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

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.beansbinding.Validator;

import de.cismet.cids.editors.Bindable;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class JaNeinNullComboForBoolean extends JaNeinNullCombo implements Bindable {

    //~ Instance fields --------------------------------------------------------

    transient Converter<Boolean, String> c = new Converter<Boolean, String>() {

            @Override
            public String convertForward(final Boolean b) {
                if ((b == null) || (b == false)) {
                    return "Nein";
                } else {
                    return "Ja";
                }
            }

            @Override
            public Boolean convertReverse(final String s) {
                if ((s != null) && s.equals("Ja")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getBindingProperty() {
        return "selectedItem";
    }

    @Override
    public Converter getConverter() {
        return c;
    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public Object getErrorSourceValue() {
        return Boolean.FALSE;
    }

    @Override
    public Object getNullSourceValue() {
        return Boolean.FALSE;
    }
}
