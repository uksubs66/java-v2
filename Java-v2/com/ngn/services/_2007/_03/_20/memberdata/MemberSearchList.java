
package com.ngn.services._2007._03._20.memberdata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MemberSearchList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MemberSearchList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MemberSearch" type="{http://services.ngn.com/2007/03/20/MemberData}MemberSearchInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MemberSearchList", propOrder = {
    "memberSearch"
})
public class MemberSearchList {

    @XmlElement(name = "MemberSearch")
    protected List<MemberSearchInfo> memberSearch;

    /**
     * Gets the value of the memberSearch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the memberSearch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMemberSearch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MemberSearchInfo }
     * 
     * 
     */
    public List<MemberSearchInfo> getMemberSearch() {
        if (memberSearch == null) {
            memberSearch = new ArrayList<MemberSearchInfo>();
        }
        return this.memberSearch;
    }

}
