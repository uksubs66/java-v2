
package org.ibsservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfAccountingCodeData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfAccountingCodeData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AccountingCodeData" type="{http://ibsservices.org/}AccountingCodeData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfAccountingCodeData", propOrder = {
    "accountingCodeData"
})
public class ArrayOfAccountingCodeData {

    @XmlElement(name = "AccountingCodeData", nillable = true)
    protected List<AccountingCodeData> accountingCodeData;

    /**
     * Gets the value of the accountingCodeData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accountingCodeData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccountingCodeData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountingCodeData }
     * 
     * 
     */
    public List<AccountingCodeData> getAccountingCodeData() {
        if (accountingCodeData == null) {
            accountingCodeData = new ArrayList<AccountingCodeData>();
        }
        return this.accountingCodeData;
    }

}
