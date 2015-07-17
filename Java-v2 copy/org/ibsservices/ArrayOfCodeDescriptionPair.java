
package org.ibsservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCodeDescriptionPair complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCodeDescriptionPair">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodeDescriptionPair" type="{http://ibsservices.org/}CodeDescriptionPair" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCodeDescriptionPair", propOrder = {
    "codeDescriptionPair"
})
public class ArrayOfCodeDescriptionPair {

    @XmlElement(name = "CodeDescriptionPair", nillable = true)
    protected List<CodeDescriptionPair> codeDescriptionPair;

    /**
     * Gets the value of the codeDescriptionPair property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the codeDescriptionPair property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCodeDescriptionPair().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CodeDescriptionPair }
     * 
     * 
     */
    public List<CodeDescriptionPair> getCodeDescriptionPair() {
        if (codeDescriptionPair == null) {
            codeDescriptionPair = new ArrayList<CodeDescriptionPair>();
        }
        return this.codeDescriptionPair;
    }

}
