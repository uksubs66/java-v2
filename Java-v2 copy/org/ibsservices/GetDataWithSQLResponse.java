
package org.ibsservices;

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
 *         &lt;element name="GetDataWithSQLResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getDataWithSQLResult"
})
@XmlRootElement(name = "GetDataWithSQLResponse")
public class GetDataWithSQLResponse {

    @XmlElement(name = "GetDataWithSQLResult")
    protected String getDataWithSQLResult;

    /**
     * Gets the value of the getDataWithSQLResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetDataWithSQLResult() {
        return getDataWithSQLResult;
    }

    /**
     * Sets the value of the getDataWithSQLResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetDataWithSQLResult(String value) {
        this.getDataWithSQLResult = value;
    }

}
