
package org.ibsservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFinancialData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFinancialData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinancialData" type="{http://ibsservices.org/}FinancialData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFinancialData", propOrder = {
    "financialData"
})
public class ArrayOfFinancialData {

    @XmlElement(name = "FinancialData", nillable = true)
    protected List<FinancialData> financialData;

    /**
     * Gets the value of the financialData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the financialData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFinancialData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FinancialData }
     * 
     * 
     */
    public List<FinancialData> getFinancialData() {
        if (financialData == null) {
            financialData = new ArrayList<FinancialData>();
        }
        return this.financialData;
    }

}
