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
package de.cismet.cids.custom.mail;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class TemplateInfo {

    //~ Instance fields --------------------------------------------------------

    ArrayList<Template> templates = new ArrayList<Template>();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<Template> getTemplates() {
        return templates;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  templates  DOCUMENT ME!
     */
    public void setTemplates(final ArrayList<Template> templates) {
        this.templates = templates;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static void main(final String[] args) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        final TemplateInfo tester = mapper.readValue(TemplateInfo.class.getResourceAsStream(
                    "/de/cismet/cids/custom/billing/email_templates.json"),
                TemplateInfo.class);
        System.out.println(tester.getTemplates().get(0).name);
    }
}
