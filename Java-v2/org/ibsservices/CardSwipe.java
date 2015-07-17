
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CardSwipe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CardSwipe">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CardSwipeData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SwipeType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardSwipe", propOrder = {
    "cardSwipeData",
    "swipeType"
})
public class CardSwipe {

    @XmlElement(name = "CardSwipeData")
    protected String cardSwipeData;
    @XmlElement(name = "SwipeType")
    protected String swipeType;

    /**
     * Gets the value of the cardSwipeData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardSwipeData() {
        return cardSwipeData;
    }

    /**
     * Sets the value of the cardSwipeData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardSwipeData(String value) {
        this.cardSwipeData = value;
    }

    /**
     * Gets the value of the swipeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwipeType() {
        return swipeType;
    }

    /**
     * Sets the value of the swipeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwipeType(String value) {
        this.swipeType = value;
    }

}
