
package org.ibsservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfMinimumCodeData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfMinimumCodeData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MinimumCodeData" type="{http://ibsservices.org/}MinimumCodeData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfMinimumCodeData", propOrder = {
    "minimumCodeData"
})
public class ArrayOfMinimumCodeData {

    @XmlElement(name = "MinimumCodeData", nillable = true)
    protected List<MinimumCodeData> minimumCodeData;

    /**
     * Gets the value of the minimumCodeData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the minimumCodeData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMinimumCodeData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MinimumCodeData }
     * 
     * 
     */
    public List<MinimumCodeData> getMinimumCodeData() {
        if (minimumCodeData == null) {
            minimumCodeData = new ArrayList<MinimumCodeData>();
        }
        return this.minimumCodeData;
    }

}
