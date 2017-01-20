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

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Template {

    //~ Instance fields --------------------------------------------------------

    String name;
    String subject;
    String body;
    String to;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  DOCUMENT ME!
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSubject() {
        return subject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  subject  DOCUMENT ME!
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBody() {
        return body;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  body  DOCUMENT ME!
     */
    public void setBody(final String body) {
        this.body = body;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTo() {
        return to;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  to  DOCUMENT ME!
     */
    public void setTo(final String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return name;
    }
}
