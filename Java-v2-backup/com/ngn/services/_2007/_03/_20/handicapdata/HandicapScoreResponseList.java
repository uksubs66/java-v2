
package com.ngn.services._2007._03._20.handicapdata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HandicapScoreResponseList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HandicapScoreResponseList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HandicapResponse" type="{http://services.ngn.com/2007/03/20/HandicapData}HandicapScoreResponseInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HandicapScoreResponseList", propOrder = {
    "handicapResponse"
})
public class HandicapScoreResponseList {

    @XmlElement(name = "HandicapResponse")
    protected List<HandicapScoreResponseInfo> handicapResponse;

    /**
     * Gets the value of the handicapResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the handicapResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHandicapResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HandicapScoreResponseInfo }
     * 
     * 
     */
    public List<HandicapScoreResponseInfo> getHandicapResponse() {
        if (handicapResponse == null) {
            handicapResponse = new ArrayList<HandicapScoreResponseInfo>();
        }
        return this.handicapResponse;
    }

}
