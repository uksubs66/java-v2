
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="acct" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="greenFeeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="greenFeeQty" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cartFeeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cartFeeQty" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="caddieFeeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="caddieFeeQty" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="otherProductCode1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="otherProduct1Qty" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="otherProduct2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="otherProduct2Qty" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "acct",
    "greenFeeCode",
    "greenFeeQty",
    "cartFeeCode",
    "cartFeeQty",
    "caddieFeeCode",
    "caddieFeeQty",
    "otherProductCode1",
    "otherProduct1Qty",
    "otherProduct2",
    "otherProduct2Qty"
})
@XmlRootElement(name = "AddSaleOnline")
public class AddSaleOnline {

    protected String acct;
    protected String greenFeeCode;
    protected int greenFeeQty;
    protected String cartFeeCode;
    protected int cartFeeQty;
    protected String caddieFeeCode;
    protected int caddieFeeQty;
    protected String otherProductCode1;
    protected int otherProduct1Qty;
    protected String otherProduct2;
    protected int otherProduct2Qty;

    /**
     * Gets the value of the acct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcct() {
        return acct;
    }

    /**
     * Sets the value of the acct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcct(String value) {
        this.acct = value;
    }

    /**
     * Gets the value of the greenFeeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGreenFeeCode() {
        return greenFeeCode;
    }

    /**
     * Sets the value of the greenFeeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGreenFeeCode(String value) {
        this.greenFeeCode = value;
    }

    /**
     * Gets the value of the greenFeeQty property.
     * 
     */
    public int getGreenFeeQty() {
        return greenFeeQty;
    }

    /**
     * Sets the value of the greenFeeQty property.
     * 
     */
    public void setGreenFeeQty(int value) {
        this.greenFeeQty = value;
    }

    /**
     * Gets the value of the cartFeeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCartFeeCode() {
        return cartFeeCode;
    }

    /**
     * Sets the value of the cartFeeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCartFeeCode(String value) {
        this.cartFeeCode = value;
    }

    /**
     * Gets the value of the cartFeeQty property.
     * 
     */
    public int getCartFeeQty() {
        return cartFeeQty;
    }

    /**
     * Sets the value of the cartFeeQty property.
     * 
     */
    public void setCartFeeQty(int value) {
        this.cartFeeQty = value;
    }

    /**
     * Gets the value of the caddieFeeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaddieFeeCode() {
        return caddieFeeCode;
    }

    /**
     * Sets the value of the caddieFeeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaddieFeeCode(String value) {
        this.caddieFeeCode = value;
    }

    /**
     * Gets the value of the caddieFeeQty property.
     * 
     */
    public int getCaddieFeeQty() {
        return caddieFeeQty;
    }

    /**
     * Sets the value of the caddieFeeQty property.
     * 
     */
    public void setCaddieFeeQty(int value) {
        this.caddieFeeQty = value;
    }

    /**
     * Gets the value of the otherProductCode1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherProductCode1() {
        return otherProductCode1;
    }

    /**
     * Sets the value of the otherProductCode1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherProductCode1(String value) {
        this.otherProductCode1 = value;
    }

    /**
     * Gets the value of the otherProduct1Qty property.
     * 
     */
    public int getOtherProduct1Qty() {
        return otherProduct1Qty;
    }

    /**
     * Sets the value of the otherProduct1Qty property.
     * 
     */
    public void setOtherProduct1Qty(int value) {
        this.otherProduct1Qty = value;
    }

    /**
     * Gets the value of the otherProduct2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherProduct2() {
        return otherProduct2;
    }

    /**
     * Sets the value of the otherProduct2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherProduct2(String value) {
        this.otherProduct2 = value;
    }

    /**
     * Gets the value of the otherProduct2Qty property.
     * 
     */
    public int getOtherProduct2Qty() {
        return otherProduct2Qty;
    }

    /**
     * Sets the value of the otherProduct2Qty property.
     * 
     */
    public void setOtherProduct2Qty(int value) {
        this.otherProduct2Qty = value;
    }

}
