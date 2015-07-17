
package com.ngn.services._2007._03._20.memberdata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TournamentMemberList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TournamentMemberList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Member" type="{http://services.ngn.com/2007/03/20/MemberData}TournamentMemberInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TournamentMemberList", propOrder = {
    "member"
})
public class TournamentMemberList {

    @XmlElement(name = "Member")
    protected List<TournamentMemberInfo> member;

    /**
     * Gets the value of the member property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the member property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMember().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TournamentMemberInfo }
     * 
     * 
     */
    public List<TournamentMemberInfo> getMember() {
        if (member == null) {
            member = new ArrayList<TournamentMemberInfo>();
        }
        return this.member;
    }

}
