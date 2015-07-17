
package com.ngn.services._2007._03._20.handicapdata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HandicapUpdateList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HandicapUpdateList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Handicap" type="{http://services.ngn.com/2007/03/20/HandicapData}HandicapUpdateInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HandicapUpdateList", propOrder = {
    "handicap"
})
public class HandicapUpdateList {

    @XmlElement(name = "Handicap")
    protected List<HandicapUpdateInfo> handicap;

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
     * {@link HandicapUpdateInfo }
     * 
     * 
     */
    public List<HandicapUpdateInfo> getHandicap() {
        if (handicap == null) {
            handicap = new ArrayList<HandicapUpdateInfo>();
        }
        return this.handicap;
    }

}
