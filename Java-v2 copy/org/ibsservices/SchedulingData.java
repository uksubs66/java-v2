
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SchedulingData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SchedulingData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ibsservices.org/}MemberData">
 *       &lt;sequence>
 *         &lt;element name="CardSwipes" type="{http://ibsservices.org/}ArrayOfCardSwipe" minOccurs="0"/>
 *         &lt;element name="Financial" type="{http://ibsservices.org/}SchedulingFinancial" minOccurs="0"/>
 *         &lt;element name="TeeTimes" type="{http://ibsservices.org/}ArrayOfReservationInformation" minOccurs="0"/>
 *         &lt;element name="Spa" type="{http://ibsservices.org/}ArrayOfSpaScheduling" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SchedulingData", propOrder = {
    "cardSwipes",
    "financial",
    "teeTimes",
    "spa"
})
public class SchedulingData
    extends MemberData
{

    @XmlElement(name = "CardSwipes")
    protected ArrayOfCardSwipe cardSwipes;
    @XmlElement(name = "Financial")
    protected SchedulingFinancial financial;
    @XmlElement(name = "TeeTimes")
    protected ArrayOfReservationInformation teeTimes;
    @XmlElement(name = "Spa")
    protected ArrayOfSpaScheduling spa;

    /**
     * Gets the value of the cardSwipes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCardSwipe }
     *     
     */
    public ArrayOfCardSwipe getCardSwipes() {
        return cardSwipes;
    }

    /**
     * Sets the value of the cardSwipes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCardSwipe }
     *     
     */
    public void setCardSwipes(ArrayOfCardSwipe value) {
        this.cardSwipes = value;
    }

    /**
     * Gets the value of the financial property.
     * 
     * @return
     *     possible object is
     *     {@link SchedulingFinancial }
     *     
     */
    public SchedulingFinancial getFinancial() {
        return financial;
    }

    /**
     * Sets the value of the financial property.
     * 
     * @param value
     *     allowed object is
     *     {@link SchedulingFinancial }
     *     
     */
    public void setFinancial(SchedulingFinancial value) {
        this.financial = value;
    }

    /**
     * Gets the value of the teeTimes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfReservationInformation }
     *     
     */
    public ArrayOfReservationInformation getTeeTimes() {
        return teeTimes;
    }

    /**
     * Sets the value of the teeTimes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfReservationInformation }
     *     
     */
    public void setTeeTimes(ArrayOfReservationInformation value) {
        this.teeTimes = value;
    }

    /**
     * Gets the value of the spa property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSpaScheduling }
     *     
     */
    public ArrayOfSpaScheduling getSpa() {
        return spa;
    }

    /**
     * Sets the value of the spa property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSpaScheduling }
     *     
     */
    public void setSpa(ArrayOfSpaScheduling value) {
        this.spa = value;
    }

}
