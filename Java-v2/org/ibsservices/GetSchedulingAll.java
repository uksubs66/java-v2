
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="a_sUserID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_sUserPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_bSpaData" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="a_sSpaStartDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_sSpaEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_bTeeTimesData" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="a_sTeeStartDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_sTeeEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_sMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "asUserID",
    "asUserPass",
    "abSpaData",
    "asSpaStartDate",
    "asSpaEndDate",
    "abTeeTimesData",
    "asTeeStartDate",
    "asTeeEndDate",
    "asMessage"
})
@XmlRootElement(name = "GetSchedulingAll")
public class GetSchedulingAll {

    @XmlElement(name = "a_sUserID")
    protected String asUserID;
    @XmlElement(name = "a_sUserPass")
    protected String asUserPass;
    @XmlElement(name = "a_bSpaData")
    protected boolean abSpaData;
    @XmlElement(name = "a_sSpaStartDate")
    protected String asSpaStartDate;
    @XmlElement(name = "a_sSpaEndDate")
    protected String asSpaEndDate;
    @XmlElement(name = "a_bTeeTimesData")
    protected boolean abTeeTimesData;
    @XmlElement(name = "a_sTeeStartDate")
    protected String asTeeStartDate;
    @XmlElement(name = "a_sTeeEndDate")
    protected String asTeeEndDate;
    @XmlElement(name = "a_sMessage")
    protected String asMessage;

    /**
     * Gets the value of the asUserID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASUserID() {
        return asUserID;
    }

    /**
     * Sets the value of the asUserID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASUserID(String value) {
        this.asUserID = value;
    }

    /**
     * Gets the value of the asUserPass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASUserPass() {
        return asUserPass;
    }

    /**
     * Sets the value of the asUserPass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASUserPass(String value) {
        this.asUserPass = value;
    }

    /**
     * Gets the value of the abSpaData property.
     * 
     */
    public boolean isABSpaData() {
        return abSpaData;
    }

    /**
     * Sets the value of the abSpaData property.
     * 
     */
    public void setABSpaData(boolean value) {
        this.abSpaData = value;
    }

    /**
     * Gets the value of the asSpaStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASSpaStartDate() {
        return asSpaStartDate;
    }

    /**
     * Sets the value of the asSpaStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASSpaStartDate(String value) {
        this.asSpaStartDate = value;
    }

    /**
     * Gets the value of the asSpaEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASSpaEndDate() {
        return asSpaEndDate;
    }

    /**
     * Sets the value of the asSpaEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASSpaEndDate(String value) {
        this.asSpaEndDate = value;
    }

    /**
     * Gets the value of the abTeeTimesData property.
     * 
     */
    public boolean isABTeeTimesData() {
        return abTeeTimesData;
    }

    /**
     * Sets the value of the abTeeTimesData property.
     * 
     */
    public void setABTeeTimesData(boolean value) {
        this.abTeeTimesData = value;
    }

    /**
     * Gets the value of the asTeeStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASTeeStartDate() {
        return asTeeStartDate;
    }

    /**
     * Sets the value of the asTeeStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASTeeStartDate(String value) {
        this.asTeeStartDate = value;
    }

    /**
     * Gets the value of the asTeeEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASTeeEndDate() {
        return asTeeEndDate;
    }

    /**
     * Sets the value of the asTeeEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASTeeEndDate(String value) {
        this.asTeeEndDate = value;
    }

    /**
     * Gets the value of the asMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASMessage() {
        return asMessage;
    }

    /**
     * Sets the value of the asMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASMessage(String value) {
        this.asMessage = value;
    }

}
