
package com.ngn.services._2007._03._20.coursedata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClubCourses complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClubCourses">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CourseName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CourseId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ClubCourseType" type="{http://services.ngn.com/2007/03/20/CourseData}ClubCourseTypeId"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClubCourses", propOrder = {
    "courseName",
    "courseId",
    "clubCourseType"
})
public class ClubCourses {

    @XmlElement(name = "CourseName")
    protected String courseName;
    @XmlElement(name = "CourseId")
    protected int courseId;
    @XmlElement(name = "ClubCourseType", required = true)
    protected ClubCourseTypeId clubCourseType;

    /**
     * Gets the value of the courseName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Sets the value of the courseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourseName(String value) {
        this.courseName = value;
    }

    /**
     * Gets the value of the courseId property.
     * 
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * Sets the value of the courseId property.
     * 
     */
    public void setCourseId(int value) {
        this.courseId = value;
    }

    /**
     * Gets the value of the clubCourseType property.
     * 
     * @return
     *     possible object is
     *     {@link ClubCourseTypeId }
     *     
     */
    public ClubCourseTypeId getClubCourseType() {
        return clubCourseType;
    }

    /**
     * Sets the value of the clubCourseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClubCourseTypeId }
     *     
     */
    public void setClubCourseType(ClubCourseTypeId value) {
        this.clubCourseType = value;
    }

}
