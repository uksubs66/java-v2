
package com.ngn.services._2007._03._20.coursedata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HoleInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HoleInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Par" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Yardage" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Handicap" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HoleInfo", propOrder = {
    "number",
    "par",
    "yardage",
    "handicap"
})
public class HoleInfo {

    @XmlElement(name = "Number")
    protected short number;
    @XmlElement(name = "Par")
    protected short par;
    @XmlElement(name = "Yardage")
    protected short yardage;
    @XmlElement(name = "Handicap")
    protected short handicap;

    /**
     * Gets the value of the number property.
     * 
     */
    public short getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     */
    public void setNumber(short value) {
        this.number = value;
    }

    /**
     * Gets the value of the par property.
     * 
     */
    public short getPar() {
        return par;
    }

    /**
     * Sets the value of the par property.
     * 
     */
    public void setPar(short value) {
        this.par = value;
    }

    /**
     * Gets the value of the yardage property.
     * 
     */
    public short getYardage() {
        return yardage;
    }

    /**
     * Sets the value of the yardage property.
     * 
     */
    public void setYardage(short value) {
        this.yardage = value;
    }

    /**
     * Gets the value of the handicap property.
     * 
     */
    public short getHandicap() {
        return handicap;
    }

    /**
     * Sets the value of the handicap property.
     * 
     */
    public void setHandicap(short value) {
        this.handicap = value;
    }

}
