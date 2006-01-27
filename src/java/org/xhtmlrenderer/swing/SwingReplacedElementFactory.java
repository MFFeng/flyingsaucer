/*
 * {{{ header & license
 * Copyright (c) 2006 Wisconsin Court System
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package org.xhtmlrenderer.swing;

import java.awt.Image;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.simple.extend.XhtmlForm;

public class SwingReplacedElementFactory implements ReplacedElementFactory {
    protected HashMap imageComponents;
    protected LinkedHashMap forms;
    
    public ReplacedElement createReplacedElement(
            Element e, UserAgentCallback uac, int setWidth, int setHeight) {
        JComponent cc = null;
        if (e == null) {
            return null;
        }
        if (e.getNodeName().equals("img")) {
            cc = getImageComponent(e);
            if (cc != null) {
                return new SwingReplacedElement(cc);
            }
            JButton jb = null;
            Image im = null;
            im = uac.getImageResource(e.getAttribute("src")).getImage();
            if (im == null) {
                jb = new JButton("Image unreachable. " + e.getAttribute("alt"));
            } else {
                Image i2 = im.getScaledInstance(setWidth, setHeight, Image.SCALE_FAST);
                ImageIcon ii = new ImageIcon(i2, e.getAttribute("alt"));
                jb = new JButton(ii);
            }
            jb.setBorder(BorderFactory.createEmptyBorder());
            jb.setSize(jb.getPreferredSize());
            addImageComponent(e, jb);
            return new SwingReplacedElement(jb);
        }
        //form components
        Element parentForm = getParentForm(e);
        //parentForm may be null! No problem! Assume action is this document and method is get.
        XhtmlForm form = getForm(parentForm);
        if (form == null) {
            form = new XhtmlForm(uac, parentForm);
            addForm(parentForm, form);
        }
        cc = form.addComponent(e);
        return cc == null ? null : new SwingReplacedElement(cc);
    }
    
    protected void addImageComponent(Element e, JComponent cc) {
        if (imageComponents == null) {
            imageComponents = new HashMap();
        }
        imageComponents.put(e, cc);
    }

    protected void addForm(Element e, XhtmlForm f) {
        if (forms == null) {
            forms = new LinkedHashMap();
        }
        forms.put(e, f);
    }

    protected JComponent getImageComponent(Element e) {
        if (imageComponents == null) {
            return null;
        }
        return (JComponent) imageComponents.get(e);
    }

    protected XhtmlForm getForm(Element e) {
        if (forms == null) {
            return null;
        }
        return (XhtmlForm) forms.get(e);
    }

    protected Element getParentForm(Element e) {
        Node n = e;
        do {
            n = n.getParentNode();
        } while (n.getNodeType() == Node.ELEMENT_NODE && !n.getNodeName().equals("form"));
        if (n.getNodeType() != Node.ELEMENT_NODE) {
            return null;
        }
        return (Element) n;
    }    
}
