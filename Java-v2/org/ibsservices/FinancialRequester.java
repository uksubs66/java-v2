
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FinancialRequester complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialRequester">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ibsservices.org/}Request">
 *       &lt;sequence>
 *         &lt;element name="RequestOptions" type="{http://ibsservices.org/}FRequestOptions" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialRequester", propOrder = {
    "requestOptions"
})
public class FinancialRequester
    extends Request
{

    @XmlElement(name = "RequestOptions")
    protected FRequestOptions requestOptions;

    /**
     * Gets the value of the requestOptions property.
     * 
     * @return
     *     possible object is
     *     {@link FRequestOptions }
     *     
     */
    public FRequestOptions getRequestOptions() {
        return requestOptions;
    }

    /**
     * Sets the value of the requestOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link FRequestOptions }
     *     
     */
    public void setRequestOptions(FRequestOptions value) {
        this.requestOptions = value;
    }

}
