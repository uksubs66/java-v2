
package com.ngn.services._2007._03._20.handicapdata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HandicapList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HandicapList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Handicap" type="{http://services.ngn.com/2007/03/20/HandicapData}HandicapInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HandicapList", propOrder = {
    "handicap"
})
public class HandicapList {

    @XmlElement(name = "Handicap")
    protected List<HandicapInfo> handicap;

    /**
     * Gets the value of the handicap property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the handicap property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHandicap().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HandicapInfo }
     * 
     * 
     */
    public List<HandicapInfo> getHandicap() {
        if (handicap == null) {
            handicap = new ArrayList<HandicapInfo>();
        }
        return this.handicap;
    }

}
