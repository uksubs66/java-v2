
package com.ngn.services._2007._03._20.memberdata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MemberIdList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MemberIdList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MemberId" type="{http://services.ngn.com/2007/03/20/MemberData}MemberIdInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MemberIdList", propOrder = {
    "memberId"
})
public class MemberIdList {

    @XmlElement(name = "MemberId")
    protected List<MemberIdInfo> memberId;

    /**
     * Gets the value of the memberId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the memberId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMemberId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MemberIdInfo }
     * 
     * 
     */
    public List<MemberIdInfo> getMemberId() {
        if (memberId == null) {
            memberId = new ArrayList<MemberIdInfo>();
        }
        return this.memberId;
    }

}
