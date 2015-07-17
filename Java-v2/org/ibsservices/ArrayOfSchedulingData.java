
package org.ibsservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSchedulingData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSchedulingData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SchedulingData" type="{http://ibsservices.org/}SchedulingData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSchedulingData", propOrder = {
    "schedulingData"
})
public class ArrayOfSchedulingData {

    @XmlElement(name = "SchedulingData", nillable = true)
    protected List<SchedulingData> schedulingData;

    /**
     * Gets the value of the schedulingData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the schedulingData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSchedulingData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SchedulingData }
     * 
     * 
     */
    public List<SchedulingData> getSchedulingData() {
        if (schedulingData == null) {
            schedulingData = new ArrayList<SchedulingData>();
        }
        return this.schedulingData;
    }

}
