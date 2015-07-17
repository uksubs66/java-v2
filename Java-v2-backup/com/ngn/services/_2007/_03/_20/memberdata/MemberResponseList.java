
package com.ngn.services._2007._03._20.memberdata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MemberResponseList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MemberResponseList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MemberResponse" type="{http://services.ngn.com/2007/03/20/MemberData}MemberResponseInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MemberResponseList", propOrder = {
    "memberResponse"
})
public class MemberResponseList {

    @XmlElement(name = "MemberResponse")
    protected List<MemberResponseInfo> memberResponse;

    /**
     * Gets the value of the memberResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the memberResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMemberResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MemberResponseInfo }
     * 
     * 
     */
    public List<MemberResponseInfo> getMemberResponse() {
        if (memberResponse == null) {
            memberResponse = new ArrayList<MemberResponseInfo>();
        }
        return this.memberResponse;
    }

}
