
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Button complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Button">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BackColor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ForeColor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Button", propOrder = {
    "description",
    "backColor",
    "foreColor"
})
public class Button {

    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "BackColor")
    protected int backColor;
    @XmlElement(name = "ForeColor")
    protected int foreColor;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the backColor property.
     * 
     */
    public int getBackColor() {
        return backColor;
    }

    /**
     * Sets the value of the backColor property.
     * 
     */
    public void setBackColor(int value) {
        this.backColor = value;
    }

    /**
     * Gets the value of the foreColor property.
     * 
     */
    public int getForeColor() {
        return foreColor;
    }

    /**
     * Sets the value of the foreColor property.
     * 
     */
    public void setForeColor(int value) {
        this.foreColor = value;
    }

}
