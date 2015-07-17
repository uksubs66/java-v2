
package org.ibsservices;

import java.math.BigDecimal;
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
 *         &lt;element name="GetMemberBalanceResult" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
    "getMemberBalanceResult",
    "asMessage"
})
@XmlRootElement(name = "GetMemberBalanceResponse")
public class GetMemberBalanceResponse {

    @XmlElement(name = "GetMemberBalanceResult", required = true)
    protected BigDecimal getMemberBalanceResult;
    @XmlElement(name = "a_sMessage")
    protected String asMessage;

    /**
     * Gets the value of the getMemberBalanceResult property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getGetMemberBalanceResult() {
        return getMemberBalanceResult;
    }

    /**
     * Sets the value of the getMemberBalanceResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setGetMemberBalanceResult(BigDecimal value) {
        this.getMemberBalanceResult = value;
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
