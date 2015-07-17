
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SDRequestOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SDRequestOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RetrieveTeeTimeData" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="TeeTimeStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="TeeTimeEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="RetrieveSpaData" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="SpaStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SpaEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SDRequestOptions", propOrder = {
    "retrieveTeeTimeData",
    "teeTimeStartDate",
    "teeTimeEndDate",
    "retrieveSpaData",
    "spaStartDate",
    "spaEndDate"
})
public class SDRequestOptions {

    @XmlElement(name = "RetrieveTeeTimeData")
    protected boolean retrieveTeeTimeData;
    @XmlElement(name = "TeeTimeStartDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar teeTimeStartDate;
    @XmlElement(name = "TeeTimeEndDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar teeTimeEndDate;
    @XmlElement(name = "RetrieveSpaData")
    protected Object retrieveSpaData;
    @XmlElement(name = "SpaStartDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar spaStartDate;
    @XmlElement(name = "SpaEndDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar spaEndDate;

    /**
     * Gets the value of the retrieveTeeTimeData property.
     * 
     */
    public boolean isRetrieveTeeTimeData() {
        return retrieveTeeTimeData;
    }

    /**
     * Sets the value of the retrieveTeeTimeData property.
     * 
     */
    public void setRetrieveTeeTimeData(boolean value) {
        this.retrieveTeeTimeData = value;
    }

    /**
     * Gets the value of the teeTimeStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTeeTimeStartDate() {
        return teeTimeStartDate;
    }

    /**
     * Sets the value of the teeTimeStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTeeTimeStartDate(XMLGregorianCalendar value) {
        this.teeTimeStartDate = value;
    }

    /**
     * Gets the value of the teeTimeEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTeeTimeEndDate() {
        return teeTimeEndDate;
    }

    /**
     * Sets the value of the teeTimeEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTeeTimeEndDate(XMLGregorianCalendar value) {
        this.teeTimeEndDate = value;
    }

    /**
     * Gets the value of the retrieveSpaData property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getRetrieveSpaData() {
        return retrieveSpaData;
    }

    /**
     * Sets the value of the retrieveSpaData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setRetrieveSpaData(Object value) {
        this.retrieveSpaData = value;
    }

    /**
     * Gets the value of the spaStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSpaStartDate() {
        return spaStartDate;
    }

    /**
     * Sets the value of the spaStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSpaStartDate(XMLGregorianCalendar value) {
        this.spaStartDate = value;
    }

    /**
     * Gets the value of the spaEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSpaEndDate() {
        return spaEndDate;
    }

    /**
     * Sets the value of the spaEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSpaEndDate(XMLGregorianCalendar value) {
        this.spaEndDate = value;
    }

}
