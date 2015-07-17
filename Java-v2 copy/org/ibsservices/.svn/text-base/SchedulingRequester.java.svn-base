
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SchedulingRequester complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SchedulingRequester">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ibsservices.org/}MemberRequester">
 *       &lt;sequence>
 *         &lt;element name="RequestOptions" type="{http://ibsservices.org/}SDRequestOptions" minOccurs="0"/>
 *         &lt;element name="IR" type="{http://ibsservices.org/}InventoryRequester" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SchedulingRequester", propOrder = {
    "requestOptions",
    "ir"
})
public class SchedulingRequester
    extends MemberRequester
{

    @XmlElement(name = "RequestOptions")
    protected SDRequestOptions requestOptions;
    @XmlElement(name = "IR")
    protected InventoryRequester ir;

    /**
     * Gets the value of the requestOptions property.
     * 
     * @return
     *     possible object is
     *     {@link SDRequestOptions }
     *     
     */
    public SDRequestOptions getRequestOptions() {
        return requestOptions;
    }

    /**
     * Sets the value of the requestOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link SDRequestOptions }
     *     
     */
    public void setRequestOptions(SDRequestOptions value) {
        this.requestOptions = value;
    }

    /**
     * Gets the value of the ir property.
     * 
     * @return
     *     possible object is
     *     {@link InventoryRequester }
     *     
     */
    public InventoryRequester getIR() {
        return ir;
    }

    /**
     * Sets the value of the ir property.
     * 
     * @param value
     *     allowed object is
     *     {@link InventoryRequester }
     *     
     */
    public void setIR(InventoryRequester value) {
        this.ir = value;
    }

}
