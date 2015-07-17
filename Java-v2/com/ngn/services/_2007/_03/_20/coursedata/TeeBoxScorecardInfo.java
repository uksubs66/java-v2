
package com.ngn.services._2007._03._20.coursedata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TeeBoxScorecardInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TeeBoxScorecardInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ScorecardType" type="{http://services.ngn.com/2007/03/20/CourseData}TeeBoxScorecardType"/>
 *         &lt;element name="TeeboxScorecardId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Front9Slope" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Back9Slope" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="TotalSlope" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Front9Rating" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Back9Rating" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="TotalRating" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Front9Par" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Back9Par" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="TotalPar" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Front9Yardage" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Back9Yardage" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="TotalYardage" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="IsActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Holes" type="{http://services.ngn.com/2007/03/20/CourseData}ArrayOfHoleInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TeeBoxScorecardInfo", propOrder = {
    "scorecardType",
    "teeboxScorecardId",
    "front9Slope",
    "back9Slope",
    "totalSlope",
    "front9Rating",
    "back9Rating",
    "totalRating",
    "front9Par",
    "back9Par",
    "totalPar",
    "front9Yardage",
    "back9Yardage",
    "totalYardage",
    "isActive",
    "holes"
})
public class TeeBoxScorecardInfo {

    @XmlElement(name = "ScorecardType", required = true)
    protected TeeBoxScorecardType scorecardType;
    @XmlElement(name = "TeeboxScorecardId")
    protected int teeboxScorecardId;
    @XmlElement(name = "Front9Slope")
    protected short front9Slope;
    @XmlElement(name = "Back9Slope")
    protected short back9Slope;
    @XmlElement(name = "TotalSlope")
    protected short totalSlope;
    @XmlElement(name = "Front9Rating")
    protected short front9Rating;
    @XmlElement(name = "Back9Rating")
    protected short back9Rating;
    @XmlElement(name = "TotalRating")
    protected short totalRating;
    @XmlElement(name = "Front9Par")
    protected short front9Par;
    @XmlElement(name = "Back9Par")
    protected short back9Par;
    @XmlElement(name = "TotalPar")
    protected short totalPar;
    @XmlElement(name = "Front9Yardage")
    protected short front9Yardage;
    @XmlElement(name = "Back9Yardage")
    protected short back9Yardage;
    @XmlElement(name = "TotalYardage")
    protected short totalYardage;
    @XmlElement(name = "IsActive")
    protected boolean isActive;
    @XmlElement(name = "Holes")
    protected ArrayOfHoleInfo holes;

    /**
     * Gets the value of the scorecardType property.
     * 
     * @return
     *     possible object is
     *     {@link TeeBoxScorecardType }
     *     
     */
    public TeeBoxScorecardType getScorecardType() {
        return scorecardType;
    }

    /**
     * Sets the value of the scorecardType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TeeBoxScorecardType }
     *     
     */
    public void setScorecardType(TeeBoxScorecardType value) {
        this.scorecardType = value;
    }

    /**
     * Gets the value of the teeboxScorecardId property.
     * 
     */
    public int getTeeboxScorecardId() {
        return teeboxScorecardId;
    }

    /**
     * Sets the value of the teeboxScorecardId property.
     * 
     */
    public void setTeeboxScorecardId(int value) {
        this.teeboxScorecardId = value;
    }

    /**
     * Gets the value of the front9Slope property.
     * 
     */
    public short getFront9Slope() {
        return front9Slope;
    }

    /**
     * Sets the value of the front9Slope property.
     * 
     */
    public void setFront9Slope(short value) {
        this.front9Slope = value;
    }

    /**
     * Gets the value of the back9Slope property.
     * 
     */
    public short getBack9Slope() {
        return back9Slope;
    }

    /**
     * Sets the value of the back9Slope property.
     * 
     */
    public void setBack9Slope(short value) {
        this.back9Slope = value;
    }

    /**
     * Gets the value of the totalSlope property.
     * 
     */
    public short getTotalSlope() {
        return totalSlope;
    }

    /**
     * Sets the value of the totalSlope property.
     * 
     */
    public void setTotalSlope(short value) {
        this.totalSlope = value;
    }

    /**
     * Gets the value of the front9Rating property.
     * 
     */
    public short getFront9Rating() {
        return front9Rating;
    }

    /**
     * Sets the value of the front9Rating property.
     * 
     */
    public void setFront9Rating(short value) {
        this.front9Rating = value;
    }

    /**
     * Gets the value of the back9Rating property.
     * 
     */
    public short getBack9Rating() {
        return back9Rating;
    }

    /**
     * Sets the value of the back9Rating property.
     * 
     */
    public void setBack9Rating(short value) {
        this.back9Rating = value;
    }

    /**
     * Gets the value of the totalRating property.
     * 
     */
    public short getTotalRating() {
        return totalRating;
    }

    /**
     * Sets the value of the totalRating property.
     * 
     */
    public void setTotalRating(short value) {
        this.totalRating = value;
    }

    /**
     * Gets the value of the front9Par property.
     * 
     */
    public short getFront9Par() {
        return front9Par;
    }

    /**
     * Sets the value of the front9Par property.
     * 
     */
    public void setFront9Par(short value) {
        this.front9Par = value;
    }

    /**
     * Gets the value of the back9Par property.
     * 
     */
    public short getBack9Par() {
        return back9Par;
    }

    /**
     * Sets the value of the back9Par property.
     * 
     */
    public void setBack9Par(short value) {
        this.back9Par = value;
    }

    /**
     * Gets the value of the totalPar property.
     * 
     */
    public short getTotalPar() {
        return totalPar;
    }

    /**
     * Sets the value of the totalPar property.
     * 
     */
    public void setTotalPar(short value) {
        this.totalPar = value;
    }

    /**
     * Gets the value of the front9Yardage property.
     * 
     */
    public short getFront9Yardage() {
        return front9Yardage;
    }

    /**
     * Sets the value of the front9Yardage property.
     * 
     */
    public void setFront9Yardage(short value) {
        this.front9Yardage = value;
    }

    /**
     * Gets the value of the back9Yardage property.
     * 
     */
    public short getBack9Yardage() {
        return back9Yardage;
    }

    /**
     * Sets the value of the back9Yardage property.
     * 
     */
    public void setBack9Yardage(short value) {
        this.back9Yardage = value;
    }

    /**
     * Gets the value of the totalYardage property.
     * 
     */
    public short getTotalYardage() {
        return totalYardage;
    }

    /**
     * Sets the value of the totalYardage property.
     * 
     */
    public void setTotalYardage(short value) {
        this.totalYardage = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     */
    public boolean isIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     */
    public void setIsActive(boolean value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the holes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHoleInfo }
     *     
     */
    public ArrayOfHoleInfo getHoles() {
        return holes;
    }

    /**
     * Sets the value of the holes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHoleInfo }
     *     
     */
    public void setHoles(ArrayOfHoleInfo value) {
        this.holes = value;
    }

}
