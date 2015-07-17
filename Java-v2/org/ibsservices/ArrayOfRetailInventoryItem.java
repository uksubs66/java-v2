
package org.ibsservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfRetailInventoryItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRetailInventoryItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RetailInventoryItem" type="{http://ibsservices.org/}RetailInventoryItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRetailInventoryItem", propOrder = {
    "retailInventoryItem"
})
public class ArrayOfRetailInventoryItem {

    @XmlElement(name = "RetailInventoryItem", nillable = true)
    protected List<RetailInventoryItem> retailInventoryItem;

    /**
     * Gets the value of the retailInventoryItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the retailInventoryItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRetailInventoryItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RetailInventoryItem }
     * 
     * 
     */
    public List<RetailInventoryItem> getRetailInventoryItem() {
        if (retailInventoryItem == null) {
            retailInventoryItem = new ArrayList<RetailInventoryItem>();
        }
        return this.retailInventoryItem;
    }

}
