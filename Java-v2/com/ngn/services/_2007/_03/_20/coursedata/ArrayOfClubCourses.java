
package com.ngn.services._2007._03._20.coursedata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfClubCourses complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfClubCourses">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClubCourses" type="{http://services.ngn.com/2007/03/20/CourseData}ClubCourses" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfClubCourses", propOrder = {
    "clubCourses"
})
public class ArrayOfClubCourses {

    @XmlElement(name = "ClubCourses", nillable = true)
    protected List<ClubCourses> clubCourses;

    /**
     * Gets the value of the clubCourses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clubCourses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClubCourses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClubCourses }
     * 
     * 
     */
    public List<ClubCourses> getClubCourses() {
        if (clubCourses == null) {
            clubCourses = new ArrayList<ClubCourses>();
        }
        return this.clubCourses;
    }

}
