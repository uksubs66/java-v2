
package org.ibsservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfTenderData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTenderData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TenderData" type="{http://ibsservices.org/}TenderData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTenderData", propOrder = {
    "tenderData"
})
public class ArrayOfTenderData {

    @XmlElement(name = "TenderData", nillable = true)
    protected List<TenderData> tenderData;

    /**
     * Gets the value of the tenderData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tenderData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTenderData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TenderData }
     * 
     * 
     */
    public List<TenderData> getTenderData() {
        if (tenderData == null) {
            tenderData = new ArrayList<TenderData>();
        }
        return this.tenderData;
    }

}
