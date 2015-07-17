
package com.ngn.services._2007._03._20.coursedata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClubList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClubList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Club" type="{http://services.ngn.com/2007/03/20/CourseData}ClubServiceInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClubList", propOrder = {
    "club"
})
public class ClubList {

    @XmlElement(name = "Club")
    protected List<ClubServiceInfo> club;

    /**
     * Gets the value of the club property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the club property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClub().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClubServiceInfo }
     * 
     * 
     */
    public List<ClubServiceInfo> getClub() {
        if (club == null) {
            club = new ArrayList<ClubServiceInfo>();
        }
        return this.club;
    }

}
