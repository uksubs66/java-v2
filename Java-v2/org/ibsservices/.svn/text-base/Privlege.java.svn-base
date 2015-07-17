
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for privlege complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="privlege">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DateToBeginPosting" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="RateVariable" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "privlege", propOrder = {
    "code",
    "dateToBeginPosting",
    "rateVariable"
})
public class Privlege {

    protected String code;
    @XmlElement(name = "DateToBeginPosting", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateToBeginPosting;
    @XmlElement(name = "RateVariable")
    protected float rateVariable;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the dateToBeginPosting property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateToBeginPosting() {
        return dateToBeginPosting;
    }

    /**
     * Sets the value of the dateToBeginPosting property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateToBeginPosting(XMLGregorianCalendar value) {
        this.dateToBeginPosting = value;
    }

    /**
     * Gets the value of the rateVariable property.
     * 
     */
    public float getRateVariable() {
        return rateVariable;
    }

    /**
     * Sets the value of the rateVariable property.
     * 
     */
    public void setRateVariable(float value) {
        this.rateVariable = value;
    }

}
