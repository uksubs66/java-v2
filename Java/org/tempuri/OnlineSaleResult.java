
package org.tempuri;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OnlineSaleResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OnlineSaleResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Total" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Tax" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="SubTotal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="SaleNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReturnText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Result" type="{http://tempuri.org/}OnlinePaymentResultEnum"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OnlineSaleResult", propOrder = {
    "total",
    "tax",
    "subTotal",
    "saleNumber",
    "returnText",
    "result"
})
public class OnlineSaleResult {

    @XmlElement(name = "Total", required = true)
    protected BigDecimal total;
    @XmlElement(name = "Tax", required = true)
    protected BigDecimal tax;
    @XmlElement(name = "SubTotal", required = true)
    protected BigDecimal subTotal;
    @XmlElement(name = "SaleNumber")
    protected String saleNumber;
    @XmlElement(name = "ReturnText")
    protected String returnText;
    @XmlElement(name = "Result", required = true)
    protected OnlinePaymentResultEnum result;

    /**
     * Gets the value of the total property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotal(BigDecimal value) {
        this.total = value;
    }

    /**
     * Gets the value of the tax property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTax() {
        return tax;
    }

    /**
     * Sets the value of the tax property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTax(BigDecimal value) {
        this.tax = value;
    }

    /**
     * Gets the value of the subTotal property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSubTotal() {
        return subTotal;
    }

    /**
     * Sets the value of the subTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSubTotal(BigDecimal value) {
        this.subTotal = value;
    }

    /**
     * Gets the value of the saleNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSaleNumber() {
        return saleNumber;
    }

    /**
     * Sets the value of the saleNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSaleNumber(String value) {
        this.saleNumber = value;
    }

    /**
     * Gets the value of the returnText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnText() {
        return returnText;
    }

    /**
     * Sets the value of the returnText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnText(String value) {
        this.returnText = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link OnlinePaymentResultEnum }
     *     
     */
    public OnlinePaymentResultEnum getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnlinePaymentResultEnum }
     *     
     */
    public void setResult(OnlinePaymentResultEnum value) {
        this.result = value;
    }

}
