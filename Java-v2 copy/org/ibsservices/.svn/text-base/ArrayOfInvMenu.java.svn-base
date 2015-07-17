
package org.ibsservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfInvMenu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfInvMenu">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InvMenu" type="{http://ibsservices.org/}InvMenu" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfInvMenu", propOrder = {
    "invMenu"
})
public class ArrayOfInvMenu {

    @XmlElement(name = "InvMenu", nillable = true)
    protected List<InvMenu> invMenu;

    /**
     * Gets the value of the invMenu property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the invMenu property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInvMenu().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InvMenu }
     * 
     * 
     */
    public List<InvMenu> getInvMenu() {
        if (invMenu == null) {
            invMenu = new ArrayList<InvMenu>();
        }
        return this.invMenu;
    }

}
